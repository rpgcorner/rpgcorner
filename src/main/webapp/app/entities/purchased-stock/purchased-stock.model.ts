import { IWare } from 'app/entities/ware/ware.model';
import { IPurchase } from 'app/entities/purchase/purchase.model';

export interface IPurchasedStock {
  id: number;
  supplie?: number | null;
  unitPrice?: number | null;
  purchasedWare?: IWare | null;
  purchase?: IPurchase | null;
}

export type NewPurchasedStock = Omit<IPurchasedStock, 'id'> & { id: null };
