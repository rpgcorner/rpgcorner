import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPurchasedStock, NewPurchasedStock } from '../purchased-stock.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPurchasedStock for edit and NewPurchasedStockFormGroupInput for create.
 */
type PurchasedStockFormGroupInput = IPurchasedStock | PartialWithRequiredKeyOf<NewPurchasedStock>;

type PurchasedStockFormDefaults = Pick<NewPurchasedStock, 'id'>;

type PurchasedStockFormGroupContent = {
  id: FormControl<IPurchasedStock['id'] | NewPurchasedStock['id']>;
  supplie: FormControl<IPurchasedStock['supplie']>;
  unitPrice: FormControl<IPurchasedStock['unitPrice']>;
  purchasedWare: FormControl<IPurchasedStock['purchasedWare']>;
  purchase: FormControl<IPurchasedStock['purchase']>;
};

export type PurchasedStockFormGroup = FormGroup<PurchasedStockFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PurchasedStockFormService {
  createPurchasedStockFormGroup(purchasedStock: PurchasedStockFormGroupInput = { id: null }): PurchasedStockFormGroup {
    const purchasedStockRawValue = {
      ...this.getFormDefaults(),
      ...purchasedStock,
    };
    return new FormGroup<PurchasedStockFormGroupContent>({
      id: new FormControl(
        { value: purchasedStockRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      supplie: new FormControl(purchasedStockRawValue.supplie),
      unitPrice: new FormControl(purchasedStockRawValue.unitPrice),
      purchasedWare: new FormControl(purchasedStockRawValue.purchasedWare),
      purchase: new FormControl(purchasedStockRawValue.purchase),
    });
  }

  getPurchasedStock(form: PurchasedStockFormGroup): IPurchasedStock | NewPurchasedStock {
    return form.getRawValue() as IPurchasedStock | NewPurchasedStock;
  }

  resetForm(form: PurchasedStockFormGroup, purchasedStock: PurchasedStockFormGroupInput): void {
    const purchasedStockRawValue = { ...this.getFormDefaults(), ...purchasedStock };
    form.reset(
      {
        ...purchasedStockRawValue,
        id: { value: purchasedStockRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PurchasedStockFormDefaults {
    return {
      id: null,
    };
  }
}
