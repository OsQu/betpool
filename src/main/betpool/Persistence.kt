package betpool

import com.squareup.moshi.*
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatterBuilder

class Persistence(private val logFile: String) {
    init {
        File(logFile).createNewFile()
    }

    class InstantAdapter {
        @ToJson fun toJson(instant: Instant): String {
            var formatter = DateTimeFormatterBuilder().appendInstant(3).toFormatter()
            return formatter.format(instant)
        }
        @FromJson fun fromJson(dateStr: String): Instant {
            return Instant.parse(dateStr)
        }
    }

    fun logAction(action: Action) {
        val jsonAdapter = Moshi.Builder()
                .add(InstantAdapter())
                .build().adapter(action.javaClass)
        File(logFile).appendText(jsonAdapter.toJson(action) + "\n")
    }

    fun readActions(): List<Action> {
        return File(logFile).readLines().map { Action.fromJSON(it) }
    }
}
