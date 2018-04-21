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
        xit("returns List of Markets") { // TODO: Figure out how to return some data in Response
            whenever(client.executeRequest(any())).thenReturn(Response(URL("http://www.example.com"), 200, "[]"))
            val markets = MarketsAPI.fetch()
        }
    }
})