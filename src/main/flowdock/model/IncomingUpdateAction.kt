package flowdock.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.moshi.Moshi
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

val OAUTH_APP_SECRET = System.getenv("FLOW_APP_SECRET")

data class IncomingUpdateAction(
        @JsonProperty("@type") val type: String,
        val name: String,
        val agent: Agent,
        val target: Target
) {
    companion object {
        fun build(token: String, jsonString: String): IncomingUpdateAction {
            val algorithm = Algorithm.HMAC256(OAUTH_APP_SECRET)
            val verifier = JWT.require(algorithm).build()
            val jwt = verifier.verify(token)
            // JSON payload is not yet verified with the jwt.signature
            val action = Moshi.Builder().build().adapter(IncomingUpdateAction::class.java).fromJson(jsonString)!!
            action.agent.id = jwt.subject
            return action
        }
    }
    data class Agent(
            @JsonProperty("@type") val type: String,
            val name: String,
            val url: String,
            val image: String,
            var id: String
    ) {
        fun toAuthor(): Author = let { agent -> Author(agent.name, agent.image) }
    }

    data class Target(
            @JsonProperty("@type") val type: String,
            val urlTemplate: String,
            val httpMethod: String
    )
}
