import { IDisposedStock, NewDisposedStock } from './disposed-stock.model';

export const sampleWithRequiredData: IDisposedStock = {
  id: 136,
};

export const sampleWithPartialData: IDisposedStock = {
  id: 4164,
};

export const sampleWithFullData: IDisposedStock = {
  id: 10245,
  supplie: 14348,
  unitPrice: 8567,
};

export const sampleWithNewData: NewDisposedStock = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
