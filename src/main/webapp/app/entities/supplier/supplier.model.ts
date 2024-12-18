export interface ISupplier {
  id: number;
  companyName: string;
  taxNumber: string;
}

export type NewSupplier = Omit<ISupplier, 'id'> & { id: null; companyName: string; taxNumber: string };
