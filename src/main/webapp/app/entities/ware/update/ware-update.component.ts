/* eslint-disable */
import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { IWare } from '../ware.model';
import { WareService } from '../service/ware.service';
import { WareFormGroup, WareFormService } from './ware-form.service';

import { HttpErrorResponse } from '@angular/common/http';
@Component({
  standalone: true,
  selector: 'jhi-ware-update',
  templateUrl: './ware-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class WareUpdateComponent implements OnInit {
  isSaving = false;
  ware: IWare | null = null;

  categoriesSharedCollection: ICategory[] = [];
  errorNameAndProductCodeExists = signal(false);
  protected wareService = inject(WareService);
  protected wareFormService = inject(WareFormService);
  protected categoryService = inject(CategoryService);
  protected activatedRoute = inject(ActivatedRoute);

  editForm: WareFormGroup = this.wareFormService.createWareFormGroup();

  compareCategory = (o1: ICategory | null, o2: ICategory | null): boolean => this.categoryService.compareCategory(o1, o2);

  ngOnInit(): void {
    this.errorNameAndProductCodeExists.set(false);
    this.activatedRoute.data.subscribe(({ ware }) => {
      this.ware = ware;
      if (ware) {
        this.updateForm(ware);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ware = this.wareFormService.getWare(this.editForm);
    if (ware.id !== null) {
      this.subscribeToSaveResponse(this.wareService.update(ware));
    } else {
      this.subscribeToSaveResponse(this.wareService.create(ware));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWare>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: response => this.processError(response),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(response: HttpErrorResponse): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(ware: IWare): void {
    this.ware = ware;
    this.wareFormService.resetForm(this.editForm, ware);

    this.categoriesSharedCollection = this.categoryService.addCategoryToCollectionIfMissing<ICategory>(
      this.categoriesSharedCollection,
      ware.mainCategory,
      ware.subCategory,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing<ICategory>(categories, this.ware?.mainCategory, this.ware?.subCategory),
        ),
      )
      .subscribe((categories: ICategory[]) => (this.categoriesSharedCollection = categories));
  }

  private processError(response: HttpErrorResponse): void {}
}
