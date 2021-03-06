package org.luxons.sevenwonders.ui.redux

import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.api.State
import org.luxons.sevenwonders.model.cards.CardBack
import org.luxons.sevenwonders.model.cards.HandCard
import redux.RAction

data class SwState(
    val connectedPlayer: ConnectedPlayer? = null,
    // they must be by ID to support updates to a sublist
    val gamesById: Map<Long, LobbyDTO> = emptyMap(),
    val currentLobby: LobbyDTO? = null,
    val gameState: GameState? = null,
) {
    val currentPlayer: PlayerDTO? = (gameState?.players ?: currentLobby?.players)?.first {
        it.username == connectedPlayer?.username
    }
    val games: List<LobbyDTO> = gamesById.values.toList()
}

data class GameState(
    val id: Long,
    val players: List<PlayerDTO>,
    val turnInfo: PlayerTurnInfo?,
    val preparedCardsByUsername: Map<String, CardBack?> = emptyMap(),
    val currentPreparedMove: PlayerMove? = null,
) {
    val currentPreparedCard: HandCard?
        get() = turnInfo?.hand?.firstOrNull { it.name == currentPreparedMove?.cardName }
}

fun rootReducer(state: SwState, action: RAction): SwState = state.copy(
    gamesById = gamesReducer(state.gamesById, action),
    connectedPlayer = currentPlayerReducer(state.connectedPlayer, action),
    currentLobby = currentLobbyReducer(state.currentLobby, action),
    gameState = gameStateReducer(state.gameState, action),
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
        l.copy(players = l.players.map { p -> if (p.username == action.username) p.copy(isReady = true) else p })
    }
    else -> currentLobby
}

private fun gameStateReducer(gameState: GameState?, action: RAction): GameState? = when (action) {
    is EnterGameAction -> GameState(
        id = action.lobby.id,
        players = action.lobby.players,
        turnInfo = action.turnInfo,
    )
    is PreparedMoveEvent -> gameState?.copy(currentPreparedMove = action.move)
    is RequestUnprepareMove -> gameState?.copy(currentPreparedMove = null)
    is PreparedCardEvent -> gameState?.copy(
        preparedCardsByUsername = gameState.preparedCardsByUsername + (action.card.username to action.card.cardBack),
    )
    is PlayerReadyEvent -> gameState?.copy(
        players = gameState.players.map { p ->
            if (p.username == action.username) p.copy(isReady = true) else p
        },
    )
    is TurnInfoEvent -> gameState?.copy(
        players = gameState.players.map { p -> p.copy(isReady = false) },
        turnInfo = action.turnInfo,
        currentPreparedMove = null,
    )
    else -> gameState
}
