import com.squareup.moshi.Moshi
import java.io.File
import java.io.PrintWriter

class Persistence(private val logFile: String) {
    fun logAction(action: Action.PlayerJoin) {
        val writer = PrintWriter(logFile)
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(Action.PlayerJoin::class.java)
        writer.append(jsonAdapter.toJson(action) + "\n")
        writer.close()
    }

    fun readActions(): List<Action.PlayerJoin> {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(Action.PlayerJoin::class.java)
        return File(logFile).readLines().map { jsonAdapter.fromJson(it)!! }
    }
}
