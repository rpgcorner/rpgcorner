import dayjs from 'dayjs/esm';

import { ISale, NewSale } from './sale.model';

export const sampleWithRequiredData: ISale = {
  id: 30893,
};

export const sampleWithPartialData: ISale = {
  id: 9970,
};

export const sampleWithFullData: ISale = {
  id: 17965,
  soldDate: dayjs('2024-12-09'),
};

export const sampleWithNewData: NewSale = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
