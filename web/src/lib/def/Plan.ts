import type { NumberLike } from './common';

export type PlanConnect = {
  type: 'connect';
  args: [];
};
export type PlanDisconnect = {
  type: 'disconnect';
  args: string[];
};
export type PlanChat = {
  type: 'chat';
  args: string[];
};
export type PlanDelay = {
  type: 'delay';
  args: [NumberLike];
};

export type Plan = PlanConnect | PlanDisconnect | PlanChat | PlanDelay;
