package bid.yuanlu.mc.bot.web

import bid.yuanlu.mc.bot.Core
import bid.yuanlu.mc.bot.def.Plans
import bid.yuanlu.mc.bot.def.Play


data class IndexData(val plays: Collection<Play>)

data class PlanInfo(val type: String, val args: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PlanInfo
        if (type != other.type) return false
        if (!args.contentEquals(other.args)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }
}

data class PlansInfo(val running: Boolean, val list: List<PlanInfo>?)
data class PlayInfo(
    val id: Long, val player: String,
    val host: String,
    val port: Int,
    val online: Boolean,
    val plans: PlansInfo,

    )

fun getPlayList(): MutableList<PlayInfo> {
    return Core.PLAYS_VIEW.stream().map { play -> getPlayInfo(play, false) }.toList()
}

fun getPlayInfo(play: Play, withPlanList: Boolean?): PlayInfo {
    return PlayInfo(
        play.id,
        play.player,
        play.host,
        play.port,
        play.isOnline,
        getPlansInfo(play.plans, withPlanList)
    )
}

fun getPlansInfo(plans: Plans, withPlanList: Boolean?): PlansInfo {
    val list =
        if (withPlanList == true) plans.plans.stream().map { plan -> PlanInfo(Plans.getType(plan), plan.planArg) }
            .toList() else null
    return PlansInfo(plans.isRunning, list)
}