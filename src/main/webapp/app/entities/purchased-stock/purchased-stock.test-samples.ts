import { IPurchasedStock, NewPurchasedStock } from './purchased-stock.model';

export const sampleWithRequiredData: IPurchasedStock = {
  id: 19468,
};

export const sampleWithPartialData: IPurchasedStock = {
  id: 62,
  unitPrice: 26872,
};

export const sampleWithFullData: IPurchasedStock = {
  id: 1556,
  supplie: 331,
  unitPrice: 1859,
};

export const sampleWithNewData: NewPurchasedStock = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
