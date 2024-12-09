import { ICategory, NewCategory } from './category.model';

export const sampleWithRequiredData: ICategory = {
  id: 3965,
};

export const sampleWithPartialData: ICategory = {
  id: 12315,
  description: 'ebédel',
};

export const sampleWithFullData: ICategory = {
  id: 14834,
  active: false,
  categoryType: 'SUB_CATEGORY',
  description: 'salsa brr utálattal',
};

export const sampleWithNewData: NewCategory = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
