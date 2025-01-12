/* eslint-disable */

import { Component, NgZone, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { ISupplier } from '../supplier.model';
import { EntityArrayResponseType, SupplierService } from '../service/supplier.service';
import { SupplierDeleteDialogComponent } from '../delete/supplier-delete-dialog.component';

@Component({
  standalone: true,
  selector: 'jhi-supplier',
  templateUrl: './supplier.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, ReactiveFormsModule],
})
export class SupplierComponent implements OnInit {
  searchTerm: string = '';
  subscription: Subscription | null = null;
  suppliers?: ISupplier[];
  isLoading = false;

  sortState = sortStateSignal({});

  public readonly router = inject(Router);
  protected readonly supplierService = inject(SupplierService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: ISupplier): number => this.supplierService.getSupplierIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (!this.suppliers || this.suppliers.length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(supplier: ISupplier): void {
    const modalRef = this.modalService.open(SupplierDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.supplier = supplier;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.suppliers = this.refineData(dataFromBody);
  }

  protected refineData(data: ISupplier[]): ISupplier[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: ISupplier[] | null): ISupplier[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.supplierService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }

  search(searchParam: any) {
    this.isLoading = true;
    this.supplierService.searchByParam(searchParam).subscribe({
      next: response => {
        this.suppliers = response.body || [];
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }
}
