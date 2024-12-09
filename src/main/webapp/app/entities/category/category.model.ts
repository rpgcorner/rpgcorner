import { CategoryTypeEnum } from 'app/entities/enumerations/category-type-enum.model';

export interface ICategory {
  id: number;
  active?: boolean | null;
  categoryType?: keyof typeof CategoryTypeEnum | null;
  description?: string | null;
  mainCategory?: ICategory | null;
}

export type NewCategory = Omit<ICategory, 'id'> & { id: null };
