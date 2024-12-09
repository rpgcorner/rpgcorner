import dayjs from 'dayjs/esm';

import { IDispose, NewDispose } from './dispose.model';

export const sampleWithRequiredData: IDispose = {
  id: 18563,
};

export const sampleWithPartialData: IDispose = {
  id: 32115,
  disposeDate: dayjs('2024-12-09'),
};

export const sampleWithFullData: IDispose = {
  id: 13817,
  disposeDate: dayjs('2024-12-09'),
  note: 'hajlíthatatlan',
};

export const sampleWithNewData: NewDispose = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
