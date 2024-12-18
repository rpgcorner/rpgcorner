/* eslint-disable */
import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IWare } from 'app/entities/ware/ware.model';
import { WareService } from 'app/entities/ware/service/ware.service';
import { IProductReturn } from 'app/entities/product-return/product-return.model';
import { ProductReturnService } from 'app/entities/product-return/service/product-return.service';
import { ReturnedStockService } from '../service/returned-stock.service';
import { IReturnedStock } from '../returned-stock.model';
import { ReturnedStockFormGroup, ReturnedStockFormService } from './returned-stock-form.service';
import { IDispose } from '../../dispose/dispose.model';

@Component({
  standalone: true,
  selector: 'jhi-returned-stock-update',
  templateUrl: './returned-stock-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ReturnedStockUpdateComponent implements OnInit {
  isSaving = false;
  returnedStock: IReturnedStock | null = null;

  returnedWaresCollection: IWare[] = [];
  productReturnsSharedCollection: IProductReturn[] = [];

  protected returnedStockService = inject(ReturnedStockService);
  protected returnedStockFormService = inject(ReturnedStockFormService);
  protected wareService = inject(WareService);
  protected productReturnService = inject(ProductReturnService);
  protected activatedRoute = inject(ActivatedRoute);
  productReturnId: number = 0;
  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ReturnedStockFormGroup = this.returnedStockFormService.createReturnedStockFormGroup();

  compareWare = (o1: IWare | null, o2: IWare | null): boolean => this.wareService.compareWare(o1, o2);

  compareProductReturn = (o1: IProductReturn | null, o2: IProductReturn | null): boolean =>
    this.productReturnService.compareProductReturn(o1, o2);

  ngOnInit(): void {
    this.productReturnId = Number(this.activatedRoute.snapshot.params['productReturnId']);
    this.activatedRoute.data.subscribe(({ returnedStock }) => {
      this.returnedStock = returnedStock;
      if (returnedStock) {
        this.updateForm(returnedStock);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const returnedStock = this.returnedStockFormService.getReturnedStock(this.editForm);
    if (returnedStock.id !== null) {
      this.subscribeToSaveResponse(this.returnedStockService.update(returnedStock));
    } else {
      this.subscribeToSaveResponse(this.returnedStockService.create(returnedStock));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReturnedStock>>): void {
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

  protected updateForm(returnedStock: IReturnedStock): void {
    this.returnedStock = returnedStock;
    this.returnedStockFormService.resetForm(this.editForm, returnedStock);

    this.returnedWaresCollection = this.wareService.addWareToCollectionIfMissing<IWare>(
      this.returnedWaresCollection,
      returnedStock.returnedWare,
    );
    this.productReturnsSharedCollection = this.productReturnService.addProductReturnToCollectionIfMissing<IProductReturn>(
      this.productReturnsSharedCollection,
      returnedStock.productReturn,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.wareService
      .query({ filter: 'returnedstock-is-null' })
      .pipe(map((res: HttpResponse<IWare[]>) => res.body ?? []))
      .pipe(map((wares: IWare[]) => this.wareService.addWareToCollectionIfMissing<IWare>(wares, this.returnedStock?.returnedWare)))
      .subscribe((wares: IWare[]) => (this.returnedWaresCollection = wares));

    this.productReturnService
      .query()
      .pipe(map((res: HttpResponse<IProductReturn[]>) => res.body ?? []))
      .pipe(
        map((productReturns: IProductReturn[]) =>
          this.productReturnService.addProductReturnToCollectionIfMissing<IProductReturn>(
            productReturns,
            this.returnedStock?.productReturn,
          ),
        ),
      )
      .subscribe((disposes: IDispose[]) => {
        this.productReturnsSharedCollection = disposes;
        const selectedProductReturn = this.productReturnsSharedCollection.find(sale => sale.id === this.productReturnId);
        if (!selectedProductReturn) {
          console.warn(`Disp with id ${this.productReturnId} not found in the collection.`);
        } else {
          console.log('Selected Sale:', selectedProductReturn);

          this.editForm.patchValue({
            productReturn: selectedProductReturn,
          });
        }
      });
  }
}
