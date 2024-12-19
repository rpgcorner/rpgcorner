import { ISupplier } from 'app/entities/supplier/supplier.model';

export interface IContact {
  id: number;
  contactName?: string | null;
  address?: string | null;
  email?: string | null;
  fax?: string | null;
  mobile?: string | null;
  phone?: string | null;
  note?: string | null;
  supplier?: ISupplier | null;
}

export type NewContact = Omit<IContact, 'id'> & { id: null };
