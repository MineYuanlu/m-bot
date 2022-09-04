package bid.yuanlu.mc.bot.def

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.event.HoverEvent.ShowEntity
import net.kyori.adventure.text.event.HoverEvent.ShowItem
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.format.TextDecoration.*
import org.jetbrains.annotations.Contract
import java.util.*

const val colorChar = '\u00A7'
private val styleChars = setOf('k', 'l', 'm', 'n', 'o')
private val colorChars = IntRange(0, 15).map { i -> i.toString(16)[0] }.toSet()
private const val styleDelimiter = " "

/**
 * 解析部件
 * @param component 部件
 * @return 解析结果
 */
fun parseComponent(component: Component): List<TextContent> {
    return parseComponent(component, false)
}

private fun parseComponent(component: Component, ignoreAction: Boolean): List<TextContent> {
    val content: ArrayList<TextContent>
    if (component is TextComponent) {
        val sb = style2string(component.style()).append(component.content())
        val color = component.style().color()?.asHexString()
        content = parseContent(color, sb)

    } else content = ArrayList()
    //TODO 支持其它Component

    val h = if (ignoreAction) null else component.style().hoverEvent()
    val hc: HoverContent? = if (h == null) null else HoverContent(h)
    val c = if (ignoreAction) null else component.style().clickEvent()
    if (h != null || c != null)
        content.forEach { tc ->
            run {
                tc.h = hc
                tc.c = c
            }
        }

    component.children().map { c -> parseComponent(c) }.forEach { list -> content.addAll(list) }
    return content
}

private fun parseContent(startColor: String?, content: StringBuilder): ArrayList<TextContent> {
    val tcs = ArrayList<TextContent>()
    var style = StringJoiner(styleDelimiter)
    val txt = StringBuilder()
    if (startColor != null) style.add(startColor)

    var i = 0
    while (i < content.length) {
        if (content[i] != colorChar) txt.append(content[i])
        else if (++i < content.length) {
            if (txt.isNotEmpty()) tcs.add(TextContent(style, txt))
            txt.setLength(0)
            val ch = content[i]
            if (ch in styleChars) {
                style.add(ch.toString())
            } else {
                style = StringJoiner(styleDelimiter)
                if (ch in colorChars) style.add(ch.toString())
            }
        }
        i++
    }
    if (txt.isNotEmpty()) tcs.add(TextContent(style, txt))

    return tcs
}

/**
 * 将样式转为字符串形式 (不包括颜色)
 */
@Contract("_->new", pure = true)
fun style2string(style: Style): StringBuilder {
    val sb = StringBuilder()
    for (td in values())
        if (style.decoration(td) == State.TRUE)
            sb.append(colorChar).append(td2key(td))
    return sb
}

/**
 * TextDecoration转为字符
 */
private fun td2key(td: TextDecoration): Char {
    return when (td) {
        OBFUSCATED -> 'k'
        BOLD -> 'l'
        STRIKETHROUGH -> 'm'
        UNDERLINED -> 'n'
        ITALIC -> 'o'
    }
}

class HoverContent internal constructor(h: HoverEvent<*>) {
    val action: String?
    val value: List<TextContent>?

    init {
        val act = h.action()
        val v = h.value()
        if (act == HoverEvent.Action.SHOW_TEXT) {
            action = act.toString()
            value = parseComponent((v as Component), true)
        } else if (act == HoverEvent.Action.SHOW_ITEM) {
            action = act.toString()
            val c = Component.text("物品: ")
                .append(Component.text((v as ShowItem).item().asString()).color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("数量: "))
                .append(Component.text(v.count().toString()).color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("NBT: "))
                .append(Component.text(v.nbt().toString()).color(NamedTextColor.GOLD))
            value = parseComponent(c, true)
        } else if (act == HoverEvent.Action.SHOW_ENTITY) {
            action = act.toString()
            var c = Component.text("实体: ")
                .append(Component.text((v as ShowEntity).type().asString()).color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("UUID: "))
                .append(Component.text(v.id().toString()).color(NamedTextColor.GOLD))

            val name = v.name()
            if (name != null) c = c
                .append(Component.newline())
                .append(Component.text("名称: ")).append(name).color(NamedTextColor.GOLD)
            value = parseComponent(c, true)
        } else {
            action = null;value = null
        }
    }

}

/**
 * 文字内容
 */
class TextContent internal constructor(s: StringJoiner, t: StringBuilder) {
    /**
     * 样式标记 每个标记由 [styleDelimiter] 分割
     *
     * 可能包含以#开头的hex颜色、0~f的16进制颜色码、klmno样式码
     */
    val s: String?
    val t: String?
    var h: HoverContent? = null
    var c: ClickEvent? = null

    init {
        this.s = if (s.length() > 0) s.toString() else null
        this.t = t.toString()
    }

    override fun toString(): String {
        return "TextContent(s=$s, t=$t, h=$h, c=$c)"
    }
}