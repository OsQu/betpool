package flowdock

import com.fasterxml.jackson.databind.annotation.JsonAppend

@JsonAppend(
    attrs = arrayOf(
        JsonAppend.Attr(value = "flow_token")
    )
)
object FlowTokenMixin {}