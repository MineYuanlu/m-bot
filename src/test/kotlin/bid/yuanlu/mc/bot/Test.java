package bid.yuanlu.mc.bot;

import bid.yuanlu.mc.bot.def.ComponentParserKt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class Test {
    private static final Gson GSON = new GsonBuilder()
//    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
//    .excludeFieldsWithoutExposeAnnotation()
//    .excludeFieldsWithModifiers(TRANSIENT)
//    .registerTypeHierarchyAdapter(Object::class.java, GenericSerializer())
//    .registerTypeAdapterFactory(RuntimeClassNameTypeAdapterFactory.of(Object::class.java, "_class_"))
            .create();

    public static void main(String[] args) {
        val c = Component.text("123").clickEvent(ClickEvent.runCommand("123")).hoverEvent(HoverEvent.showText(Component.text("123")));
        System.out.println(GSON.toJson(ComponentParserKt.parseComponent(c)));
    }
}
