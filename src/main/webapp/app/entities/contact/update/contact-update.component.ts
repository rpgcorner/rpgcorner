/* eslint-disable */
import { Component, OnInit, inject, Input, ViewChild } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { catchError, finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISupplier } from 'app/entities/supplier/supplier.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';
import { IContact } from '../contact.model';
import { ContactService } from '../service/contact.service';
import { ContactFormGroup, ContactFormService } from './contact-form.service';
import { CustomerUpdateComponent } from '../../customer/update/customer-update.component';
import { IDispose } from '../../dispose/dispose.model';

@Component({
  standalone: true,
  selector: 'jhi-contact-update',
  templateUrl: './contact-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ContactUpdateComponent implements OnInit {
  @Input() isVisible: boolean = true;
  isSaving = false;
  contact: IContact | null = null;
  supplierId: number = 0;
  suppliersSharedCollection: ISupplier[] = [];

  protected contactService = inject(ContactService);
  protected contactFormService = inject(ContactFormService);
  protected supplierService = inject(SupplierService);
  protected activatedRoute = inject(ActivatedRoute);

  editForm: ContactFormGroup = this.contactFormService.createContactFormGroup();

  compareSupplier = (o1: ISupplier | null, o2: ISupplier | null): boolean => this.supplierService.compareSupplier(o1, o2);

  ngOnInit(): void {
    if (this.isVisible === undefined) {
      this.isVisible = true;
    }
    this.supplierId = Number(this.activatedRoute.snapshot.params['supplierId']);
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
    debugger;
    this.isSaving = true;
    const contact = this.contactFormService.getContact(this.editForm);

    let saveObservable: Observable<HttpResponse<IContact>>;
    if (contact.id !== null) {
      saveObservable = this.contactService.update(contact);
    } else {
      saveObservable = this.contactService.create(contact);
    }

    return saveObservable.pipe(
      map((response: HttpResponse<IContact>) => {
        console.log('Response received:', response);
        return response.body!; // Extract the body (IContact)
      }),

      finalize(() => {
        this.isSaving = false;
        console.log('Save process finalized');
      }),
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
      .subscribe((disposes: ISupplier[]) => {
        this.suppliersSharedCollection = disposes;
        const selectedSupplier = this.suppliersSharedCollection.find(sale => sale.id === this.supplierId);
        if (!selectedSupplier) {
          console.warn(`Disp with id ${this.supplierId} not found in the collection.`);
        } else {
          console.log('Selected Sale:', selectedSupplier);

          this.editForm.patchValue({
            //supplier: selectedSupplier,
          });
        }
      });
  }
}
