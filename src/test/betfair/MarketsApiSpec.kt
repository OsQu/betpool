package betfair

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import java.net.URL
import java.time.Instant

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

class MarketsApiSpec : Spek({
    val client = mock<Client>()
    val oldClient = FuelManager.instance.client

    beforeGroup {
        FuelManager.instance.client = client;
    }

    afterGroup {
        FuelManager.instance.client = oldClient
    }

    describe("#fetch") {
        it("returns List of Markets") {
            whenever(client.executeRequest(any())).thenReturn(Response(
                url = URL("http://www.example.com"),
                statusCode = 200,
                dataStream = jsonResponse.byteInputStream()
            ))
            val markets = MarketsAPI.fetch()

            markets.size shouldEqual 2
            markets.first().event shouldEqual "Mark Allen v Liam Highfield"
            markets.first().startTime shouldEqual Instant.parse("2018-04-22T09:00:00.000Z")
        }
    }
})