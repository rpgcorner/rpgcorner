/* eslint-disable */
import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProductReturn, NewProductReturn } from '../product-return.model';
import { ISale } from '../../sale/sale.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProductReturn for edit and NewProductReturnFormGroupInput for create.
 */
type ProductReturnFormGroupInput = IProductReturn | PartialWithRequiredKeyOf<NewProductReturn>;

type ProductReturnFormDefaults = Pick<NewProductReturn, 'id'>;

type ProductReturnFormGroupContent = {
  id: FormControl<IProductReturn['id'] | NewProductReturn['id']>;
  transactionClosed: FormControl<ISale['transactionClosed']>;
  returnDate: FormControl<IProductReturn['returnDate']>;
  note: FormControl<IProductReturn['note']>;
  sale: FormControl<IProductReturn['sale']>;
  returnedByUser: FormControl<IProductReturn['returnedByUser']>;
  returnedByCustomer: FormControl<IProductReturn['returnedByCustomer']>;
};

export type ProductReturnFormGroup = FormGroup<ProductReturnFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductReturnFormService {
  createProductReturnFormGroup(productReturn: ProductReturnFormGroupInput = { id: null }): ProductReturnFormGroup {
    const productReturnRawValue = {
      ...this.getFormDefaults(),
      ...productReturn,
    };
    return new FormGroup<ProductReturnFormGroupContent>({
      id: new FormControl(
        { value: productReturnRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      transactionClosed: new FormControl(productReturnRawValue.transactionClosed),
      returnDate: new FormControl(productReturnRawValue.returnDate),
      note: new FormControl(productReturnRawValue.note),
      sale: new FormControl(productReturnRawValue.sale),
      returnedByUser: new FormControl(productReturnRawValue.returnedByUser),
      returnedByCustomer: new FormControl(productReturnRawValue.returnedByCustomer),
    });
  }

  getProductReturn(form: ProductReturnFormGroup): IProductReturn | NewProductReturn {
    return form.getRawValue() as IProductReturn | NewProductReturn;
  }

  resetForm(form: ProductReturnFormGroup, productReturn: ProductReturnFormGroupInput): void {
    const productReturnRawValue = { ...this.getFormDefaults(), ...productReturn };
    form.reset(
      {
        ...productReturnRawValue,
        id: { value: productReturnRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProductReturnFormDefaults {
    return {
      id: null,
    };
  }
}
