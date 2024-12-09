import { ISoldStock, NewSoldStock } from './sold-stock.model';

export const sampleWithRequiredData: ISoldStock = {
  id: 30581,
};

export const sampleWithPartialData: ISoldStock = {
  id: 14014,
  supplie: 12576,
  unitPrice: 7188,
};

export const sampleWithFullData: ISoldStock = {
  id: 18639,
  supplie: 18747,
  unitPrice: 20609,
  returnedSupplie: 12376,
};

export const sampleWithNewData: NewSoldStock = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
