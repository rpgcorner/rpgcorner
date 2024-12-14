/* eslint-disable */
import { Component, OnInit, inject, Input, ViewChild } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISupplier } from 'app/entities/supplier/supplier.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';
import { IContact } from '../contact.model';
import { ContactService } from '../service/contact.service';
import { ContactFormGroup, ContactFormService } from './contact-form.service';
import { CustomerUpdateComponent } from '../../customer/update/customer-update.component';

@Component({
  standalone: true,
  selector: 'jhi-contact-update',
  templateUrl: './contact-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ContactUpdateComponent implements OnInit {
  @Input() isVisible: boolean = true; // Alapértelmezett érték: false
  isSaving = false;
  contact: IContact | null = null;

  suppliersSharedCollection: ISupplier[] = [];

  protected contactService = inject(ContactService);
  protected contactFormService = inject(ContactFormService);
  protected supplierService = inject(SupplierService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ContactFormGroup = this.contactFormService.createContactFormGroup();

  compareSupplier = (o1: ISupplier | null, o2: ISupplier | null): boolean => this.supplierService.compareSupplier(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ contact }) => {
      this.contact = contact;
      if (contact) {
        this.updateForm(contact);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): Observable<IContact> {
    this.isSaving = true;
    const contact = this.contactFormService.getContact(this.editForm);

    let saveObservable: Observable<HttpResponse<IContact>>;
    if (contact.id !== null) {
      saveObservable = this.contactService.update(contact);
    } else {
      saveObservable = this.contactService.create(contact);
    }

    return saveObservable.pipe(
      map((response: HttpResponse<IContact>) => response.body!), // Extract the body (IContact)
      finalize(() => (this.isSaving = false)),
    );
  }
  protected subscribeToSaveResponse(result: Observable<HttpResponse<IContact>>): void {
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

  public updateForm(contact: IContact): void {
    this.contact = contact;
    this.contactFormService.resetForm(this.editForm, contact);

    this.suppliersSharedCollection = this.supplierService.addSupplierToCollectionIfMissing<ISupplier>(
      this.suppliersSharedCollection,
      contact.supplier,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.supplierService
      .query()
      .pipe(map((res: HttpResponse<ISupplier[]>) => res.body ?? []))
      .pipe(
        map((suppliers: ISupplier[]) =>
          this.supplierService.addSupplierToCollectionIfMissing<ISupplier>(suppliers, this.contact?.supplier),
        ),
      )
      .subscribe((suppliers: ISupplier[]) => (this.suppliersSharedCollection = suppliers));
  }
}
