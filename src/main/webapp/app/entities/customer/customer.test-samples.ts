import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 11974,
};

export const sampleWithPartialData: ICustomer = {
  id: 8277,
};

export const sampleWithFullData: ICustomer = {
  id: 32185,
};

export const sampleWithNewData: NewCustomer = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
