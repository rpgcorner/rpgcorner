import dayjs from 'dayjs/esm';
import { ISale } from 'app/entities/sale/sale.model';
import { IUser } from 'app/entities/user/user.model';
import { ICustomer } from 'app/entities/customer/customer.model';

export interface IProductReturn {
  id: number;
  returnDate?: dayjs.Dayjs | null;
  note?: string | null;
  sale?: ISale | null;
  returnedByUser?: IUser | null;
  returnedByCustomer?: ICustomer | null;
  transactionClosed?: boolean | null;
}

export type NewProductReturn = Omit<IProductReturn, 'id'> & { id: null };
