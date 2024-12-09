import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 31594,
  login: 'g?^&H@MDcZGo\\HTH5Sd\\KGNyxGE\\gJGUa6\\LQsQuI',
};

export const sampleWithPartialData: IUser = {
  id: 7858,
  login: 'fO@N8',
};

export const sampleWithFullData: IUser = {
  id: 10792,
  login: 'D8JHyF',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
