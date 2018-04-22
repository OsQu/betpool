package flowdock.model

data class Activity(
  val title: String,
  val body: String? = null,
  val author: Author,
  val external_thread_id: String,
  val thread: Thread
) {
    val event = "activity"
}