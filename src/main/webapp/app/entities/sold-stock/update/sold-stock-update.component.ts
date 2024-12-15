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
import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { SoldStockService } from '../service/sold-stock.service';
import { ISoldStock } from '../sold-stock.model';
import { SoldStockFormGroup, SoldStockFormService } from './sold-stock-form.service';

@Component({
  standalone: true,
  selector: 'jhi-sold-stock-update',
  templateUrl: './sold-stock-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SoldStockUpdateComponent implements OnInit {
  isSaving = false;
  soldStock: ISoldStock | null = null;

  soldWaresCollection: IWare[] = [];
  salesSharedCollection: ISale[] = [];
  saleId: number = 0;
  protected soldStockService = inject(SoldStockService);
  protected soldStockFormService = inject(SoldStockFormService);
  protected wareService = inject(WareService);
  protected saleService = inject(SaleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SoldStockFormGroup = this.soldStockFormService.createSoldStockFormGroup();

  compareWare = (o1: IWare | null, o2: IWare | null): boolean => this.wareService.compareWare(o1, o2);

  compareSale = (o1: ISale | null, o2: ISale | null): boolean => this.saleService.compareSale(o1, o2);

  ngOnInit(): void {
    this.saleId = Number(this.activatedRoute.snapshot.params['salesId']);
    this.activatedRoute.data.subscribe(({ soldStock }) => {
      this.soldStock = soldStock;
      if (soldStock) {
        this.updateForm(soldStock);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const soldStock = this.soldStockFormService.getSoldStock(this.editForm);
    if (soldStock.id !== null) {
      this.subscribeToSaveResponse(this.soldStockService.update(soldStock));
    } else {
      this.subscribeToSaveResponse(this.soldStockService.create(soldStock));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISoldStock>>): void {
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

  protected updateForm(soldStock: ISoldStock): void {
    this.soldStock = soldStock;
    this.soldStockFormService.resetForm(this.editForm, soldStock);

    this.soldWaresCollection = this.wareService.addWareToCollectionIfMissing<IWare>(this.soldWaresCollection, soldStock.soldWare);
    this.salesSharedCollection = this.saleService.addSaleToCollectionIfMissing<ISale>(this.salesSharedCollection, soldStock.sale);
  }

  protected loadRelationshipsOptions(): void {
    this.wareService
      .query({ filter: 'soldstock-is-null' })
      .pipe(map((res: HttpResponse<IWare[]>) => res.body ?? []))
      .pipe(map((wares: IWare[]) => this.wareService.addWareToCollectionIfMissing<IWare>(wares, this.soldStock?.soldWare)))
      .subscribe((wares: IWare[]) => (this.soldWaresCollection = wares));

    this.saleService
      .query()
      .pipe(map((res: HttpResponse<ISale[]>) => res.body ?? []))
      .pipe(map((sales: ISale[]) => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, this.soldStock?.sale)))
      .subscribe((sales: ISale[]) => {
        this.salesSharedCollection = sales;
        // Keresés a salesSharedCollection-ben
        const selectedSale = this.salesSharedCollection.find(sale => sale.id === this.saleId);
        if (!selectedSale) {
          console.warn(`Sale with id ${this.saleId} not found in the collection.`);
        } else {
          console.log('Selected Sale:', selectedSale);
          // Itt beállíthatsz bármit a selectedSale alapján, ha szükséges
          this.editForm.patchValue({
            sale: selectedSale, // 'sale' az űrlapkontroll neve
          });
        }
      });
  }
}
