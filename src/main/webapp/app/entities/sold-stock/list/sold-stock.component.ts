/* eslint-disable */
import { Component, inject, Input, NgZone, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, Observable, Subscription, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { FormsModule } from '@angular/forms';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { ISoldStock } from '../sold-stock.model';
import { EntityArrayResponseType, SoldStockService } from '../service/sold-stock.service';
import { SoldStockDeleteDialogComponent } from '../delete/sold-stock-delete-dialog.component';

@Component({
  standalone: true,
  selector: 'jhi-sold-stock',
  templateUrl: './sold-stock.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective],
})
export class SoldStockComponent implements OnInit {
  subscription: Subscription | null = null;
  soldStocks?: ISoldStock[];
  isLoading = false;
  @Input() isVisible = true;
  @Input() salesId?: number;
  sortState = sortStateSignal({});
  startDate: any;
  endDate: any;
  public readonly router = inject(Router);
  protected readonly soldStockService = inject(SoldStockService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: ISoldStock): number => this.soldStockService.getSoldStockIdentifier(item);

  ngOnInit(): void {
    if (this.isVisible === undefined) {
      this.isVisible = true;
    }
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (!this.soldStocks || this.soldStocks.length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(soldStock: ISoldStock): void {
    const modalRef = this.modalService.open(SoldStockDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.soldStock = soldStock;
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
    this.soldStocks = this.refineData(dataFromBody);
  }

  protected refineData(data: ISoldStock[]): ISoldStock[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: ISoldStock[] | null): ISoldStock[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };

    if (this.salesId) {
      return this.soldStockService.findBySaleId(this.salesId).pipe(tap(() => (this.isLoading = false)));
    }
    if (this.isVisible) {
      return this.soldStockService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
    }
    return new Observable<EntityArrayResponseType>();
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
  search() {
    this.load();
  }
}
