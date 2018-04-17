class Match(val player1: String, val player2: String, var bank: Bank) {

    override fun toString(): String {
        return "$player1 - $player2"
    }
}
