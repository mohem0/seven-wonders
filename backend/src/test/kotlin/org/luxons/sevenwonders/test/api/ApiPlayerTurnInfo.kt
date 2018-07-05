package org.luxons.sevenwonders.test.api

import java.util.Objects

import org.luxons.sevenwonders.game.api.Action

class ApiPlayerTurnInfo {

    var playerIndex: Int = 0

    var table: ApiTable? = null

    var currentAge: Int = 0

    var action: Action? = null

    var hand: List<ApiHandCard>? = null

    var neighbourGuildCards: List<ApiCard>? = null

    var message: String? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val that = o as ApiPlayerTurnInfo?
        return (playerIndex == that!!.playerIndex && currentAge == that.currentAge && table == that.table && action === that.action && hand == that.hand && neighbourGuildCards == that.neighbourGuildCards && message == that.message)
    }

    override fun hashCode(): Int {
        return Objects.hash(playerIndex, table, currentAge, action, hand, neighbourGuildCards, message)
    }
}
