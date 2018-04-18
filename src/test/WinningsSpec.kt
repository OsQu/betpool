import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class WinningsSpec : Spek({
    it("starts with empty data") {
        var winnings = Winnings()
        winnings.getData() shouldEqual HashMap<String, Int>()
    }

    it("addPlayer adds a player with zero winnings") {
        var winnings = Winnings()
        winnings.addPlayer("testId")
        winnings.getData() shouldEqual mapOf("testId" to 0)
    }

    it("mergeWinnings returns new winnings") {
        val firstWinnings = Winnings(HashMap(mapOf("p1" to 1, "p2" to -1)))
        val secondWinnings = Winnings(HashMap(mapOf("p1" to 1, "p2" to -1)))
        val newWinnings = firstWinnings.merge(secondWinnings)
        newWinnings.getData() shouldEqual mapOf("p1" to 2, "p2" to -2)
    }

    it("mergeWinnings returns doesnt mutate winnings") {
        val data = HashMap(mapOf("p1" to 1, "p2" to -1))
        val firstWinnings = Winnings(data)
        val secondWinnings = Winnings(data)
        firstWinnings.merge(secondWinnings)
        firstWinnings.getData() shouldEqual data
        secondWinnings.getData() shouldEqual data
    }

    it("is impossible to create a Winning where sum of values is not zero") {
        val func = { Winnings(HashMap(mapOf("p1" to 1))) }
        func shouldThrow IllegalArgumentException::class
    }
})
