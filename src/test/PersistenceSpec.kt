import org.amshove.kluent.should
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class PersistenceSpec : Spek({
    it("serializes and deserializes actions to/from file") {
        val persistence = Persistence("/tmp/kotlinlog.log")
        persistence.logAction(Action.PlayerJoin("Sampo"))
        persistence.readActions() shouldEqual listOf(Action.PlayerJoin("Sampo"))
    }
})
