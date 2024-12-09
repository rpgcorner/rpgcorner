import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ISupplier } from 'app/entities/supplier/supplier.model';

export interface IPurchase {
  id: number;
  purchaseDate?: dayjs.Dayjs | null;
  purchasedByUser?: Pick<IUser, 'id'> | null;
  purchasedFromSupplier?: ISupplier | null;
}

export type NewPurchase = Omit<IPurchase, 'id'> & { id: null };
