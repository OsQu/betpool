package betfair

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import fixtures.marketsApiResponse
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.net.URL
import java.time.Instant

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
                dataStream = marketsApiResponse.byteInputStream()
            ))
            val markets = MarketsAPI.fetch()

            markets.size shouldEqual 2
            markets.first().event shouldEqual "Mark Allen v Liam Highfield"
            markets.first().startTime shouldEqual Instant.parse("2018-04-22T09:00:00.000Z")
        }
    }
})