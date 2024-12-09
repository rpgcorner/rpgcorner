import { ISupplier, NewSupplier } from './supplier.model';

export const sampleWithRequiredData: ISupplier = {
  id: 6274,
};

export const sampleWithPartialData: ISupplier = {
  id: 944,
};

export const sampleWithFullData: ISupplier = {
  id: 8084,
};

export const sampleWithNewData: NewSupplier = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
