/* eslint-disable */
import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { SaleService } from '../service/sale.service';
import { ISale } from '../sale.model';
import { SaleFormGroup, SaleFormService } from './sale-form.service';
import { SoldStockComponent } from '../../sold-stock/list/sold-stock.component';
import { Router } from '@angular/router';
@Component({
  standalone: true,
  selector: 'jhi-sale-update',
  templateUrl: './sale-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, SoldStockComponent],
})
export class SaleUpdateComponent implements OnInit {
  isSaving = false;
  sale: ISale | null = null;

  usersSharedCollection: IUser[] = [];
  customersSharedCollection: ICustomer[] = [];

  protected saleService = inject(SaleService);
  protected saleFormService = inject(SaleFormService);
  protected userService = inject(UserService);
  protected customerService = inject(CustomerService);
  protected activatedRoute = inject(ActivatedRoute);

  editForm: SaleFormGroup = this.saleFormService.createSaleFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);
  protected router = inject(Router);
  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sale }) => {
      this.sale = sale;
      if (sale) {
        this.updateForm(sale);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sale = this.saleFormService.getSale(this.editForm);
    if (sale.id !== null) {
      this.subscribeToSaveResponse(this.saleService.update(sale));
    } else {
      this.subscribeToSaveResponse(this.saleService.create(sale));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISale>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: res => this.onSaveSuccess(res.body),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(sale: ISale | null): void {
    if (sale?.id) {
      // Navigálás az adott sale ID alapján
      this.router.navigate(['/sale', sale.id, 'edit']);
    }
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(sale: ISale): void {
    this.sale = sale;
    this.saleFormService.resetForm(this.editForm, sale);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, sale.soldByUser);
    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing<ICustomer>(
      this.customersSharedCollection,
      sale.soldForCustomer,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.sale?.soldByUser)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.sale?.soldForCustomer),
        ),
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));
  }
}
