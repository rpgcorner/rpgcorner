import { IReturnedStock, NewReturnedStock } from './returned-stock.model';

export const sampleWithRequiredData: IReturnedStock = {
  id: 11898,
};

export const sampleWithPartialData: IReturnedStock = {
  id: 30225,
  unitPrice: 29581,
};

export const sampleWithFullData: IReturnedStock = {
  id: 15876,
  supplie: 15275,
  unitPrice: 19922,
};

export const sampleWithNewData: NewReturnedStock = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
