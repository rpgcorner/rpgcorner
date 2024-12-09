import { IInventory, NewInventory } from './inventory.model';

export const sampleWithRequiredData: IInventory = {
  id: 20017,
};

export const sampleWithPartialData: IInventory = {
  id: 11939,
};

export const sampleWithFullData: IInventory = {
  id: 1436,
  supplie: 18090,
};

export const sampleWithNewData: NewInventory = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
