import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 18395,
  login: 'cMuwz1',
};

export const sampleWithPartialData: IUser = {
  id: 12242,
  login: 'jS7iz',
};

export const sampleWithFullData: IUser = {
  id: 21227,
  login: 'qTI7Z',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
