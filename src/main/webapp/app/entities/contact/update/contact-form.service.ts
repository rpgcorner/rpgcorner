import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IContact, NewContact } from '../contact.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IContact for edit and NewContactFormGroupInput for create.
 */
type ContactFormGroupInput = IContact | PartialWithRequiredKeyOf<NewContact>;

type ContactFormDefaults = Pick<NewContact, 'id'>;

type ContactFormGroupContent = {
  id: FormControl<IContact['id'] | NewContact['id']>;
  companyName: FormControl<IContact['companyName']>;
  taxNumber: FormControl<IContact['taxNumber']>;
  contactName: FormControl<IContact['contactName']>;
  address: FormControl<IContact['address']>;
  email: FormControl<IContact['email']>;
  fax: FormControl<IContact['fax']>;
  mobile: FormControl<IContact['mobile']>;
  phone: FormControl<IContact['phone']>;
  note: FormControl<IContact['note']>;
  supplier: FormControl<IContact['supplier']>;
};

export type ContactFormGroup = FormGroup<ContactFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ContactFormService {
  createContactFormGroup(contact: ContactFormGroupInput = { id: null }): ContactFormGroup {
    const contactRawValue = {
      ...this.getFormDefaults(),
      ...contact,
    };
    return new FormGroup<ContactFormGroupContent>({
      id: new FormControl(
        { value: contactRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      companyName: new FormControl(contactRawValue.companyName),
      taxNumber: new FormControl(contactRawValue.taxNumber),
      contactName: new FormControl(contactRawValue.contactName),
      address: new FormControl(contactRawValue.address),
      email: new FormControl(contactRawValue.email),
      fax: new FormControl(contactRawValue.fax),
      mobile: new FormControl(contactRawValue.mobile),
      phone: new FormControl(contactRawValue.phone),
      note: new FormControl(contactRawValue.note),
      supplier: new FormControl(contactRawValue.supplier),
    });
  }

  getContact(form: ContactFormGroup): IContact | NewContact {
    return form.getRawValue() as IContact | NewContact;
  }

  resetForm(form: ContactFormGroup, contact: ContactFormGroupInput): void {
    const contactRawValue = { ...this.getFormDefaults(), ...contact };
    form.reset(
      {
        ...contactRawValue,
        id: { value: contactRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ContactFormDefaults {
    return {
      id: null,
    };
  }
}
