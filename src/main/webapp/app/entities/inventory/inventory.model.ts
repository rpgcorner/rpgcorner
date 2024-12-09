import { IWare } from 'app/entities/ware/ware.model';

export interface IInventory {
  id: number;
  supplie?: number | null;
  ware?: IWare | null;
}

export type NewInventory = Omit<IInventory, 'id'> & { id: null };
