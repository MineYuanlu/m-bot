package bid.yuanlu.mc.bot.def

import bid.yuanlu.mc.bot.def.MsgCache.Msg
import com.google.gson.Gson
import io.ktor.websocket.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.future.future
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

private val GSON = Gson()

/**
 * ws消息发送<br>带有缓存的消息发送接口
 */
@OptIn(DelicateCoroutinesApi::class)
class MsgSender(private val session: WebSocketSession) {
    private val lock = Object()
    private val queue = ConcurrentLinkedQueue<Msg>()
    private var close: Boolean = false

    init {
        thread(name = "msg-sender: ${session.hashCode()}") {
            while (!close) {
                synchronized(lock) {
                    lock.wait(1000)
                }
                GlobalScope.future { check() }.get()
                while (!close && queue.isNotEmpty()) {
                    val msg = queue.poll()
                    if (!GlobalScope.future { send0(msg) }.get()) close = true
                }
            }
        }
    }


    fun close() {
        close = true
        try {
            GlobalScope.future { session.close() }.get()
        } catch (_: Throwable) {

        }
    }

    fun send(msg: Msg): Boolean {
        if (close) return false
        queue.add(msg)
        synchronized(lock) {
            lock.notify()
        }
        return false
    }

    fun send(msgs: List<Msg>): Boolean {
        if (close) return false
        queue.addAll(msgs)
        synchronized(lock) {
            lock.notifyAll()
        }
        return false
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun check() {
        if (close) return
        if (session.outgoing.isClosedForSend) close = true
        else try {
            session.send(Frame.Text("-"))
            session.flush()
        } catch (e: ClosedSendChannelException) {
            close = true
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun send0(msg: Msg): Boolean {
        if (session.outgoing.isClosedForSend) {
            close = true
            return false
        }
//        stringBuilder.append(msg.type().name).append(' ')
//        stringBuilder.append(msg.type().key).append(' ')
//        stringBuilder.append(toJson(msg.component()))
        val json = GSON.toJson(msg)
        return try {
            session.send(Frame.Text(json))
            session.flush()
            true
        } catch (e: ClosedSendChannelException) {
            close = true
            false
        }
    }
}