const chars = (() => {
  const cs = Array(36)
    .fill(0)
    .map((_, i) => i.toString(36));
  cs.push(...cs.map((x) => x.toUpperCase()));
  return Array.from(new Set(cs)).join('');
})();
/**
 * 随机字符串 `/[0-9a-zA-Z]{len}/`
 * @param len 字符串长度
 * @returns 字符串
 */
export function random(len: number) {
  return Array(len)
    .fill(0)
    .map(() => chars[(Math.random() * chars.length) | 0])
    .join('');
}
