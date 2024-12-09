import { IContact, NewContact } from './contact.model';

export const sampleWithRequiredData: IContact = {
  id: 2721,
};

export const sampleWithPartialData: IContact = {
  id: 23286,
  companyName: 'in toward',
  contactName: 'állj',
  address: 'mint',
  mobile: 'alá pedig nos',
  note: 'katonás elkerülhetetlenül hasból',
};

export const sampleWithFullData: IContact = {
  id: 9237,
  companyName: 'behind bővelkedő különálló',
  taxNumber: 'ó midst',
  contactName: 'mindnyájan',
  address: 'atonális pántlika',
  email: 'Szonja_Bogdan20@outlook.com',
  fax: 'ó belül',
  mobile: 'úgyhogy',
  phone: '+36 70/001-7276',
  note: 'cárevics ó amidst',
};

export const sampleWithNewData: NewContact = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
