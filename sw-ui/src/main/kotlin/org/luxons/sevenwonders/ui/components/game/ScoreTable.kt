package org.luxons.sevenwonders.ui.components.game

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import com.palantir.blueprintjs.bpCard
import com.palantir.blueprintjs.bpHtmlTable
import com.palantir.blueprintjs.bpIcon
import com.palantir.blueprintjs.bpOverlay
import com.palantir.blueprintjs.bpTag
import kotlinx.css.*
import kotlinx.html.TD
import kotlinx.html.TH
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.score.ScoreBoard
import org.luxons.sevenwonders.model.score.ScoreCategory
import org.luxons.sevenwonders.ui.components.GlobalStyles
import react.RBuilder
import react.dom.*
import styled.css
import styled.getClassName
import styled.inlineStyles
import styled.styledDiv
import styled.styledH1
import styled.styledTd

fun RBuilder.scoreTableOverlay(scoreBoard: ScoreBoard, players: List<PlayerDTO>, leaveGame: () -> Unit) {
    bpOverlay(isOpen = true) {
        bpCard {
            attrs {
                val fixedCenterClass = GlobalStyles.getClassName { it::fixedCenter }
                val scoreBoardClass = GameStyles.getClassName { it::scoreBoard }
                className = "$fixedCenterClass $scoreBoardClass"
            }
            styledDiv {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = Align.center
                    +GameStyles.scoreBoard // loads the styles so that they can be picked up by bpCard
                }
                styledH1 {
                    css {
                        marginTop = 0.px
                    }
                    +"Score Board"
                }
                scoreTable(scoreBoard, players)
                styledDiv {
                    css {
                        marginTop = 1.rem
                    }
                    bpButton(intent = Intent.WARNING, rightIcon = "log-out", large = true, onClick = { leaveGame() }) {
                        +"LEAVE"
                    }
                }
            }
        }
    }
}

private fun RBuilder.scoreTable(scoreBoard: ScoreBoard, players: List<PlayerDTO>) {
    bpHtmlTable(bordered = false, interactive = true) {
        thead {
            tr {
                centeredTh { +"Rank" }
                centeredTh {
                    attrs { colSpan = "2" }
                    +"Player"
                }
                centeredTh { +"Score" }
                ScoreCategory.values().forEach {
                    centeredTh { +it.title }
                }
            }
        }
        tbody {
            scoreBoard.scores.forEachIndexed { index, score ->
                val player = players[score.playerIndex]
                tr {
                    centeredTd { +"${index + 1}" }
                    centeredTd { bpIcon(player.icon?.name ?: "user", size = 25) }
                    styledTd {
                        inlineStyles {
                            verticalAlign = VerticalAlign.middle
                        }
                        +player.displayName
                    }
                    centeredTd {
                        bpTag(large = true, round = true, minimal = true) {
                            +"${score.totalPoints}"
                        }
                    }
                    ScoreCategory.values().forEach { cat ->
                        centeredTd {
                            bpTag(intent = cat.intent, large = true, round = true, icon = cat.icon, fill = true) {
                                +"${score.pointsByCategory[cat]}"
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun RBuilder.centeredTh(block: RDOMBuilder<TH>.() -> Unit) {
    th {
        // inline styles necessary to overcome blueprintJS overrides
        inlineStyles {
            textAlign = TextAlign.center
            verticalAlign = VerticalAlign.middle
        }
        block()
    }
}

private fun RBuilder.centeredTd(block: RDOMBuilder<TD>.() -> Unit) {
    td {
        // inline styles necessary to overcome blueprintJS overrides
        inlineStyles {
            textAlign = TextAlign.center
            verticalAlign = VerticalAlign.middle
        }
        block()
    }
}

private val ScoreCategory.intent: Intent
    get() = when (this) {
        ScoreCategory.CIVIL -> Intent.PRIMARY
        ScoreCategory.SCIENCE -> Intent.SUCCESS
        ScoreCategory.MILITARY -> Intent.DANGER
        ScoreCategory.TRADE -> Intent.WARNING
        ScoreCategory.GUILD -> Intent.NONE
        ScoreCategory.WONDER -> Intent.NONE
        ScoreCategory.GOLD -> Intent.WARNING
    }

private val ScoreCategory.icon: String
    get() = when (this) {
        ScoreCategory.CIVIL -> "office"
        ScoreCategory.SCIENCE -> "lab-test"
        ScoreCategory.MILITARY -> "cut"
        ScoreCategory.TRADE -> "swap-horizontal"
        ScoreCategory.GUILD -> "clean" // stars
        ScoreCategory.WONDER -> "symbol-triangle-up"
        ScoreCategory.GOLD -> "dollar"
    }

// Potentially useful emojis:
// Greek temple:  🏛
// Cog (science): ⚙️
// Swords (war):  ⚔️
// Gold bag:      💰
