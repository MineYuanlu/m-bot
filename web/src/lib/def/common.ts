import type { AlertType } from './bootstrap';

export type MaybePromise<T> = T | Promise<T>;
export type MaybeFunction<T> = T | (() => T);

export type NumberLike = string;

/**
 * 添加alert的函数
 */
export type AddAlert = (message: any, type: AlertType) => void;

export type Flush = () => MaybePromise<void>;
