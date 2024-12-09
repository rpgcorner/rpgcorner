import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDisposedStock, NewDisposedStock } from '../disposed-stock.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDisposedStock for edit and NewDisposedStockFormGroupInput for create.
 */
type DisposedStockFormGroupInput = IDisposedStock | PartialWithRequiredKeyOf<NewDisposedStock>;

type DisposedStockFormDefaults = Pick<NewDisposedStock, 'id'>;

type DisposedStockFormGroupContent = {
  id: FormControl<IDisposedStock['id'] | NewDisposedStock['id']>;
  supplie: FormControl<IDisposedStock['supplie']>;
  unitPrice: FormControl<IDisposedStock['unitPrice']>;
  disposedWare: FormControl<IDisposedStock['disposedWare']>;
  dispose: FormControl<IDisposedStock['dispose']>;
};

export type DisposedStockFormGroup = FormGroup<DisposedStockFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DisposedStockFormService {
  createDisposedStockFormGroup(disposedStock: DisposedStockFormGroupInput = { id: null }): DisposedStockFormGroup {
    const disposedStockRawValue = {
      ...this.getFormDefaults(),
      ...disposedStock,
    };
    return new FormGroup<DisposedStockFormGroupContent>({
      id: new FormControl(
        { value: disposedStockRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      supplie: new FormControl(disposedStockRawValue.supplie),
      unitPrice: new FormControl(disposedStockRawValue.unitPrice),
      disposedWare: new FormControl(disposedStockRawValue.disposedWare),
      dispose: new FormControl(disposedStockRawValue.dispose),
    });
  }

  getDisposedStock(form: DisposedStockFormGroup): IDisposedStock | NewDisposedStock {
    return form.getRawValue() as IDisposedStock | NewDisposedStock;
  }

  resetForm(form: DisposedStockFormGroup, disposedStock: DisposedStockFormGroupInput): void {
    const disposedStockRawValue = { ...this.getFormDefaults(), ...disposedStock };
    form.reset(
      {
        ...disposedStockRawValue,
        id: { value: disposedStockRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DisposedStockFormDefaults {
    return {
      id: null,
    };
  }
}
