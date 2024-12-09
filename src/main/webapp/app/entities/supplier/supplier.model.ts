export interface ISupplier {
  id: number;
}

export type NewSupplier = Omit<ISupplier, 'id'> & { id: null };
