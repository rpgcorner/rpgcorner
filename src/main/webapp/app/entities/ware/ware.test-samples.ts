import dayjs from 'dayjs/esm';

import { IWare, NewWare } from './ware.model';

export const sampleWithRequiredData: IWare = {
  id: 5560,
};

export const sampleWithPartialData: IWare = {
  id: 6643,
  active: false,
  created: dayjs('2024-12-09'),
  productCode: 'kiváltképpen áhítatoskodik modulo',
};

export const sampleWithFullData: IWare = {
  id: 5772,
  active: true,
  created: dayjs('2024-12-09'),
  name: 'pozitív vele sans',
  description: 'hihihi felizgul',
  productCode: 'ám olajoz',
};

export const sampleWithNewData: NewWare = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
