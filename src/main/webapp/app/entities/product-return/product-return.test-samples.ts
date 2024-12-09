import dayjs from 'dayjs/esm';

import { IProductReturn, NewProductReturn } from './product-return.model';

export const sampleWithRequiredData: IProductReturn = {
  id: 9952,
};

export const sampleWithPartialData: IProductReturn = {
  id: 30950,
  note: 'közvetlenül azonban',
};

export const sampleWithFullData: IProductReturn = {
  id: 20162,
  returnDate: dayjs('2024-12-09'),
  note: 'kisajtolás azazhogy',
};

export const sampleWithNewData: NewProductReturn = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
