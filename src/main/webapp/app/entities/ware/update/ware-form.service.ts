import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IWare, NewWare } from '../ware.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWare for edit and NewWareFormGroupInput for create.
 */
type WareFormGroupInput = IWare | PartialWithRequiredKeyOf<NewWare>;

type WareFormDefaults = Pick<NewWare, 'id' | 'active'>;

type WareFormGroupContent = {
  id: FormControl<IWare['id'] | NewWare['id']>;
  active: FormControl<IWare['active']>;
  created: FormControl<IWare['created']>;
  name: FormControl<IWare['name']>;
  description: FormControl<IWare['description']>;
  productCode: FormControl<IWare['productCode']>;
  mainCategory: FormControl<IWare['mainCategory']>;
  subCategory: FormControl<IWare['subCategory']>;
};

export type WareFormGroup = FormGroup<WareFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class WareFormService {
  createWareFormGroup(ware: WareFormGroupInput = { id: null }): WareFormGroup {
    const wareRawValue = {
      ...this.getFormDefaults(),
      ...ware,
    };
    return new FormGroup<WareFormGroupContent>({
      id: new FormControl(
        { value: wareRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      active: new FormControl(wareRawValue.active),
      created: new FormControl(wareRawValue.created),
      name: new FormControl(wareRawValue.name),
      description: new FormControl(wareRawValue.description),
      productCode: new FormControl(wareRawValue.productCode),
      mainCategory: new FormControl(wareRawValue.mainCategory, {
        validators: [Validators.required],
      }),
      subCategory: new FormControl(wareRawValue.subCategory),
    });
  }

  getWare(form: WareFormGroup): IWare | NewWare {
    return form.getRawValue() as IWare | NewWare;
  }

  resetForm(form: WareFormGroup, ware: WareFormGroupInput): void {
    const wareRawValue = { ...this.getFormDefaults(), ...ware };
    form.reset(
      {
        ...wareRawValue,
        id: { value: wareRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): WareFormDefaults {
    return {
      id: null,
      active: false,
    };
  }
}
