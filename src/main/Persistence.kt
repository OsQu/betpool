import com.squareup.moshi.Moshi
import java.io.File

class Persistence(private val logFile: String) {
    init {
        File(logFile).createNewFile()
    }
    fun logAction(action: Action) {
        val jsonAdapter = Moshi.Builder().build().adapter(action.javaClass)
        File(logFile).appendText(jsonAdapter.toJson(action) + "\n")
    }

    fun readActions(): List<Action> {
        return File(logFile).readLines().map { Action.fromJSON(it) }
    }
}
