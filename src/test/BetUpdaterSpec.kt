import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.nhaarman.mockito_kotlin.*
import fixtures.flowdockResponse
import fixtures.marketsApiResponse
import flowdock.model.Activity
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xdescribe
import java.io.ByteArrayOutputStream
import java.net.URL

const val jsonResponse = """
[
  {
    "marketId": "1.142872972",
    "event": "Mark Allen v Liam Highfield",
    "start": "2018-04-22T09:00:00.000Z",
    "odds": {
      "2279947": {
        "name": "Mark Allen",
        "odds": 1.31
      },
      "4392732": {
        "name": "Liam Highfield",
        "odds": 4
      }
    }
  },
  {
    "marketId": "1.142873360",
    "event": "Shaun Murphy v Jamie Jones",
    "start": "2018-04-22T13:30:00.000Z",
    "odds": {
      "2278789": {
        "name": "Shaun Murphy",
        "odds": 1.4
      },
      "3502366": {
        "name": "Jamie Jones",
        "odds": 3.4
      }
    }
  }
]
"""

private fun extractHttpBody(request: Request): String? {
    val body = ByteArrayOutputStream().apply {
        request.bodyCallback?.invoke(request, this, 0)
    }.toString()

    if (body.isBlank()) {
        return null;
    } else {
        return body;
    }
}

private inline fun <reified T: Any> parseJson(json: String): T =
    jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue<T>(json)


class BetUpdaterSpec : Spek({
    val client = mock<Client>()
    val oldClient = FuelManager.instance.client

    beforeGroup {
        FuelManager.instance.client = client;
    }

    afterGroup {
        FuelManager.instance.client = oldClient
    }

    xdescribe("run") {
        whenever(client.executeRequest(
            argThat { this.url == URL(betfair.URL) }
        )).thenReturn(Response(
            url = URL("http://www.example.com"),
            statusCode = 200,
            dataStream = marketsApiResponse.byteInputStream()
        ))

        whenever(client.executeRequest(
            argThat { this.url == URL("https://api.flowdock.com/messages") }
        )).thenReturn(Response(
            url = URL("http://flowdock.com"),
            statusCode = 200,
            dataStream = flowdockResponse.byteInputStream()
        ))

        it("fetches odds from MarketApi") {
            BetUpdater("deadbeef").run()
            verify(client).executeRequest(
                argThat { this.url == URL(betfair.URL) }
            )
        }

        it("sends betting information to Flowdock") {
            BetUpdater("deadbeef").run()
            verify(client).executeRequest(
                argThat {
                    val activity = extractHttpBody(this)?.let { parseJson<Activity>(it) }
                    this.url == URL("https://api.flowdock.com/messages") &&
                    activity?.title == "Updated betting information" &&
                    activity.thread.title == "Mark Allen v Liam Highfield" &&
                    activity.thread.body == "Odds: Mark Allen [1.31] - Liam Highfield [4.0]"
                }
            )
        }
    }
})