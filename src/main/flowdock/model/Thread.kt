package flowdock.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Field(val label: String, val value: String)

data class Thread(
    val title: String,
    val body: String? = null,
    val external_url: String? = null,
    val status: Status? = null,
    val actions: List<ThreadAction>? = null,
    val fields: List<Field>? = null
) {
    data class Status(val value: String, val color: String)
}

sealed class ThreadAction;

data class ViewAction(
    val url: String,
    val name: String
) : ThreadAction() {
    @JsonProperty("@type") val type: String = "ViewAction"
}

data class UpdateAction(
    val name: String,
    val target: Target
): ThreadAction() {
    @JsonProperty("@type") val type: String = "UpdateAction"

    data class Target(
        val urlTemplate: String,
        val httpMethod: String
    ) {
        @JsonProperty("@type") val type: String = "EntryPoint"
    }
}