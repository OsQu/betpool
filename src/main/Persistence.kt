import com.squareup.moshi.Moshi
import java.io.File
import java.io.PrintWriter

class Persistence(private val logFile: String) {
    fun logAction(action: Action) {
        val writer = PrintWriter(logFile)
        val jsonAdapter = Moshi.Builder().build().adapter(action.javaClass)
        writer.append(jsonAdapter.toJson(action) + "\n")
        writer.close()
    }

    fun readActions(): List<Action> {
        return File(logFile).readLines().map { Action.fromJSON(it) }
    }
}
