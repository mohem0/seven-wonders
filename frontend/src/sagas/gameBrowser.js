// @flow
import { normalize } from 'normalizr';
import { push } from 'react-router-redux';
import type { SagaIterator } from 'redux-saga';
import { eventChannel } from 'redux-saga';
import { all, apply, call, put, take } from 'redux-saga/effects';
import type { SevenWondersSession } from '../api/sevenWondersApi';
import { actions as gameActions } from '../redux/actions/lobby';
import { types } from '../redux/actions/lobby';
import { actions as playerActions } from '../redux/actions/players';
import { game as gameSchema, gameList as gameListSchema } from '../schemas/games';

function* watchGames(session: SevenWondersSession): SagaIterator {
  const gamesChannel = yield eventChannel(session.watchGames());
  try {
    while (true) {
      const gameList = yield take(gamesChannel);
      const normGameList = normalize(gameList, gameListSchema);
      // for an empty game array, there is no players/games entity maps
      yield put(playerActions.updatePlayers(normGameList.entities.players || {}));
      yield put(gameActions.updateGames(normGameList.entities.games || {}));
    }
  } finally {
    yield apply(gamesChannel, gamesChannel.close);
  }
}

function* watchLobbyJoined(session: SevenWondersSession): SagaIterator {
  const joinedLobbyChannel = yield eventChannel(session.watchLobbyJoined());
  try {
    const joinedLobby = yield take(joinedLobbyChannel);
    const normalized = normalize(joinedLobby, gameSchema);
    const gameId = normalized.result;
    yield put(playerActions.updatePlayers(normalized.entities.players));
    yield put(gameActions.updateGames(normalized.entities.games));
    yield put(gameActions.enterLobby(gameId));
    yield put(push(`/lobby/${gameId}`));
  } finally {
    yield apply(joinedLobbyChannel, joinedLobbyChannel.close);
  }
}

function* createGame(session: SevenWondersSession): SagaIterator {
  while (true) {
    const { gameName } = yield take(types.REQUEST_CREATE_GAME);
    // $FlowFixMe
    yield apply(session, session.createGame, [gameName]);
  }
}

function* joinGame(session: SevenWondersSession): SagaIterator {
  while (true) {
    const { gameId } = yield take(types.REQUEST_JOIN_GAME);
    // $FlowFixMe
    yield apply(session, session.joinGame, [gameId]);
  }
}

export function* gameBrowserSaga(session: SevenWondersSession): SagaIterator {
  yield all([
    call(watchGames, session),
    call(watchLobbyJoined, session),
    call(createGame, session),
    call(joinGame, session),
  ]);
}
