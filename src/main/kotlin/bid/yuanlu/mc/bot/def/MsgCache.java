package bid.yuanlu.mc.bot.def;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public final class MsgCache {
    private final LinkedList<Component> list=new LinkedList<>();
    private final int size;
    public MsgCache(){
        this(128);
    }
    public synchronized void addMsg(Component msg){
        list.add(msg);
        if (list.size()>size)list.pollLast();
    }
    public synchronized List<Component> getMsgs(){
        return new ArrayList<>(list);
    }
}
