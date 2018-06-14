package flowdock

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import flowdock.model.Activity
import java.nio.charset.Charset

const val FLOWDOCK_URL = "https://api.flowdock.com"

class FlowdockAPI(val flowTokens: Set<String>) {
    fun createActivity(activity: Activity) {
        flowTokens.forEach { postActivity(activity, it) }
    }
    private fun postActivity(activity: Activity, flowToken: String) {
        val payload = objectMapper(activity::class.java)
                .writerFor(activity::class.java)
                .withAttribute("flow_token", flowToken)
                .writeValueAsString(activity)

        Fuel.post("${FLOWDOCK_URL}/messages")
                .header(mapOf("Content-Type" to "application/json"))
                .body(payload, Charset.forName("UTF-8"))
                .response { _, response, _ -> println("GOT STATUS CODE: ${response.statusCode}") } // TODO: Do something with me!
    }

    private fun <T> objectMapper(targetClass: Class<T>): ObjectMapper =
        ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .addMixIn(targetClass, FlowTokenMixin::class.java)

}