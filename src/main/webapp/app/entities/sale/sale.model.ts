import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ICustomer } from 'app/entities/customer/customer.model';

export interface ISale {
  id: number;
  soldDate?: dayjs.Dayjs | null;
  soldByUser?: Pick<IUser, 'id'> | null;
  soldForCustomer?: ICustomer | null;
}

export type NewSale = Omit<ISale, 'id'> & { id: null };
