import { IContact } from 'app/entities/contact/contact.model';

export interface ICustomer {
  id: number;
  contact?: IContact | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
