import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPurchase, NewPurchase } from '../purchase.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPurchase for edit and NewPurchaseFormGroupInput for create.
 */
type PurchaseFormGroupInput = IPurchase | PartialWithRequiredKeyOf<NewPurchase>;

type PurchaseFormDefaults = Pick<NewPurchase, 'id' | 'transactionClosed'>;

type PurchaseFormGroupContent = {
  id: FormControl<IPurchase['id'] | NewPurchase['id']>;
  transactionClosed: FormControl<IPurchase['transactionClosed']>;
  purchaseDate: FormControl<IPurchase['purchaseDate']>;
  purchasedByUser: FormControl<IPurchase['purchasedByUser']>;
  purchasedFromSupplier: FormControl<IPurchase['purchasedFromSupplier']>;
};

export type PurchaseFormGroup = FormGroup<PurchaseFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PurchaseFormService {
  createPurchaseFormGroup(purchase: PurchaseFormGroupInput = { id: null }): PurchaseFormGroup {
    const purchaseRawValue = {
      ...this.getFormDefaults(),
      ...purchase,
    };
    return new FormGroup<PurchaseFormGroupContent>({
      id: new FormControl(
        { value: purchaseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      transactionClosed: new FormControl(purchaseRawValue.transactionClosed),
      purchaseDate: new FormControl(purchaseRawValue.purchaseDate),
      purchasedByUser: new FormControl(purchaseRawValue.purchasedByUser, {
        validators: [Validators.required],
      }),
      purchasedFromSupplier: new FormControl(purchaseRawValue.purchasedFromSupplier, {
        validators: [Validators.required],
      }),
    });
  }

  getPurchase(form: PurchaseFormGroup): IPurchase | NewPurchase {
    return form.getRawValue() as IPurchase | NewPurchase;
  }

  resetForm(form: PurchaseFormGroup, purchase: PurchaseFormGroupInput): void {
    const purchaseRawValue = { ...this.getFormDefaults(), ...purchase };
    form.reset(
      {
        ...purchaseRawValue,
        id: { value: purchaseRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PurchaseFormDefaults {
    return {
      id: null,
      transactionClosed: false, // Alapértelmezett érték
    };
  }
}
