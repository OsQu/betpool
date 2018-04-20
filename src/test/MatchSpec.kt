import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.Date

class MatchSpec : Spek({
    it("toString returns the player names") {
        var match = Match("id", "Ronnie", "Selby", Date())
        match.toString() shouldEqual "Ronnie - Selby"
    }

    it("addBet adds a bet") {
        var match = Match("id", "Ronnie", "Selby", Date())
        match.addBet(playerId = "p1", athleteNo = 1)
        match.getBets() shouldEqual mapOf("p1" to 1)
    }

    it("addBet throws if player has already bet on the match") {
        var match = Match("id", "Ronnie", "Selby", Date())
        match.addBet(playerId = "p1", athleteNo = 1)
        val func = { match.addBet(playerId = "p1", athleteNo = 1) }
        func shouldThrow IllegalArgumentException::class
    }

    it("removeBet removes a bet") {
        var match = Match("id", "Ronnie", "Selby", Date())
        match.addBet(playerId = "p1", athleteNo = 1)
        match.addBet(playerId = "p2", athleteNo = 2)
        match.removeBet(playerId = "p1")
        match.getBets() shouldEqual mapOf("p2" to 2)
    }

    it("removeBet throws if the player doesn't have a bet") {
        var match = Match("id", "Ronnie", "Selby", Date())
        val func = { match.removeBet(playerId = "p1") }
        func shouldThrow IllegalArgumentException::class
    }

    it("setPool sets the pool for the match") {
        var match = Match("id", "Ronnie", "Selby", Date())
        match.setPool(setOf("p1", "p2"))
        match.getPool() shouldEqual setOf("p1", "p2")
    }

    describe("when pool is set") {
        it("setPool throws") {
            var match = Match("id", "Ronnie", "Selby", Date())
            match.setPool(setOf("p1", "p2"))
            val func = { match.setPool(setOf("p1", "p3")) }
            func shouldThrow IncompatibleClassChangeError::class
        }

        it("addBet throws") {
            var match = Match("id", "Ronnie", "Selby", Date())
            match.setPool(setOf("p1", "p2"))
            val func = { match.addBet(playerId = "p1", athleteNo = 1)}
            func shouldThrow IncompatibleClassChangeError::class
        }

        it("removeBet throws") {
            var match = Match("id", "Ronnie", "Selby", Date())
            match.addBet(playerId = "p1", athleteNo = 1)
            match.setPool(setOf("p1", "p2"))
            val func = { match.removeBet(playerId = "p1")}
            func shouldThrow IncompatibleClassChangeError::class
        }
    }
})
