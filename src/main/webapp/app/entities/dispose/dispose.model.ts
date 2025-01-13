import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IDispose {
  id: number;
  disposeDate?: dayjs.Dayjs | null;
  note?: string | null;
  disposedByUser?: IUser | null;
  transactionClosed?: boolean | null;
}

export type NewDispose = Omit<IDispose, 'id'> & { id: null };
