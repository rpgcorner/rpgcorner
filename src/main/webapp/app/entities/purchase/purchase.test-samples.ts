import dayjs from 'dayjs/esm';

import { IPurchase, NewPurchase } from './purchase.model';

export const sampleWithRequiredData: IPurchase = {
  id: 700,
};

export const sampleWithPartialData: IPurchase = {
  id: 10544,
};

export const sampleWithFullData: IPurchase = {
  id: 24175,
  purchaseDate: dayjs('2024-12-09'),
};

export const sampleWithNewData: NewPurchase = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
