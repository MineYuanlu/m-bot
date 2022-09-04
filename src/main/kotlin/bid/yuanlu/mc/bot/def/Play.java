package bid.yuanlu.mc.bot.def;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerRespawnPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginDisconnectPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketErrorEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import io.ktor.websocket.WebSocketSession;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;

/**
 * 一个游戏管理, 对应一个玩家与服务器之间的连接
 */
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Play implements Serializable, Closeable {
    @Serial
    private static final long serialVersionUID = 7042842042228314369L;
    /**
     * 唯一id
     */
    public final long id;
    /**
     * 玩家名称
     */
    public final @NonNull String player;
    /**
     * 服务器ip
     */
    public final @NonNull String host;
    /**
     * 服务器端口
     */
    public final int port;
    public final @NonNull Plans plans;
    /**
     * 服务器聊天缓存
     */
    private transient MsgCache chats = new MsgCache();
    /**
     * 监听此游戏的客户端
     */
    private transient ArrayList<MsgSender> hooks = new ArrayList<>();
    /**
     * 通信
     */
    @Nullable
    private transient Session session;

    @Builder
    public Play(long id, @NonNull String player, @NonNull String host, int port, @NonNull Plans plans) {
        this.id = id;
        this.player = player;
        this.host = host;
        this.port = port;
        this.plans = plans;
    }

    public synchronized void startPlans() {
        plans.run(this);
    }

    public synchronized void stopPlans() {
        plans.stop();
    }

    /**
     * @return 是否在线
     */
    public synchronized boolean isOnline() {
        return session != null && session.isConnected();
    }

    /**
     * 添加监听钩子
     *
     * @param msgSender 消息发送器
     * @see bid.yuanlu.mc.bot.Core#hookMsg(long, WebSocketSession)
     */
    public synchronized void hookMsg(@NonNull MsgSender msgSender) {
        msgSender.send(chats.getMsgs());
        hooks.add(msgSender);
    }

    /**
     * 发送聊天/命令
     *
     * @param line 文字内容
     */
    public synchronized void chat(@NonNull String line) {
        if (session != null && session.isConnected()) {
            session.send(new ClientChatPacket(line));
            handleMsg(MsgCache.Type.CHAT_OUT, line);
        } else {
            handleMsg(MsgCache.Type.CHAT_OUT, Component.text("发送失败: ").color(NamedTextColor.RED).append(Component.text(line).color(NamedTextColor.WHITE)));
        }
    }

    /**
     * 断开游戏连接
     *
     * @param reason 断开原因
     */
    public synchronized void disconnect(String reason) {
        if (session != null)
            session.disconnect(reason);
        session = null;
    }

    public synchronized void connect() {
        disconnect(null);
        val protocol = new MinecraftProtocol(player);

        session = new TcpClientSession(getHost(), getPort(), protocol, null);
        session.addListener(new SessionAdapter() {
            boolean inhibitionDisconnect = false;

            @Override
            public void packetError(PacketErrorEvent event) {
                event.getCause().printStackTrace();
                event.setSuppress(true);
            }

            @Override
            public void packetReceived(PacketReceivedEvent event) {
                val p = event.getPacket();
                if (p instanceof ServerChatPacket scp) {
                    handleMsg(MsgCache.Type.CHAT_IN, scp.getMessage());
                } else if (p instanceof ServerRespawnPacket srp) {
                    handleMsg(MsgCache.Type.RESPAWN,
                            String.format("重生于 [%s] (%s, %s->%s)", srp.getWorldName(), srp.getDimension().getName(),
                                    srp.getPreviousGamemode(), srp.getGamemode()));
                } else if (p instanceof LoginDisconnectPacket ldp) {
                    handleMsg(MsgCache.Type.DISCONNECT,
                            Component.text("[%s] ".formatted(protocol.getProfile().getName())).append(ldp.getReason()));
                    inhibitionDisconnect = true;
                } else if (p instanceof ServerDisconnectPacket sdp) {
                    handleMsg(MsgCache.Type.DISCONNECT,
                            Component.text("[%s] ".formatted(protocol.getProfile().getName())).append(sdp.getReason()));
                    inhibitionDisconnect = true;
                }
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                if (inhibitionDisconnect) {
                    inhibitionDisconnect = false;
                    return;
                }
                handleMsg(MsgCache.Type.DISCONNECT,
                        "[%s] %s".formatted(protocol.getProfile().getName(), event.getReason()));
                if (event.getCause() != null) {
                    event.getCause().printStackTrace();
                }
            }
        });
        session.connect();
    }

    private synchronized void handleMsg(MsgCache.Type type, String msg) {
        handleMsg(type, Component.text(msg));
    }

    private synchronized void handleMsg(MsgCache.Type type, Component msg) {
        val msg0 = chats.addMsg(type, msg);
        hooks.forEach(msgSender -> msgSender.send(msg0));
    }

    @Override
    public synchronized void close() {
        disconnect("Close");
        hooks.forEach(MsgSender::close);
    }

    /**
     * Always treat de-serialization as a full-blown constructor, by validating the final state of the de-serialized object.
     */
    @Serial
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        chats = new MsgCache();
        hooks = new ArrayList<>();
    }

    /**
     * This is the default implementation of writeObject. Customize as necessary.
     */
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    public long getId() {
        return this.id;
    }

    public @NonNull String getPlayer() {
        return this.player;
    }

    public @NonNull String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public @NonNull Plans getPlans() {
        return this.plans;
    }

    public MsgCache getChats() {
        return this.chats;
    }

    public ArrayList<MsgSender> getHooks() {
        return this.hooks;
    }

    @Nullable
    public Session getSession() {
        return this.session;
    }
}
