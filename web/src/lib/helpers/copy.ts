import { dev } from '$app/environment';
import copy0 from 'copy-to-clipboard';

/**
 * 复制到剪切板
 * @param txt 复制的内容
 * @param onCopy 接受 `clipboardData` 来添加自定义行为(例如附加格式)
 * @returns
 */
export const copy = (txt: string, onCopy?: (clipboardData: object) => void) =>
  copy0(txt, {
    debug: dev,
    message: '按下 #{key} 以复制内容',
    onCopy,
  });
