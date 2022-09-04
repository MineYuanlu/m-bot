package bid.yuanlu.mc.bot.def;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 任务计划列表
 */
@NoArgsConstructor
public final class Plans implements Serializable {
    private static final Map<String, Function<String[], ? extends Plan>> PLAN_CONSTRUCTOR = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Plan>, String> PLAN_TYPE = new ConcurrentHashMap<>();
    @Serial
    private static final long serialVersionUID = 7042842042228314369L;

    static {
        addPlanType("connect", PlanConnect.class, PlanConnect::new);
        addPlanType("disconnect", PlanDisconnect.class, PlanDisconnect::new);
        addPlanType("chat", PlanChat.class, PlanChat::new);
        addPlanType("delay", PlanDelay.class, PlanDelay::new);
    }

    /**
     * 计划列表
     */
    private @NonNull List<@NonNull Plan> plans = Collections.emptyList();
    /**
     * 运行计数
     */
    private transient long id;
    /**
     * 是否正在运行
     */
    private transient boolean running;

    public Plans(@NonNull List<@NonNull Plan> plans) {
        setPlans(plans);
    }

    public Plans(@NonNull Plan @NonNull ... plans) {
        setPlans(Arrays.asList(plans));
    }

    @Contract(pure = true)
    public static String getType(@NotNull Plan plan) {
        return PLAN_TYPE.get(plan.getClass());
    }

    private static <T extends Plan> void addPlanType(String type, Class<T> clazz, Function<String[], T> builder) {
        PLAN_CONSTRUCTOR.put(type, builder);
        PLAN_TYPE.put(clazz, type);
    }

    /**
     * 将字符串逆序列化为计划列表
     *
     * @param txt 字符串
     * @return 计划列表
     * @throws RuntimeException 逆序列化中的问题
     * @see #getPlanString() 序列化
     */
    @NotNull
    @Contract("_ -> new")
    public static Plans build(String txt) throws RuntimeException {
        val list = parsePlanString(txt);
        return list.isEmpty() ? new Plans() : new Plans(list);
    }

    /**
     * @see #build(String)
     */
    private static List<@NonNull Plan> parsePlanString(String txt) throws RuntimeException {
        if (txt == null || txt.isBlank())
            return Collections.emptyList();
        val list = new ArrayList<Plan>();
        for (val line : txt.replace("\r", "").split("\n")) {
            var arg = line.trim().split(" ", 2);
            val builder = PLAN_CONSTRUCTOR.get(arg[0].toLowerCase(Locale.ENGLISH));
            val plan = builder.apply(arg.length > 1 ? arg[1].split(" ") : new String[0]);
            list.add(plan);
        }
        return list;
    }

    /**
     * 从字符串重新设置计划列表
     *
     * @param txt 字符串
     * @throws RuntimeException 逆序列化中的问题
     * @see #getPlanString() 序列化
     */
    public void update(String txt) throws RuntimeException {
        setPlans(parsePlanString(txt));
    }

    public List<Plan> getPlans() {
        return new ArrayList<>(plans);
    }

    public synchronized void setPlans(@NonNull List<@NonNull Plan> plans) {
        stop();
        this.plans = new ArrayList<>(plans);
    }

    /**
     * 获取计划的字符串<br>
     * 一个计划列表可以序列化为字符串, 并由字符串可以{@link #build 逆序列化}为计划列表
     *
     * @return
     */
    public String getPlanString() {
        val itr = plans.iterator();
        if (!itr.hasNext()) return "";
        val sb = new StringBuilder();
        while (true) {
            val plan = itr.next();
            sb.append(getType(plan));
            for (val arg : plan.getPlanArg()) sb.append(' ').append(arg);
            if (itr.hasNext()) sb.append('\n');
            else return sb.toString();
        }
    }

    public void run(@NonNull final Play play) {
        final long id;
        final List<Plan> plans;
        synchronized (this) {
            id = ++this.id;
            running = true;
            plans = this.plans;
        }
        val name = String.format("Play-%s-Plan-%s/%s", play.getId(), super.hashCode(), id);
        val t = new Thread(name) {
            @Override
            public void run() {
                for (val p : plans) {
                    if (Plans.this.id != id)
                        break;
                    try {
                        p.accept(play);
                    } catch (Exception e) {
                        new RuntimeException("无法运行计划任务[" + name + "]", e).printStackTrace();
                    }
                }
                synchronized (Plans.this) {
                    if (id == Plans.this.id)
                        running = false;
                }

            }
        };
        t.start();
    }

    public synchronized void stop() {
        id++;
        running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    /**
     * 代表一个计划步骤
     */
    public interface Plan extends Serializable {
        /**
         * 运行此计划<br>
         * 一个计划会在一个独立的线程中运行
         *
         * @param play 此计划所对应的Play对象
         * @throws Exception 计划中任何错误
         */
        void accept(@NonNull Play play) throws Exception;

        /**
         * 获取计划的参数
         *
         * @return 计划的参数
         */
        @NotNull
        @Contract(value = " -> new", pure = true)
        String[] getPlanArg();
    }

    /**
     * 连接计划
     */
    public record PlanConnect() implements Plan {
        PlanConnect(String[] arg) {
            this();
        }

        @Override
        public void accept(@NonNull Play play) {
            play.connect();
        }

        @NotNull
        @Contract(value = " -> new", pure = true)
        @Override
        public String[] getPlanArg() {
            return new String[0];
        }
    }

    /**
     * 聊天计划
     */
    public record PlanChat(String chat) implements Plan {
        PlanChat(String[] arg) {
            this(String.join(" ", arg));
        }

        @Override
        public void accept(@NonNull Play play) {
            play.chat(chat);
        }

        @NotNull
        @Override
        @Contract(value = " -> new", pure = true)
        public String[] getPlanArg() {
            return chat.split(" ");
        }
    }

    /**
     * 延时计划
     */
    public record PlanDelay(long delay) implements Plan {
        PlanDelay(String[] arg) {
            this(Long.parseLong(arg[0]));
        }

        @Override
        public void accept(@NonNull Play play) throws InterruptedException {
            Thread.sleep(delay);
        }

        @NotNull
        @Override
        @Contract(value = " -> new", pure = true)
        public String[] getPlanArg() {
            return new String[]{Long.toString(delay)};
        }
    }

    /**
     * 断开计划
     */
    public record PlanDisconnect(String reason) implements Plan {
        PlanDisconnect(String[] arg) {
            this(String.join(" ", arg));
        }

        @Override
        public void accept(@NonNull Play play) {
            play.disconnect(reason);
        }

        @NotNull
        @Override
        @Contract(value = " -> new", pure = true)
        public String[] getPlanArg() {
            return reason.split(" ");
        }
    }
}
