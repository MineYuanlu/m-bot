'https://wiki.vg/Chat';

/**消息类型 */
export const MsgType = {
  CHAT_IN: '↓',
  CHAT_OUT: '↑',
  RESPAWN: 'R',
  CONNECT: '+',
  DISCONNECT: '-',
} as const;

/**一条消息 */
export type Msg = { type: keyof typeof MsgType; component: TextContent[] };
/**
 * 文本内容
 */
export type TextContent = {
  /**样式描述*/
  s?: string;
  /**正文 */
  t: string;
  /**悬浮事件 */
  h?: HoverEvent;
  /**点击事件 */
  c?: ClickEvent;
};

export const ClickAction = {
  OPEN_URL: '打开网址',
  OPEN_FILE: '打开文件',
  RUN_COMMAND: '运行命令',
  SUGGEST_COMMAND: '推荐命令',
  CHANGE_PAGE: '换页',
  COPY_TO_CLIPBOARD: '复制到剪切板',
} as const;
export type ClickEvent = { action: keyof typeof ClickAction; value: string };
export const HoverAction = {
  show_text: '显示文字',
  show_item: '显示物品',
  show_entity: '显示实体',
} as const;
export type HoverEvent = {
  action: keyof typeof HoverAction;
  value: TextContent[];
};

/**Control Sequences */
export const colorChar = '\u00A7';
/**颜色键 */
export const colorsKey = '0123456789abcdef'.split('');
/**颜色键 */
export const colorsKeySet = new Set(colorsKey);
/**颜色代码 */
export const colors =
  '000000|000000,0000aa|00002a,00aa00|002a00,00aaaa|002a2a,aa0000|2a0000,aa00aa|2a002a,ffaa00|2a2a00,aaaaaa|2a2a2a,555555|151515,5555ff|15153f,55ff55|153f15,55ffff|153f3f,ff5555|3f1515,ff55ff|3f153f,ffff55|3f3f15,ffffff|3f3f3f'
    .split(',')
    .map((x) =>
      x
        .split('|')
        .map((t) => `#${t}`)
        .join('|'),
    );
/**样式键 */
export const styleKey = 'klmnor'.split('');
/**样式键 */
export const styleKeySet = new Set(styleKey);
