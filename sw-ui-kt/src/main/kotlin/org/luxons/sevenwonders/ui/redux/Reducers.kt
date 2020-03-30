package org.luxons.sevenwonders.ui.redux

import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.api.State
import org.luxons.sevenwonders.model.cards.PreparedCard
import redux.RAction

data class SwState(
    val connectedPlayer: ConnectedPlayer? = null,
    // they must be by ID to support updates to a sublist
    val gamesById: Map<Long, LobbyDTO> = emptyMap(),
    val currentLobby: LobbyDTO? = null,
    val gameState: GameState? = null
) {
    val currentPlayer: PlayerDTO? = (gameState?.players ?: currentLobby?.players)?.first {
        it.username == connectedPlayer?.username
    }
    val games: List<LobbyDTO> = gamesById.values.toList()
}

data class GameState(
    val id: Long,
    val players: List<PlayerDTO>,
    val preparedCardsByUsername: Map<String, PreparedCard>,
    val turnInfo: PlayerTurnInfo?
)

fun rootReducer(state: SwState, action: RAction): SwState = state.copy(
    gamesById = gamesReducer(state.gamesById, action),
    connectedPlayer = currentPlayerReducer(state.connectedPlayer, action),
    currentLobby = currentLobbyReducer(state.currentLobby, action),
    gameState = currentTurnInfoReducer(state.gameState, action)
)

private fun gamesReducer(games: Map<Long, LobbyDTO>, action: RAction): Map<Long, LobbyDTO> = when (action) {
    is UpdateGameListAction -> (games + action.games.associateBy { it.id }).filterValues { it.state != State.FINISHED }
    else -> games
}

private fun currentPlayerReducer(currentPlayer: ConnectedPlayer?, action: RAction): ConnectedPlayer? = when (action) {
    is SetCurrentPlayerAction -> action.player
    else -> currentPlayer
}

private fun currentLobbyReducer(currentLobby: LobbyDTO?, action: RAction): LobbyDTO? = when (action) {
    is EnterLobbyAction -> action.lobby
    is UpdateLobbyAction -> action.lobby
    is PlayerReadyEvent -> currentLobby?.let { l ->
        l.copy(players = l.players.map { p ->
            if (p.username == action.username) p.copy(isReady = true) else p
        })
    }
    else -> currentLobby
}

private fun currentTurnInfoReducer(gameState: GameState?, action: RAction): GameState? = when (action) {
    is EnterGameAction -> GameState(
        id = action.lobby.id,
        players = action.lobby.players,
        preparedCardsByUsername = emptyMap(),
        turnInfo = action.turnInfo
    )
    is PreparedCardEvent -> gameState?.copy(
        preparedCardsByUsername = gameState.preparedCardsByUsername + (action.card.player.username to action.card)
    )
    is PlayerReadyEvent -> gameState?.copy(players = gameState.players.map { p ->
        if (p.username == action.username) p.copy(isReady = true) else p
    })
    is TurnInfoEvent -> gameState?.copy(
        players = gameState.players.map { p -> p.copy(isReady = false) },
        turnInfo = action.turnInfo
    )
    else -> gameState
}
