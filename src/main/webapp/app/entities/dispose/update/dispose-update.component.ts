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
import { IDispose } from '../dispose.model';
import { DisposeService } from '../service/dispose.service';
import { DisposeFormGroup, DisposeFormService } from './dispose-form.service';
import { DisposedStockComponent } from '../../disposed-stock/list/disposed-stock.component';
import { SoldStockComponent } from '../../sold-stock/list/sold-stock.component';
import { ISale } from '../../sale/sale.model';

@Component({
  standalone: true,
  selector: 'jhi-dispose-update',
  templateUrl: './dispose-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, DisposedStockComponent, SoldStockComponent],
})
export class DisposeUpdateComponent implements OnInit {
  isSaving = false;
  dispose: IDispose | null = null;

  usersSharedCollection: IUser[] = [];

  protected disposeService = inject(DisposeService);
  protected disposeFormService = inject(DisposeFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DisposeFormGroup = this.disposeFormService.createDisposeFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);
  @Input() isVisible = true;
  @Input() salesId?: number;
  protected router = inject(Router);
  ngOnInit(): void {
    if (this.isVisible === undefined) {
      this.isVisible = true;
    }
    this.activatedRoute.data.subscribe(({ dispose }) => {
      this.dispose = dispose;
      if (dispose) {
        this.updateForm(dispose);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dispose = this.disposeFormService.getDispose(this.editForm);
    if (dispose.id !== null) {
      this.subscribeToSaveResponse(this.disposeService.update(dispose));
    } else {
      this.subscribeToSaveResponse(this.disposeService.create(dispose));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDispose>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: res => this.onSaveSuccess(res.body),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(dispose: IDispose | null): void {
    if (dispose?.id) {
      // Navigálás az adott sale ID alapján
      this.router.navigate(['/dispose', dispose.id, 'edit']);
    }
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(dispose: IDispose): void {
    this.dispose = dispose;
    this.disposeFormService.resetForm(this.editForm, dispose);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, dispose.disposedByUser);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.dispose?.disposedByUser)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
