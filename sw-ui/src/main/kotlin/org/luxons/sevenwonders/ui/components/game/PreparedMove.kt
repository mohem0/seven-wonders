package org.luxons.sevenwonders.ui.components.game

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.DIV
import org.luxons.sevenwonders.model.MoveType
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.ui.components.GlobalStyles
import react.RBuilder
import styled.StyledDOMBuilder
import styled.css
import styled.styledDiv
import styled.styledImg

fun RBuilder.preparedMove(
    card: HandCard,
    move: PlayerMove,
    unprepareMove: () -> Unit,
    block: StyledDOMBuilder<DIV>.() -> Unit,
) {
    styledDiv {
        block()
        cardImage(card) {
            if (move.type == MoveType.DISCARD || move.type == MoveType.UPGRADE_WONDER) {
                css { +GameStyles.dimmedCard }
            }
        }
        if (move.type == MoveType.DISCARD) {
            discardText()
        }
        if (move.type == MoveType.UPGRADE_WONDER) {
            upgradeWonderSymbol()
        }
        styledDiv {
            css {
                position = Position.absolute
                top = 0.px
                right = 0.px
            }
            bpButton(
                icon = "cross",
                title = "Cancel prepared move",
                small = true,
                intent = Intent.DANGER,
                onClick = { unprepareMove() },
            )
        }
    }
}

private fun StyledDOMBuilder<DIV>.discardText() {
    styledDiv {
        css {
            +GlobalStyles.centerInParent
            +GameStyles.discardMoveText
        }
        +"DISCARD"
    }
}

private fun StyledDOMBuilder<DIV>.upgradeWonderSymbol() {
    styledImg(src = "/images/wonder-upgrade-bright.png") {
        css {
            width = 8.rem
            position = Position.absolute
            left = 10.pct
            top = 50.pct
            transform {
                translate((-50).pct, (-50).pct)
            }
        }
    }
}
