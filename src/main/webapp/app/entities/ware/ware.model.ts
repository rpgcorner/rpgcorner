import dayjs from 'dayjs/esm';
import { ICategory } from 'app/entities/category/category.model';

export interface IWare {
  id: number;
  active?: boolean | null;
  created?: dayjs.Dayjs | null;
  name?: string | null;
  description?: string | null;
  productCode?: string | null;
  mainCategory?: ICategory | null;
  subCategory?: ICategory | null;
}

export type NewWare = Omit<IWare, 'id'> & { id: null };
