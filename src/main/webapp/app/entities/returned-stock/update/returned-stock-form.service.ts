import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IReturnedStock, NewReturnedStock } from '../returned-stock.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReturnedStock for edit and NewReturnedStockFormGroupInput for create.
 */
type ReturnedStockFormGroupInput = IReturnedStock | PartialWithRequiredKeyOf<NewReturnedStock>;

type ReturnedStockFormDefaults = Pick<NewReturnedStock, 'id'>;

type ReturnedStockFormGroupContent = {
  id: FormControl<IReturnedStock['id'] | NewReturnedStock['id']>;
  supplie: FormControl<IReturnedStock['supplie']>;
  unitPrice: FormControl<IReturnedStock['unitPrice']>;
  returnedWare: FormControl<IReturnedStock['returnedWare']>;
  productReturn: FormControl<IReturnedStock['productReturn']>;
};

export type ReturnedStockFormGroup = FormGroup<ReturnedStockFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReturnedStockFormService {
  createReturnedStockFormGroup(returnedStock: ReturnedStockFormGroupInput = { id: null }): ReturnedStockFormGroup {
    const returnedStockRawValue = {
      ...this.getFormDefaults(),
      ...returnedStock,
    };
    return new FormGroup<ReturnedStockFormGroupContent>({
      id: new FormControl(
        { value: returnedStockRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      supplie: new FormControl(returnedStockRawValue.supplie),
      unitPrice: new FormControl(returnedStockRawValue.unitPrice),
      returnedWare: new FormControl(returnedStockRawValue.returnedWare),
      productReturn: new FormControl(returnedStockRawValue.productReturn),
    });
  }

  getReturnedStock(form: ReturnedStockFormGroup): IReturnedStock | NewReturnedStock {
    return form.getRawValue() as IReturnedStock | NewReturnedStock;
  }

  resetForm(form: ReturnedStockFormGroup, returnedStock: ReturnedStockFormGroupInput): void {
    const returnedStockRawValue = { ...this.getFormDefaults(), ...returnedStock };
    form.reset(
      {
        ...returnedStockRawValue,
        id: { value: returnedStockRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ReturnedStockFormDefaults {
    return {
      id: null,
    };
  }
}
