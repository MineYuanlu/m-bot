import type { NumberLike } from './common';
import type { Plan } from './Plan';

/**
 * 游戏信息
 */
export type Play = {
  /**唯一id */
  id: NumberLike;
  /**玩家名称 */
  player: string;
  /**服务器ip */
  host: string;
  /**服务器端口 */
  port: number;
  /**在线状态 */
  online: boolean;
  /**计划信息 */
  plans: {
    /**是否正在运行 */
    running: boolean;
    /**计划详情 */
    list?: Plan[];
  };
};

/**
 * 创建数据
 */
export type CreateData = {
  player: string;
  host: string;
  port: number;
  plan: string;
};
