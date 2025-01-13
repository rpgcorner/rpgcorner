import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ICustomer } from 'app/entities/customer/customer.model';

export interface ISale {
  id: number;
  soldDate?: dayjs.Dayjs | null;
  soldByUser?: IUser | null; // A teljes IUser típus kell
  soldForCustomer?: ICustomer | null;
  transactionClosed?: boolean | null;
}

export type NewSale = Omit<ISale, 'id'> & { id: null };
