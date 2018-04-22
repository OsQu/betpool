import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.FileWriter

class PersistenceSpec : Spek({
    it("serializes and deserializes actions to/from file") {
        FileWriter("/tmp/kotlinlog.log").close() // Truncates the file
        val persistence = Persistence("/tmp/kotlinlog.log")
        persistence.logAction(Action.PlayerJoin("Sampo"))
        persistence.readActions() shouldEqual listOf(Action.PlayerJoin("Sampo"))
    }
})
