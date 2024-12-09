import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'f98862b5-05b2-4b60-a164-5b71de8352cf',
};

export const sampleWithPartialData: IAuthority = {
  name: 'feddfca8-c9fa-446e-bb0d-825159235acd',
};

export const sampleWithFullData: IAuthority = {
  name: '4ac25c50-67a2-464f-ae02-92a3fbd87990',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
