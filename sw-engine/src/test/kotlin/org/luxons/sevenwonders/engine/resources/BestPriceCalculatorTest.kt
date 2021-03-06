package org.luxons.sevenwonders.engine.resources

import org.junit.Test
import org.luxons.sevenwonders.engine.SimplePlayer
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.engine.test.createTransaction
import org.luxons.sevenwonders.engine.test.createTransactions
import org.luxons.sevenwonders.engine.test.testBoard
import org.luxons.sevenwonders.engine.test.testTable
import org.luxons.sevenwonders.model.resources.Provider.LEFT_PLAYER
import org.luxons.sevenwonders.model.resources.Provider.RIGHT_PLAYER
import org.luxons.sevenwonders.model.resources.ResourceTransactions
import org.luxons.sevenwonders.model.resources.ResourceType.CLAY
import org.luxons.sevenwonders.model.resources.ResourceType.GLASS
import org.luxons.sevenwonders.model.resources.ResourceType.ORE
import org.luxons.sevenwonders.model.resources.ResourceType.STONE
import org.luxons.sevenwonders.model.resources.ResourceType.WOOD
import org.luxons.sevenwonders.model.resources.noTransactions
import kotlin.test.assertEquals

class BestPriceCalculatorTest {

    private fun solutions(price: Int, vararg resourceTransactions: ResourceTransactions) =
        TransactionPlan(price, setOf(*resourceTransactions))

    @Test
    fun bestPrice_0forEmptyResources() {
        val table = testTable(3)
        val player0 = SimplePlayer(0, table)
        val emptyResources = emptyResources()
        val emptyTransactions = noTransactions()
        assertEquals(solutions(0, emptyTransactions), bestSolution(emptyResources, player0))
    }

    @Test
    fun bestPrice_fixedResources_defaultCost() {
        val left = testBoard(STONE)
        val main = testBoard(STONE)
        val right = testBoard(WOOD)
        val table = Table(listOf(main, right, left))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(STONE, STONE)

        val stoneLeftSingle = createTransaction(LEFT_PLAYER, STONE)
        val stoneRightSingle = createTransaction(RIGHT_PLAYER, STONE)

        val stoneLeft = createTransactions(stoneLeftSingle)
        val stoneRight = createTransactions(stoneRightSingle)
        val stoneLeftAndRight = createTransactions(stoneLeftSingle, stoneRightSingle)

        assertEquals(solutions(2, stoneLeft), bestSolution(resources, player0))
        assertEquals(solutions(4, stoneLeftAndRight), bestSolution(resources, player1))
        assertEquals(solutions(2, stoneRight), bestSolution(resources, player2))
    }

    @Test
    fun bestPrice_fixedResources_overridenCost() {
        val main = testBoard(STONE)
        main.tradingRules.setCost(WOOD, RIGHT_PLAYER, 1)

        val left = testBoard(WOOD)
        val right = testBoard(WOOD)
        val opposite = testBoard(GLASS)
        val table = Table(listOf(main, right, opposite, left))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)
        val player3 = SimplePlayer(3, table)

        val resources = resourcesOf(WOOD)

        val woodLeft = createTransactions(LEFT_PLAYER, WOOD)
        val woodRight = createTransactions(RIGHT_PLAYER, WOOD)

        assertEquals(solutions(1, woodRight), bestSolution(resources, player0))
        assertEquals(solutions(0, noTransactions()), bestSolution(resources, player1))
        assertEquals(solutions(2, woodLeft, woodRight), bestSolution(resources, player2))
        assertEquals(solutions(0, noTransactions()), bestSolution(resources, player3))
    }

    @Test
    fun bestPrice_mixedResources_overridenCost() {
        val left = testBoard(WOOD)

        val main = testBoard(STONE)
        main.tradingRules.setCost(WOOD, RIGHT_PLAYER, 1)

        val right = testBoard(ORE)
        right.production.addChoice(WOOD, CLAY)
        right.publicProduction.addChoice(WOOD, CLAY)

        val table = Table(listOf(main, right, left))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(WOOD)
        val woodRight = createTransactions(RIGHT_PLAYER, WOOD)

        assertEquals(solutions(1, woodRight), bestSolution(resources, player0))
        assertEquals(solutions(0, noTransactions()), bestSolution(resources, player1))
        assertEquals(solutions(0, noTransactions()), bestSolution(resources, player2))
    }

    @Test
    fun bestPrice_chooseCheapest() {
        val left = testBoard(WOOD)

        val main = testBoard(WOOD)
        main.production.addChoice(CLAY, ORE)
        main.tradingRules.setCost(CLAY, RIGHT_PLAYER, 1)

        val right = testBoard(WOOD)
        right.production.addFixedResource(ORE, 1)
        right.production.addFixedResource(CLAY, 1)
        right.publicProduction.addFixedResource(ORE, 1)
        right.publicProduction.addFixedResource(CLAY, 1)

        val table = Table(listOf(main, right, left))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(ORE, CLAY)
        val oreAndClayLeft = createTransactions(LEFT_PLAYER, ORE, CLAY)
        val clayRight = createTransactions(RIGHT_PLAYER, CLAY)

        assertEquals(solutions(1, clayRight), bestSolution(resources, player0))
        assertEquals(solutions(0, noTransactions()), bestSolution(resources, player1))
        assertEquals(solutions(4, oreAndClayLeft), bestSolution(resources, player2))
    }
}
