package betpool

import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.Date

class MatchSpec : Spek({
    fun createMatch(): Match {
        val odds = Odds(
                mapOf("oddsId1" to Competitor(name = "Ronnie", odds = 150), "oddsId2" to Competitor(name = "Selby", odds = 200))
        )
        return Match("id", odds, Date())
    }

    it("addBet adds a bet") {
        var match = createMatch()
        match.addBet(playerId = "p1", oddsId = "oddsId1")
        match.getBets() shouldEqual mapOf("p1" to "oddsId1")
    }

    it("addBet throws if player has already bet on the match") {
        var match = createMatch()
        match.addBet(playerId = "p1", oddsId = "oddsId1")
        val func = { match.addBet(playerId = "p1", oddsId = "oddsId1") }
        func shouldThrow IllegalArgumentException::class
    }

    it("addBet throws if no oddsID found on the match") {
        var match = createMatch()
        val func = { match.addBet(playerId = "p1", oddsId = "oddsId3") }
        func shouldThrow IllegalArgumentException::class
    }

    it("removeBet removes a bet") {
        var match = createMatch()
        match.addBet(playerId = "p1", oddsId = "oddsId1")
        match.addBet(playerId = "p2", oddsId = "oddsId2")
        match.removeBet(playerId = "p1")
        match.getBets() shouldEqual mapOf("p2" to "oddsId2")
    }

    it("removeBet throws if the player doesn't have a bet") {
        var match = createMatch()
        val func = { match.removeBet(playerId = "p1") }
        func shouldThrow IllegalArgumentException::class
    }

    it("start sets the pool for the match") {
        var match = createMatch()
        match.start(setOf("p1", "p2"))
        match.getPool() shouldEqual setOf("p1", "p2")
    }

    it("matchEnd throws is match has not started") {
        var match = createMatch()
        val func = { match.end("oddsId1") }
        func shouldThrow IllegalArgumentException::class
    }

    it("matchEnd throws is winnerId is not in the odds") {
        var match = createMatch()
        match.start(setOf("p1", "p2"))
        val func = { match.end("invalid") }
        func shouldThrow IllegalArgumentException::class
    }

    it("matchEnd ends the match") {
        var match = createMatch()
        match.start(setOf("p1", "p2"))
        match.hasEnded() shouldEqual false
        match.end("oddsId1")
        match.hasEnded() shouldEqual true
    }

    it("MatchEnd sets winnings") {
        val match = createMatch()
        match.start(setOf("p1", "p2"))
        match.end("oddsId1")
        match.getWinnings() `should be instance of` Winnings::class
    }

    describe("when pool is set") {
        it("start throws") {
            var match = createMatch()
            match.start(setOf("p1", "p2"))
            val func = { match.start(setOf("p1", "p3")) }
            func shouldThrow IncompatibleClassChangeError::class
        }

        it("addBet throws") {
            var match = createMatch()
            match.start(setOf("p1", "p2"))
            val func = { match.addBet(playerId = "p1", oddsId = "oddsId1")}
            func shouldThrow IncompatibleClassChangeError::class
        }

        it("removeBet throws") {
            var match = createMatch()
            match.addBet(playerId = "p1", oddsId = "oddsId1")
            match.start(setOf("p1", "p2"))
            val func = { match.removeBet(playerId = "p1")}
            func shouldThrow IncompatibleClassChangeError::class
        }
    }
})
