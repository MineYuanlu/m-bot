package bid.yuanlu.mc.bot.def;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static bid.yuanlu.mc.bot.def.ComponentParserKt.parseComponent;


/**
 * 消息缓存
 */
@AllArgsConstructor
public final class MsgCache {
    /**
     * 消息缓存
     */
    private final LinkedList<Msg> list = new LinkedList<>();
    /**
     * 大小
     */
    private final int size;

    public MsgCache() {
        this(128);
    }

    /**
     * 添加一条消息
     *
     * @param type 消息类型
     * @param msg  消息内容
     * @return 消息体
     */
    @NotNull
    @Contract("_,_ -> new")
    public synchronized Msg addMsg(Type type, @NonNull Component msg) {
        val msg0 = new Msg(type, parseComponent(msg.compact()));
        list.add(msg0);
        if (list.size() > size)
            list.pollLast();
        return msg0;
    }

    /**
     * 获取所有缓存的消息
     *
     * @return 消息列表
     */
    @NotNull
    @Contract(" -> new")
    public synchronized List<Msg> getMsgs() {
        return new ArrayList<>(list);
    }

    @RequiredArgsConstructor
    public enum Type {
        CHAT_IN('↓'), CHAT_OUT('↑'), RESPAWN('R'), CONNECT('+'), DISCONNECT('-');
        public final char key;
    }

    public record Msg(@NonNull Type type, @NonNull List<TextContent> component) {
        @Override
        public String toString() {
            val sb = new StringBuilder();
            sb.append('[').append(type.key).append(']').append(' ');
            component.stream().map(TextContent::getT).forEach(sb::append);
            return sb.toString();
        }
    }
}
