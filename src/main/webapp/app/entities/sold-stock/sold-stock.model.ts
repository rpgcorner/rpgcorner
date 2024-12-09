import { IWare } from 'app/entities/ware/ware.model';
import { ISale } from 'app/entities/sale/sale.model';

export interface ISoldStock {
  id: number;
  supplie?: number | null;
  unitPrice?: number | null;
  returnedSupplie?: number | null;
  soldWare?: IWare | null;
  sale?: ISale | null;
}

export type NewSoldStock = Omit<ISoldStock, 'id'> & { id: null };
