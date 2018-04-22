import com.squareup.moshi.Moshi
import java.io.File
import java.io.PrintWriter

class Persistence(private val logFile: String) {
    fun logAction(action: Action) {
        val writer = PrintWriter(logFile)
        val jsonAdapter = moshi().adapter(action.javaClass)
        writer.append(jsonAdapter.toJson(action) + "\n")
        writer.close()
    }

    fun readActions(): List<Action> {
        return File(logFile).readLines().map { readAction(it) }
    }

    private fun readAction(json: String): Action {
        val baseAction: ActionType = moshi().adapter(ActionType::class.java).fromJson(json)!!
        return moshi().adapter(baseAction.toActionClass().java).fromJson(json)!!
    }

    private fun moshi(): Moshi {
        return Moshi.Builder().build()
    }
}
