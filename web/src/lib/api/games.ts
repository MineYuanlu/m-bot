import { apiPath, handle, wsApiPath } from '$lib/def/api';
import type { AddAlert, Flush, NumberLike } from '$lib/def/common';

const subzone = 'game';
const prefix = `${apiPath}/${subzone}`;
/**
 * 运行计划
 * @param id PlayID
 * @param alert 异常通知
 */
export const runPlan = async (id: number | NumberLike, alert: AddAlert, flush?: Flush) => {
  await handle(() => fetch(`${prefix}/start-plan?id=${id}`), alert);
  flush && (await flush());
};

/**
 * 停止计划
 * @param id PlayID
 * @param alert 异常通知
 */
export const stopPlan = async (id: number | NumberLike, alert: AddAlert, flush?: Flush) => {
  await handle(() => fetch(`${prefix}/stop-plan?id=${id}`), alert);
  flush && (await flush());
};
/**
 * 获取计划
 * @param id PlayID
 * @param alert 异常通知
 */
export const getPlan = async (id: number | NumberLike, alert: AddAlert) => {
  const resp = await handle<string>(() => fetch(`${prefix}/plan?id=${id}`), alert);
  return resp?.info;
};
/**
 * 设置计划
 * @param id PlayID
 * @param plan 计划内容
 * @param alert 异常通知
 */
export const setPlan = async (id: number | NumberLike, plan: string, alert: AddAlert) => {
  const resp = await handle<string | boolean>(
    () =>
      fetch(`${prefix}/plan?id=${id}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(plan),
      }),
    alert,
  );
  return resp?.info;
};
/**
 * 断开链接
 * @param id PlayID
 * @param alert 异常通知
 */
export const disconnect = async (id: number | NumberLike, alert: AddAlert, flush?: Flush) => {
  await handle(() => fetch(`${prefix}/disconnect?id=${id}`), alert);
  flush && (await flush());
};
/**
 * 连接
 * @param id PlayID
 * @param alert 异常通知
 */
export const connect = async (id: number | NumberLike, alert: AddAlert, flush?: Flush) => {
  await handle(() => fetch(`${prefix}/connect?id=${id}`), alert);
  flush && (await flush());
};
/**
 * 发送消息/命令
 * @param id PlayID
 * @param alert 异常通知
 * @param line 消息
 */
export const chat = async (
  id: number | NumberLike,
  line: string,
  alert: AddAlert,
  flush?: Flush,
) => {
  await handle(() => fetch(`${prefix}/chat?id=${id}&chat=${encodeURIComponent(line)}`), alert);
  flush && (await flush());
};
/**
 * 获取信息的WebSocket
 */
export const getInfoWs = (url: URL) => {
  return new WebSocket(`${wsApiPath(url)}/${subzone}/info?id=${url.searchParams.get('id')}`);
};
