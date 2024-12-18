/* eslint-disable */
import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IWare } from 'app/entities/ware/ware.model';
import { WareService } from 'app/entities/ware/service/ware.service';
import { IPurchase } from 'app/entities/purchase/purchase.model';
import { PurchaseService } from 'app/entities/purchase/service/purchase.service';
import { PurchasedStockService } from '../service/purchased-stock.service';
import { IPurchasedStock } from '../purchased-stock.model';
import { PurchasedStockFormGroup, PurchasedStockFormService } from './purchased-stock-form.service';
import { IDispose } from '../../dispose/dispose.model';

@Component({
  standalone: true,
  selector: 'jhi-purchased-stock-update',
  templateUrl: './purchased-stock-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PurchasedStockUpdateComponent implements OnInit {
  isSaving = false;
  purchasedStock: IPurchasedStock | null = null;

  purchasedWaresCollection: IWare[] = [];
  purchasesSharedCollection: IPurchase[] = [];

  protected purchasedStockService = inject(PurchasedStockService);
  protected purchasedStockFormService = inject(PurchasedStockFormService);
  protected wareService = inject(WareService);
  protected purchaseService = inject(PurchaseService);
  protected activatedRoute = inject(ActivatedRoute);
  purchasedId: number = 0;
  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PurchasedStockFormGroup = this.purchasedStockFormService.createPurchasedStockFormGroup();

  compareWare = (o1: IWare | null, o2: IWare | null): boolean => this.wareService.compareWare(o1, o2);

  comparePurchase = (o1: IPurchase | null, o2: IPurchase | null): boolean => this.purchaseService.comparePurchase(o1, o2);

  ngOnInit(): void {
    this.purchasedId = Number(this.activatedRoute.snapshot.params['purchasedId']);
    this.activatedRoute.data.subscribe(({ purchasedStock }) => {
      this.purchasedStock = purchasedStock;
      if (purchasedStock) {
        this.updateForm(purchasedStock);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const purchasedStock = this.purchasedStockFormService.getPurchasedStock(this.editForm);
    if (purchasedStock.id !== null) {
      this.subscribeToSaveResponse(this.purchasedStockService.update(purchasedStock));
    } else {
      this.subscribeToSaveResponse(this.purchasedStockService.create(purchasedStock));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPurchasedStock>>): void {
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

  protected updateForm(purchasedStock: IPurchasedStock): void {
    this.purchasedStock = purchasedStock;
    this.purchasedStockFormService.resetForm(this.editForm, purchasedStock);

    this.purchasedWaresCollection = this.wareService.addWareToCollectionIfMissing<IWare>(
      this.purchasedWaresCollection,
      purchasedStock.purchasedWare,
    );
    this.purchasesSharedCollection = this.purchaseService.addPurchaseToCollectionIfMissing<IPurchase>(
      this.purchasesSharedCollection,
      purchasedStock.purchase,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.wareService
      .query({ filter: 'purchasedstock-is-null' })
      .pipe(map((res: HttpResponse<IWare[]>) => res.body ?? []))
      .pipe(map((wares: IWare[]) => this.wareService.addWareToCollectionIfMissing<IWare>(wares, this.purchasedStock?.purchasedWare)))
      .subscribe((wares: IWare[]) => (this.purchasedWaresCollection = wares));

    this.purchaseService
      .query()
      .pipe(map((res: HttpResponse<IPurchase[]>) => res.body ?? []))
      .pipe(
        map((purchases: IPurchase[]) =>
          this.purchaseService.addPurchaseToCollectionIfMissing<IPurchase>(purchases, this.purchasedStock?.purchase),
        ),
      )
      .subscribe((disposes: IDispose[]) => {
        this.purchasesSharedCollection = disposes;
        const selectedDispose = this.purchasesSharedCollection.find(sale => sale.id === this.purchasedId);
        if (!selectedDispose) {
          console.warn(`Disp with id ${this.purchasedId} not found in the collection.`);
        } else {
          console.log('Selected Sale:', selectedDispose);

          this.editForm.patchValue({
            purchase: selectedDispose,
          });
        }
      });
  }
}
