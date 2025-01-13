import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDispose, NewDispose } from '../dispose.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDispose for edit and NewDisposeFormGroupInput for create.
 */
type DisposeFormGroupInput = IDispose | PartialWithRequiredKeyOf<NewDispose>;

type DisposeFormDefaults = Pick<NewDispose, 'id'>;

type DisposeFormGroupContent = {
  id: FormControl<IDispose['id'] | NewDispose['id']>;
  transactionClosed: FormControl<IDispose['transactionClosed']>;
  disposeDate: FormControl<IDispose['disposeDate']>;
  note: FormControl<IDispose['note']>;
  disposedByUser: FormControl<IDispose['disposedByUser']>;
};

export type DisposeFormGroup = FormGroup<DisposeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DisposeFormService {
  createDisposeFormGroup(dispose: DisposeFormGroupInput = { id: null }): DisposeFormGroup {
    const disposeRawValue = {
      ...this.getFormDefaults(),
      ...dispose,
    };
    return new FormGroup<DisposeFormGroupContent>({
      id: new FormControl(
        { value: disposeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      transactionClosed: new FormControl(disposeRawValue.transactionClosed),
      disposeDate: new FormControl(disposeRawValue.disposeDate),
      note: new FormControl(disposeRawValue.note),
      disposedByUser: new FormControl(disposeRawValue.disposedByUser, {
        validators: [Validators.required],
      }),
    });
  }

  getDispose(form: DisposeFormGroup): IDispose | NewDispose {
    return form.getRawValue() as IDispose | NewDispose;
  }

  resetForm(form: DisposeFormGroup, dispose: DisposeFormGroupInput): void {
    const disposeRawValue = { ...this.getFormDefaults(), ...dispose };
    form.reset(
      {
        ...disposeRawValue,
        id: { value: disposeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DisposeFormDefaults {
    return {
      id: null,
    };
  }
}
