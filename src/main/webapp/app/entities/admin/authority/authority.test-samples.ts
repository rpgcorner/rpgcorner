import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '745d48bc-c6f7-46a5-a393-eca4373ad4a0',
};

export const sampleWithPartialData: IAuthority = {
  name: '62020325-d98a-4e38-9f04-c4db2a2b52a8',
};

export const sampleWithFullData: IAuthority = {
  name: '2448a35a-1a2a-4f00-a403-6a418ccc4716',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
