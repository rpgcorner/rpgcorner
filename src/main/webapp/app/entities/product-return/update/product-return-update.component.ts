import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { ProductReturnService } from '../service/product-return.service';
import { IProductReturn } from '../product-return.model';
import { ProductReturnFormGroup, ProductReturnFormService } from './product-return-form.service';

@Component({
  standalone: true,
  selector: 'jhi-product-return-update',
  templateUrl: './product-return-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ProductReturnUpdateComponent implements OnInit {
  isSaving = false;
  productReturn: IProductReturn | null = null;

  salesSharedCollection: ISale[] = [];
  usersSharedCollection: IUser[] = [];
  customersSharedCollection: ICustomer[] = [];

  protected productReturnService = inject(ProductReturnService);
  protected productReturnFormService = inject(ProductReturnFormService);
  protected saleService = inject(SaleService);
  protected userService = inject(UserService);
  protected customerService = inject(CustomerService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProductReturnFormGroup = this.productReturnFormService.createProductReturnFormGroup();

  compareSale = (o1: ISale | null, o2: ISale | null): boolean => this.saleService.compareSale(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productReturn }) => {
      this.productReturn = productReturn;
      if (productReturn) {
        this.updateForm(productReturn);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productReturn = this.productReturnFormService.getProductReturn(this.editForm);
    if (productReturn.id !== null) {
      this.subscribeToSaveResponse(this.productReturnService.update(productReturn));
    } else {
      this.subscribeToSaveResponse(this.productReturnService.create(productReturn));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductReturn>>): void {
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

  protected updateForm(productReturn: IProductReturn): void {
    this.productReturn = productReturn;
    this.productReturnFormService.resetForm(this.editForm, productReturn);

    this.salesSharedCollection = this.saleService.addSaleToCollectionIfMissing<ISale>(this.salesSharedCollection, productReturn.sale);
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      productReturn.returnedByUser,
    );
    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing<ICustomer>(
      this.customersSharedCollection,
      productReturn.returnedByCustomer,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.saleService
      .query()
      .pipe(map((res: HttpResponse<ISale[]>) => res.body ?? []))
      .pipe(map((sales: ISale[]) => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, this.productReturn?.sale)))
      .subscribe((sales: ISale[]) => (this.salesSharedCollection = sales));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.productReturn?.returnedByUser)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.productReturn?.returnedByCustomer),
        ),
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));
  }
}
