import type { MaybePromise } from '$def/common';
import { readable } from 'svelte/store';

export type Storges = 'theme.dark' | 'msg.limit';

/**
 *
 * @param key
 * @param def
 * @param serialize
 * @param deserialize
 * @returns
 */
export const storge = async <T>(
  key: string,
  def: T,
  serialize: (data: T) => MaybePromise<string>,
  deserialize: (data: string) => MaybePromise<T>,
) => {
  const str = localStorage.getItem(key);
  let data: T;
  if (str == null) data = def;
  else data = await deserialize(str);

  const set = async (data: T) => {
    localStorage.setItem(key, await serialize(data));
  };
  await set(data);

  const box = readable(data, (setter) => {
    setter(data);
    const listener: (this: Window, ev: StorageEvent) => any = async (ev) => {
      if (ev.key != key) return;
      const str = ev.newValue;
      if (ev.oldValue == str) return;
      if (str == null) setter((data = def));
      else setter((data = await deserialize(str)));
    };
    window.addEventListener('storage', listener);
    return () => window.removeEventListener('storage', listener);
  });
  return { box, set };
};
