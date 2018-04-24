package flowdock.model

import com.fasterxml.jackson.annotation.JsonProperty

data class IncomingUpdateAction(
        @JsonProperty("@type") val type: String,
        val name: String,
        val agent: Agent,
        val target: Target
) {
    data class Agent(
            @JsonProperty("@type") val type: String,
            val name: String,
            val url: String,
            val image: String
    ) {
        fun toAuthor(): Author = let { agent -> Author(agent.name, agent.image) }
    }

    data class Target(
            @JsonProperty("@type") val type: String,
            val urlTemplate: String,
            val httpMethod: String
    )
}