import type { ApiPlayerTurnInfo, ApiPreparedCard, ApiTable } from '../../api/model';

export const types = {
  REQUEST_SAY_READY: 'GAME/REQUEST_SAY_READY',
  PLAYER_READY_RECEIVED: 'GAME/PLAYER_READY_RECEIVED',
  TABLE_UPDATE_RECEIVED: 'GAME/TABLE_UPDATE_RECEIVED',
  PREPARED_CARD_RECEIVED: 'GAME/PREPARED_CARD_RECEIVED',
  TURN_INFO_RECEIVED: 'GAME/TURN_INFO_RECEIVED',
};

export type SayReadyAction = { type: 'GAME/REQUEST_SAY_READY' };
export type PlayerReadyEvent = { type: 'GAME/PLAYER_READY_RECEIVED', username: string };
export type TableUpdateEvent = { type: 'GAME/TABLE_UPDATE_RECEIVED', table: ApiTable };
export type PreparedCardEvent = { type: 'GAME/PREPARED_CARD_RECEIVED', card: ApiPreparedCard };
export type TurnInfoEvent = { type: 'GAME/TURN_INFO_RECEIVED', turnInfo: ApiPlayerTurnInfo };

export type GameAction = SayReadyAction | PlayerReadyEvent | TableUpdateEvent | PreparedCardEvent | TurnInfoEvent;

export const actions = {
  sayReady: () => ({ type: types.REQUEST_SAY_READY }),
  receivePlayerReady: (username: string) => ({ type: types.PLAYER_READY_RECEIVED, username }),
  receiveTableUpdate: (table: ApiTable) => ({ type: types.TABLE_UPDATE_RECEIVED, table }),
  receivePreparedCard: (card: ApiPreparedCard) => ({ type: types.PREPARED_CARD_RECEIVED, card }),
  receiveTurnInfo: (turnInfo: ApiPlayerTurnInfo) => ({ type: types.TURN_INFO_RECEIVED, turnInfo }),
};
