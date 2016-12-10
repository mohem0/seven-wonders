package org.luxons.sevenwonders.game.effects;

public enum SpecialAction {
    /**
     * The player can play the last card of each age instead of discarding it. This card can be played by paying its
     * cost, discarded to gain 3 coins or used in the construction of his or her Wonder.
     */
    PLAY_LAST_CARD,

    /**
     * Once per age, a player can construct a building from his or her hand for free.
     */
    ONE_FREE,

    /**
     * The player can look at all cards discarded since the beginning of the game, pick one and build it for free.
     */
    PLAY_DISCARDED,

    /**
     * The player can, at the end of the game, “copy” a Guild of his or her choice (purple card), built by one of his or
     * her two neighboring cities.
     */
    COPY_GUILD;
}
