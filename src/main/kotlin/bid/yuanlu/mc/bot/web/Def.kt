package bid.yuanlu.mc.bot.web


data class PlayInfo(
    val id:Long,
    val player: String,
    val host: String,
    val port: Int,
    val online: Boolean,
    val autoSend: List<String>
)

data class IndexData(val plays: List<PlayInfo>)