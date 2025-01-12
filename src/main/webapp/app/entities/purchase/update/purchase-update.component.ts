/* eslint-disable */
import { Component, OnInit, inject, Input } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';
import { PurchaseService } from '../service/purchase.service';
import { IPurchase } from '../purchase.model';
import { PurchaseFormGroup, PurchaseFormService } from './purchase-form.service';
import { PurchasedStockComponent } from '../../purchased-stock/list/purchased-stock.component';

@Component({
  standalone: true,
  selector: 'jhi-purchase-update',
  templateUrl: './purchase-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, PurchasedStockComponent],
})
export class PurchaseUpdateComponent implements OnInit {
  isSaving = false;
  purchase: IPurchase | null = null;

  usersSharedCollection: IUser[] = [];
  suppliersSharedCollection: ISupplier[] = [];

  protected purchaseService = inject(PurchaseService);
  protected purchaseFormService = inject(PurchaseFormService);
  protected userService = inject(UserService);
  protected supplierService = inject(SupplierService);
  protected activatedRoute = inject(ActivatedRoute);
  protected router = inject(Router);

  editForm: PurchaseFormGroup = this.purchaseFormService.createPurchaseFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareSupplier = (o1: ISupplier | null, o2: ISupplier | null): boolean => this.supplierService.compareSupplier(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ purchase }) => {
      this.purchase = purchase;
      if (purchase) {
        this.updateForm(purchase);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const purchase = this.purchaseFormService.getPurchase(this.editForm);
    if (purchase.id !== null) {
      this.subscribeToSaveResponse(this.purchaseService.update(purchase));
    } else {
      this.subscribeToSaveResponse(this.purchaseService.create(purchase));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPurchase>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: res => this.onSaveSuccess(res.body),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(dispose: IPurchase | null): void {
    if (dispose?.id) {
      // Navigálás az adott sale ID alapján
      this.router.navigate(['/purchase', dispose.id, 'edit']);
    }
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(purchase: IPurchase): void {
    this.purchase = purchase;
    this.purchaseFormService.resetForm(this.editForm, purchase);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, purchase.purchasedByUser);
    this.suppliersSharedCollection = this.supplierService.addSupplierToCollectionIfMissing<ISupplier>(
      this.suppliersSharedCollection,
      purchase.purchasedFromSupplier,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.purchase?.purchasedByUser)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.supplierService
      .query()
      .pipe(map((res: HttpResponse<ISupplier[]>) => res.body ?? []))
      .pipe(
        map((suppliers: ISupplier[]) =>
          this.supplierService.addSupplierToCollectionIfMissing<ISupplier>(suppliers, this.purchase?.purchasedFromSupplier),
        ),
      )
      .subscribe((suppliers: ISupplier[]) => (this.suppliersSharedCollection = suppliers));
  }
}
