package bid.yuanlu.mc.bot;

import bid.yuanlu.mc.bot.def.MsgSender;
import bid.yuanlu.mc.bot.def.Plans;
import bid.yuanlu.mc.bot.def.Play;
import io.ktor.websocket.WebSocketSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Core {
    private static final File DATA_FILE = new File("runtime/datas");
    private static final File DATA_ID_FILE = new File(DATA_FILE, "id");
    private static final AtomicLong ID = new AtomicLong();
    private static final SortedMap<Long, Play> PLAYS = new ConcurrentSkipListMap<>();
    public static final Collection<Play> PLAYS_VIEW = Collections.unmodifiableCollection(PLAYS.values());

    @SuppressWarnings("all")
    public static void init() {
        DATA_FILE.mkdirs();
        if (DATA_FILE.listFiles().length == 0)
            addPlay("yuanlu", "play.loricat.cn", 25565, new Plans(new Plans.PlanConnect(), new Plans.PlanDelay(1000)));
        try (val in = new DataInputStream(new FileInputStream(DATA_ID_FILE))) {
            ID.set(in.readLong());
        } catch (FileNotFoundException e) {
            return;//没有ID文件,忽略所有
        } catch (Throwable e) {
            System.err.println("无法读取ID文件");
            e.printStackTrace();
            System.exit(-1);
        }
        for (val file : DATA_FILE.listFiles((dir, name) -> {
            if (name == null || name.isEmpty())
                return false;
            for (int i = 0; i < name.length(); i++)//unsigned
                if (name.charAt(i) < '0' || name.charAt(i) > '9')
                    return false;
            return true;
        }))
            try (val in = new ObjectInputStream(new FileInputStream(file))) {
                long id = Long.parseUnsignedLong(file.getName());
                Play play = (Play) in.readObject();
                if (play.getId() != id)
                    throw new IOException("文件内所指定的ID[%s]与文件名[%s]不相符".formatted(play.getId(), file));
                PLAYS.put(id, play);
            } catch (Throwable e) {
                e.printStackTrace();
                System.exit(-1);
            }

    }

    /**
     * 移除游戏
     *
     * @param id 游戏ID
     * @return 被移除的游戏
     */
    public synchronized static Play removePlay(long id) {
        val old = PLAYS.remove(id);
        try {
            val file = getPlayFile(id);
            //noinspection ResultOfMethodCallIgnored
            file.renameTo(new File(file.getParentFile(), file.getName() + ".del"));
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return old;
    }

    /**
     * 添加游戏
     *
     * @param player 玩家名称
     * @param host   服务器地址
     * @param port   服务器端口
     * @param plans  计划任务
     * @return 游戏ID
     */
    public synchronized static long addPlay(@NonNull String player, @NonNull String host, int port,
                                            @NonNull Plans plans) {
        val play = new Play(ID.incrementAndGet(), player, host, port, plans);
        PLAYS.put(play.getId(), play);

        try (val out = new DataOutputStream(new FileOutputStream(DATA_ID_FILE))) {
            out.writeLong(play.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (val out = new ObjectOutputStream(new FileOutputStream(getPlayFile(play.getId())))) {
            out.writeObject(play);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return play.getId();
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static File getPlayFile(long id) {
        return new File(DATA_FILE, Long.toUnsignedString(id));
    }

    /**
     * 添加消息监听
     *
     * @param id      监听游戏对象ID
     * @param session websocket
     * @return 是否成功连接
     */
    public static boolean hookMsg(long id, @NotNull WebSocketSession session) {
        val play = getPlay(id);
        if (play == null)
            return false;
        play.hookMsg(new MsgSender(session));
        return true;
    }

    @Nullable
    public static Play getPlay(long id) {
        if (id <= 0)
            return null;
        return PLAYS.get(id);
    }

}
