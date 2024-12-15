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
import { IDispose } from 'app/entities/dispose/dispose.model';
import { DisposeService } from 'app/entities/dispose/service/dispose.service';
import { DisposedStockService } from '../service/disposed-stock.service';
import { IDisposedStock } from '../disposed-stock.model';
import { DisposedStockFormGroup, DisposedStockFormService } from './disposed-stock-form.service';

@Component({
  standalone: true,
  selector: 'jhi-disposed-stock-update',
  templateUrl: './disposed-stock-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DisposedStockUpdateComponent implements OnInit {
  isSaving = false;
  disposedStock: IDisposedStock | null = null;

  disposedWaresCollection: IWare[] = [];
  disposesSharedCollection: IDispose[] = [];
  disposeId: number = 0;
  protected disposedStockService = inject(DisposedStockService);
  protected disposedStockFormService = inject(DisposedStockFormService);
  protected wareService = inject(WareService);
  protected disposeService = inject(DisposeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DisposedStockFormGroup = this.disposedStockFormService.createDisposedStockFormGroup();

  compareWare = (o1: IWare | null, o2: IWare | null): boolean => this.wareService.compareWare(o1, o2);

  compareDispose = (o1: IDispose | null, o2: IDispose | null): boolean => this.disposeService.compareDispose(o1, o2);

  ngOnInit(): void {
    this.disposeId = Number(this.activatedRoute.snapshot.params['disposeId']);
    this.activatedRoute.data.subscribe(({ disposedStock }) => {
      this.disposedStock = disposedStock;
      if (disposedStock) {
        this.updateForm(disposedStock);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const disposedStock = this.disposedStockFormService.getDisposedStock(this.editForm);
    if (disposedStock.id !== null) {
      this.subscribeToSaveResponse(this.disposedStockService.update(disposedStock));
    } else {
      this.subscribeToSaveResponse(this.disposedStockService.create(disposedStock));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDisposedStock>>): void {
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

  protected updateForm(disposedStock: IDisposedStock): void {
    this.disposedStock = disposedStock;
    this.disposedStockFormService.resetForm(this.editForm, disposedStock);

    this.disposedWaresCollection = this.wareService.addWareToCollectionIfMissing<IWare>(
      this.disposedWaresCollection,
      disposedStock.disposedWare,
    );
    this.disposesSharedCollection = this.disposeService.addDisposeToCollectionIfMissing<IDispose>(
      this.disposesSharedCollection,
      disposedStock.dispose,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.wareService
      .query({ filter: 'disposedstock-is-null' })
      .pipe(map((res: HttpResponse<IWare[]>) => res.body ?? []))
      .pipe(map((wares: IWare[]) => this.wareService.addWareToCollectionIfMissing<IWare>(wares, this.disposedStock?.disposedWare)))
      .subscribe((wares: IWare[]) => (this.disposedWaresCollection = wares));

    this.disposeService
      .query()
      .pipe(map((res: HttpResponse<IDispose[]>) => res.body ?? []))
      .pipe(
        map((disposes: IDispose[]) => this.disposeService.addDisposeToCollectionIfMissing<IDispose>(disposes, this.disposedStock?.dispose)),
      )
      .subscribe((disposes: IDispose[]) => {
        this.disposesSharedCollection = disposes;
        const selectedDispose = this.disposesSharedCollection.find(sale => sale.id === this.disposeId);
        if (!selectedDispose) {
          console.warn(`Disp with id ${this.disposeId} not found in the collection.`);
        } else {
          console.log('Selected Sale:', selectedDispose);

          this.editForm.patchValue({
            dispose: selectedDispose,
          });
        }
      });
  }
}
