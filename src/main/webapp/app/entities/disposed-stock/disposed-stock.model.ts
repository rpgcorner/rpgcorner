import { IWare } from 'app/entities/ware/ware.model';
import { IDispose } from 'app/entities/dispose/dispose.model';

export interface IDisposedStock {
  id: number;
  supplie?: number | null;
  unitPrice?: number | null;
  disposedWare?: IWare | null;
  dispose?: IDispose | null;
}

export type NewDisposedStock = Omit<IDisposedStock, 'id'> & { id: null };
