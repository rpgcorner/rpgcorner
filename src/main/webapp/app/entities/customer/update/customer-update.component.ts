/* eslint-disable */
import { Component, OnInit, inject, ViewChild } from '@angular/core';
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
import { ContactUpdateComponent } from '../../contact/update/contact-update.component';

@Component({
  standalone: true,
  selector: 'jhi-customer-update',
  templateUrl: './customer-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, ContactUpdateComponent],
})
export class CustomerUpdateComponent implements OnInit {
  @ViewChild(ContactUpdateComponent) contactUpdateComponent!: ContactUpdateComponent;
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
      if (customer.contact?.id) {
        this.loadContact(customer.contact.id); // Meghívod a loadContact metódust
      }
      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;

    // Contact mentése
    this.contactUpdateComponent.save().subscribe({
      next: (savedContact: IContact) => {
        console.log('Saved Contact:', savedContact);

        // Customer mentése a Contact ID-val
        const customer = this.customerFormService.getCustomer(this.editForm);
        customer.contact = savedContact; // A Contact ID hozzáadása

        let saveObservable: Observable<HttpResponse<ICustomer>>;
        if (customer.id !== null) {
          saveObservable = this.customerService.update(customer);
        } else {
          saveObservable = this.customerService.create(customer);
        }

        saveObservable.pipe(finalize(() => this.onSaveFinalize())).subscribe({
          next: () => this.onSaveSuccess(),
          error: () => this.onSaveError(),
        });
      },
      error: () => {
        console.error('Failed to save Contact');
        this.isSaving = false;
      },
    });
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

  protected loadContact(contactId: number): void {
    this.contactService.find(contactId).subscribe({
      next: (response: HttpResponse<IContact>) => {
        if (response.body) {
          this.contactUpdateComponent.updateForm(response.body);
          this.contactsCollection = [response.body];
          console.log('Contact loaded:', response.body);
        } else {
          console.error('Contact not found');
        }
      },
      error: err => {
        console.error('Error loading contact:', err);
      },
    });
  }
}
