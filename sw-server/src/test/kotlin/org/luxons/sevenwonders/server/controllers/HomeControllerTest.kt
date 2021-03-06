package org.luxons.sevenwonders.server.controllers

import org.junit.Test
import org.luxons.sevenwonders.model.api.actions.ChooseNameAction
import org.luxons.sevenwonders.model.api.actions.Icon
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import kotlin.test.assertEquals

class HomeControllerTest {

    @Test
    fun chooseName() {
        val playerRepository = PlayerRepository()
        val homeController = HomeController(playerRepository)

        val action = ChooseNameAction("Test User", Icon("person"))
        val principal = TestPrincipal("testuser")

        val player = homeController.chooseName(action, principal)

        assertEquals("testuser", player.username)
        assertEquals("Test User", player.displayName)
        assertEquals("person", player.icon?.name)
    }
}
