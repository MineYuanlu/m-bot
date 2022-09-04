import { dev } from '$app/environment';
import type { MaybePromise, AddAlert, MaybeFunction } from './common';

export type ApiResp<T> = { ok: false; err: any } | { ok: true; info: T };

/**
 * 响应获取
 * @param resp 响应
 * @param alert
 */
export async function handle<T>(resp: MaybeFunction<MaybePromise<Response>>, alert: AddAlert) {
  try {
    if (resp instanceof Function) resp = resp();
    if ((resp = await resp).ok) {
      const json: ApiResp<T> = await resp.json();
      if (json.ok) return json;
      else alert(json.err, 'danger');
    } else alert(resp.status, 'danger');
    return null;
  } catch (err) {
    alert(err, 'danger');
  }
}

export const apiPath = `${dev ? '//127.0.0.1:8080' : ''}/api`;
export const wsApiPath = (page: URL) => `ws://${dev ? '127.0.0.1:8080' : page.host}/api`;
