/* eslint-disable */
import { Component, NgZone, OnInit, inject, Input } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { FormsModule } from '@angular/forms';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { IReturnedStock } from '../returned-stock.model';
import { EntityArrayResponseType, ReturnedStockService } from '../service/returned-stock.service';
import { ReturnedStockDeleteDialogComponent } from '../delete/returned-stock-delete-dialog.component';

@Component({
  standalone: true,
  selector: 'jhi-returned-stock',
  templateUrl: './returned-stock.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective],
})
export class ReturnedStockComponent implements OnInit {
  subscription: Subscription | null = null;
  returnedStocks?: IReturnedStock[];
  isLoading = false;
  @Input() isVisible = true;
  @Input() productReturnId?: number;
  sortState = sortStateSignal({});

  public readonly router = inject(Router);
  protected readonly returnedStockService = inject(ReturnedStockService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: IReturnedStock): number => this.returnedStockService.getReturnedStockIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (!this.returnedStocks || this.returnedStocks.length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(returnedStock: IReturnedStock): void {
    const modalRef = this.modalService.open(ReturnedStockDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.returnedStock = returnedStock;
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
    this.returnedStocks = this.refineData(dataFromBody);
  }

  protected refineData(data: IReturnedStock[]): IReturnedStock[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IReturnedStock[] | null): IReturnedStock[] {
    return data ?? [];
  }
  protected queryBackend(): Observable<EntityArrayResponseType> {
    if (this.productReturnId) {
      return this.returnedStockService.findByProductReturnId(this.productReturnId).pipe(tap(() => (this.isLoading = false)));
    }
    this.isLoading = true;
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    if (this.isVisible) {
      return this.returnedStockService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
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
