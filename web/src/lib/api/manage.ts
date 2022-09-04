import { apiPath, handle } from '$lib/def/api';
import type { AddAlert, NumberLike } from '$lib/def/common';
import type { CreateData, Play } from '$lib/def/Play';

const prefix = `${apiPath}/manage`;
/**
 * 创建游戏
 * @param data 创建数据
 * @param alert 异常通知
 * @return 创建的游戏ID
 */
export const create = async (data: CreateData, alert: AddAlert) => {
  const resp = await handle<NumberLike>(
    () =>
      fetch(`${prefix}/create`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      }),
    alert,
  );
  return resp?.info;
};

/**
 * 删除游戏
 * @param id PlayID
 * @param alert 异常通知
 * @return 删除结果
 */
export const remove = async (id: number | NumberLike, alert: AddAlert) => {
  const resp = await handle<boolean>(() => fetch(`${prefix}/remove?id=${id}`), alert);
  return resp?.info;
};
/**
 * 列出游戏
 * @param alert 异常通知
 * @return 游戏信息列表
 */
export const list = async (alert: AddAlert) => {
  const resp = await handle<Play[]>(() => fetch(`${prefix}/list`), alert);

  return resp?.info;
};

/**
 * 获取游戏
 * @param id PlayID
 * @param withList 是否附带计划列表
 * @param alert 异常通知
 * @return 游戏信息
 */
export const get = async (id: number | NumberLike, withList: boolean, alert: AddAlert) => {
  const resp = await handle<Play>(() => fetch(`${prefix}/get?id=${id}&list=${!!withList}`), alert);
  return resp?.info;
};
