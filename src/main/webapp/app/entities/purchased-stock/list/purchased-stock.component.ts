/* eslint-disable */
import { Component, NgZone, OnInit, inject, Input } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { IPurchasedStock } from '../purchased-stock.model';
import { EntityArrayResponseType, PurchasedStockService } from '../service/purchased-stock.service';
import { PurchasedStockDeleteDialogComponent } from '../delete/purchased-stock-delete-dialog.component';

@Component({
  standalone: true,
  selector: 'jhi-purchased-stock',
  templateUrl: './purchased-stock.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
  ],
})
export class PurchasedStockComponent implements OnInit {
  @Input() isVisible = true;
  @Input() purchaseId?: number;
  subscription: Subscription | null = null;
  purchasedStocks?: IPurchasedStock[];
  isLoading = false;

  sortState = sortStateSignal({});

  public readonly router = inject(Router);
  protected readonly purchasedStockService = inject(PurchasedStockService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: IPurchasedStock): number => this.purchasedStockService.getPurchasedStockIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (!this.purchasedStocks || this.purchasedStocks.length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(purchasedStock: IPurchasedStock): void {
    const modalRef = this.modalService.open(PurchasedStockDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.purchasedStock = purchasedStock;
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
    this.purchasedStocks = this.refineData(dataFromBody);
  }

  protected refineData(data: IPurchasedStock[]): IPurchasedStock[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IPurchasedStock[] | null): IPurchasedStock[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    if (this.purchaseId) {
      return this.purchasedStockService.findByPurchaseId(this.purchaseId).pipe(tap(() => (this.isLoading = false)));
    }
    this.isLoading = true;
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    if (this.isVisible) {
      return this.purchasedStockService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
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
}
