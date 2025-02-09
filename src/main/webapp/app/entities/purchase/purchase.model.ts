import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ISupplier } from 'app/entities/supplier/supplier.model';

export interface IPurchase {
  id: number;
  purchaseDate?: dayjs.Dayjs | null;
  purchasedByUser?: Pick<IUser, 'id' | 'firstName'> | null; // Hozzáadjuk a 'firstName'-t
  purchasedFromSupplier?: ISupplier | null;
  transactionClosed?: boolean | null;
}

export type NewPurchase = Omit<IPurchase, 'id'> & { id: null };
