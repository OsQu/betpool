package betpool

import com.squareup.moshi.*
import java.io.File
import java.util.*
import java.text.SimpleDateFormat
import java.util.TimeZone

class Persistence(private val logFile: String) {
    init {
        File(logFile).createNewFile()
    }
    class DateAdapter {
        @ToJson fun toJson(date: Date): String {
            val tz = TimeZone.getTimeZone("UTC")
            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
            df.timeZone = tz
            return df.format(date)
        }
        @FromJson fun fromJson(dateStr: String): Date {
            val tz = TimeZone.getTimeZone("UTC")
            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
            df.timeZone = tz
            return df.parse(dateStr)
        }
    }

    fun logAction(action: Action) {
        val jsonAdapter = Moshi.Builder()
                .add(DateAdapter())
                .build().adapter(action.javaClass)
        File(logFile).appendText(jsonAdapter.toJson(action) + "\n")
    }

    fun readActions(): List<Action> {
        return File(logFile).readLines().map { Action.fromJSON(it) }
    }
}
