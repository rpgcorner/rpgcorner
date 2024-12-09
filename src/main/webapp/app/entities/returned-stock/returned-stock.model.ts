import { IWare } from 'app/entities/ware/ware.model';
import { IProductReturn } from 'app/entities/product-return/product-return.model';

export interface IReturnedStock {
  id: number;
  supplie?: number | null;
  unitPrice?: number | null;
  returnedWare?: IWare | null;
  productReturn?: IProductReturn | null;
}

export type NewReturnedStock = Omit<IReturnedStock, 'id'> & { id: null };
