class Match(val athlete1Name: String, val athlete2Name: String, var pool: Set<String> = setOf()) {

    override fun toString(): String {
        return "$athlete1Name - $athlete2Name"
    }
}
