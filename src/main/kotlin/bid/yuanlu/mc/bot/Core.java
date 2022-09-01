package bid.yuanlu.mc.bot;

import bid.yuanlu.mc.bot.web.PlayInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Core {
    private static final AtomicLong ID=new AtomicLong();
    private static final List<PlayInfo> plays = new Vector<>();
    public static final List<PlayInfo> PLAYS = Collections.unmodifiableList(plays);
private static void addPlayer(){

}
    static {
        plays.add(new PlayInfo(1,"yuanlu", "127.0.0.1", 25565, true, Arrays.asList("/reg 123123 123123", "/l 123123")));
        plays.add(new PlayInfo(2,"yuanlu", "127.0.0.1", 25565, false, Arrays.asList("/reg 123123 123123", "/l 123123")));
    }
}
