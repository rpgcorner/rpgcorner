import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IContact } from 'app/entities/contact/contact.model';
import { ContactService } from 'app/entities/contact/service/contact.service';
import { ICustomer } from '../customer.model';
import { CustomerService } from '../service/customer.service';
import { CustomerFormGroup, CustomerFormService } from './customer-form.service';

@Component({
  standalone: true,
  selector: 'jhi-customer-update',
  templateUrl: './customer-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CustomerUpdateComponent implements OnInit {
  isSaving = false;
  customer: ICustomer | null = null;

  contactsCollection: IContact[] = [];

  protected customerService = inject(CustomerService);
  protected customerFormService = inject(CustomerFormService);
  protected contactService = inject(ContactService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CustomerFormGroup = this.customerFormService.createCustomerFormGroup();

  compareContact = (o1: IContact | null, o2: IContact | null): boolean => this.contactService.compareContact(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customer }) => {
      this.customer = customer;
      if (customer) {
        this.updateForm(customer);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const customer = this.customerFormService.getCustomer(this.editForm);
    if (customer.id !== null) {
      this.subscribeToSaveResponse(this.customerService.update(customer));
    } else {
      this.subscribeToSaveResponse(this.customerService.create(customer));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICustomer>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(customer: ICustomer): void {
    this.customer = customer;
    this.customerFormService.resetForm(this.editForm, customer);

    this.contactsCollection = this.contactService.addContactToCollectionIfMissing<IContact>(this.contactsCollection, customer.contact);
  }

  protected loadRelationshipsOptions(): void {
    this.contactService
      .query({ filter: 'customer-is-null' })
      .pipe(map((res: HttpResponse<IContact[]>) => res.body ?? []))
      .pipe(map((contacts: IContact[]) => this.contactService.addContactToCollectionIfMissing<IContact>(contacts, this.customer?.contact)))
      .subscribe((contacts: IContact[]) => (this.contactsCollection = contacts));
  }
}
