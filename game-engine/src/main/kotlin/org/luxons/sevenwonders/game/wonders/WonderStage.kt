package org.luxons.sevenwonders.game.wonders

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.Requirements
import org.luxons.sevenwonders.game.effects.Effect
import org.luxons.sevenwonders.game.resources.ResourceTransactions

internal class WonderStage(val requirements: Requirements, val effects: List<Effect>) {

    var cardBack: CardBack? = null
        private set

    val isBuilt: Boolean
        get() = cardBack != null

    fun isBuildable(board: Board, boughtResources: ResourceTransactions): Boolean {
        return requirements.areMetWithHelpBy(board, boughtResources)
    }

    fun placeCard(cardBack: CardBack) {
        this.cardBack = cardBack
    }

    fun activate(player: Player, boughtResources: ResourceTransactions) {
        effects.forEach { it.applyTo(player) }
        requirements.pay(player, boughtResources)
    }
}
