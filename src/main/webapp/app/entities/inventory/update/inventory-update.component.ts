import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IWare } from 'app/entities/ware/ware.model';
import { WareService } from 'app/entities/ware/service/ware.service';
import { IInventory } from '../inventory.model';
import { InventoryService } from '../service/inventory.service';
import { InventoryFormGroup, InventoryFormService } from './inventory-form.service';

@Component({
  standalone: true,
  selector: 'jhi-inventory-update',
  templateUrl: './inventory-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class InventoryUpdateComponent implements OnInit {
  isSaving = false;
  inventory: IInventory | null = null;

  waresCollection: IWare[] = [];

  protected inventoryService = inject(InventoryService);
  protected inventoryFormService = inject(InventoryFormService);
  protected wareService = inject(WareService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: InventoryFormGroup = this.inventoryFormService.createInventoryFormGroup();

  compareWare = (o1: IWare | null, o2: IWare | null): boolean => this.wareService.compareWare(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ inventory }) => {
      this.inventory = inventory;
      if (inventory) {
        this.updateForm(inventory);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const inventory = this.inventoryFormService.getInventory(this.editForm);
    if (inventory.id !== null) {
      this.subscribeToSaveResponse(this.inventoryService.update(inventory));
    } else {
      this.subscribeToSaveResponse(this.inventoryService.create(inventory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInventory>>): void {
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

  protected updateForm(inventory: IInventory): void {
    this.inventory = inventory;
    this.inventoryFormService.resetForm(this.editForm, inventory);

    this.waresCollection = this.wareService.addWareToCollectionIfMissing<IWare>(this.waresCollection, inventory.ware);
  }

  protected loadRelationshipsOptions(): void {
    this.wareService
      .query({ filter: 'inventory-is-null' })
      .pipe(map((res: HttpResponse<IWare[]>) => res.body ?? []))
      .pipe(map((wares: IWare[]) => this.wareService.addWareToCollectionIfMissing<IWare>(wares, this.inventory?.ware)))
      .subscribe((wares: IWare[]) => (this.waresCollection = wares));
  }
}
