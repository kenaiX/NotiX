package cc.kenai.noti.model


data class Record(var id: Long, var type: Int, var limit: Long, var question: String, val answer: String) {

    companion object {
        fun fromString(s: String): Record? {
            val split = s.split("|")
            if (split.size != 5) {
                return null
            }

            val id = split[0]
            val type = split[1]
            val limit = split[2]
            val question = split[3]
            val answer = split[4]

            try {
                return Record(id.toLong(), type.toInt(), limit.toLong(), question, answer)
            } catch (e: Exception) {
                return null
            }
        }
    }
}