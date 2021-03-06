package org.luxons.sevenwonders.server.controllers

import org.hildan.livedoc.core.annotations.Api
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.api.actions.ChooseNameAction
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import java.security.Principal

/**
 * Handles actions in the homepage of the game.
 */
@Api(name = "Home")
@Controller
class HomeController(
    private val playerRepository: PlayerRepository,
) {

    /**
     * Creates/updates the player's name (for the user's session).
     *
     * @param action the action to choose the name of the player
     * @param principal the connected user's information
     *
     * @return the created [PlayerDTO] object
     */
    @MessageMapping("/chooseName")
    @SendToUser("/queue/nameChoice")
    fun chooseName(@Validated action: ChooseNameAction, principal: Principal): ConnectedPlayer {
        val username = principal.name
        val player = playerRepository.createOrUpdate(username, action.playerName, action.icon)

        logger.info("Player '{}' chose the name '{}'", username, player.displayName)
        return ConnectedPlayer(username, player.displayName, player.icon)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HomeController::class.java)
    }
}
