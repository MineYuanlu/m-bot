package bid.yuanlu.mc.bot;

import lombok.Builder;
import lombok.Data;
import net.kyori.adventure.text.Component;

import java.util.List;

@Data
@Builder
public class Play {
    final long id;
    String player;
    String host;
    int port;
    boolean online;
    List<String> autoSend;
    /**服务器聊天缓存*/
    final List<Component> chats;
}
