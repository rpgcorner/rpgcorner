/* eslint-disable */
import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISupplier, NewSupplier } from '../supplier.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISupplier for edit and NewSupplierFormGroupInput for create.
 */
type SupplierFormGroupInput = ISupplier | PartialWithRequiredKeyOf<NewSupplier>;

type SupplierFormDefaults = Pick<NewSupplier, 'id' | 'companyName' | 'taxNumber'>;

type SupplierFormGroupContent = {
  id: FormControl<ISupplier['id'] | NewSupplier['id']>;
  companyName: FormControl<ISupplier['companyName'] | NewSupplier['companyName']>;
  taxNumber: FormControl<ISupplier['taxNumber'] | NewSupplier['taxNumber']>;
};

export type SupplierFormGroup = FormGroup<SupplierFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SupplierFormService {
  createSupplierFormGroup(supplier: SupplierFormGroupInput = { id: null }): SupplierFormGroup {
    const supplierRawValue = {
      ...this.getFormDefaults(),
      ...supplier,
    };
    return new FormGroup<SupplierFormGroupContent>({
      id: new FormControl(
        { value: supplierRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      companyName: new FormControl(
        { value: supplierRawValue.companyName, disabled: false },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      taxNumber: new FormControl(
        { value: supplierRawValue.taxNumber, disabled: false },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
    });
  }

  getSupplier(form: SupplierFormGroup): NewSupplier {
    return form.getRawValue() as NewSupplier;
  }

  resetForm(form: SupplierFormGroup, supplier: SupplierFormGroupInput): void {
    const supplierRawValue = { ...this.getFormDefaults(), ...supplier };
    form.reset(
      {
        ...supplierRawValue,
        id: { value: supplierRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SupplierFormDefaults {
    return {
      id: null,
      companyName: '',
      taxNumber: '',
    };
  }
}
