import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISoldStock, NewSoldStock } from '../sold-stock.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISoldStock for edit and NewSoldStockFormGroupInput for create.
 */
type SoldStockFormGroupInput = ISoldStock | PartialWithRequiredKeyOf<NewSoldStock>;

type SoldStockFormDefaults = Pick<NewSoldStock, 'id'>;

type SoldStockFormGroupContent = {
  id: FormControl<ISoldStock['id'] | NewSoldStock['id']>;
  supplie: FormControl<ISoldStock['supplie']>;
  unitPrice: FormControl<ISoldStock['unitPrice']>;
  returnedSupplie: FormControl<ISoldStock['returnedSupplie']>;
  soldWare: FormControl<ISoldStock['soldWare']>;
  sale: FormControl<ISoldStock['sale']>;
};

export type SoldStockFormGroup = FormGroup<SoldStockFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SoldStockFormService {
  createSoldStockFormGroup(soldStock: SoldStockFormGroupInput = { id: null }): SoldStockFormGroup {
    const soldStockRawValue = {
      ...this.getFormDefaults(),
      ...soldStock,
    };
    return new FormGroup<SoldStockFormGroupContent>({
      id: new FormControl(
        { value: soldStockRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      supplie: new FormControl(soldStockRawValue.supplie),
      unitPrice: new FormControl(soldStockRawValue.unitPrice),
      returnedSupplie: new FormControl(soldStockRawValue.returnedSupplie),
      soldWare: new FormControl(soldStockRawValue.soldWare),
      sale: new FormControl(soldStockRawValue.sale),
    });
  }

  getSoldStock(form: SoldStockFormGroup): ISoldStock | NewSoldStock {
    return form.getRawValue() as ISoldStock | NewSoldStock;
  }

  resetForm(form: SoldStockFormGroup, soldStock: SoldStockFormGroupInput): void {
    const soldStockRawValue = { ...this.getFormDefaults(), ...soldStock };
    form.reset(
      {
        ...soldStockRawValue,
        id: { value: soldStockRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SoldStockFormDefaults {
    return {
      id: null,
    };
  }
}
