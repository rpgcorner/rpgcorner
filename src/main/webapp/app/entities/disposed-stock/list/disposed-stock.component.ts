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
import { IDisposedStock } from '../disposed-stock.model';
import { DisposedStockService, EntityArrayResponseType } from '../service/disposed-stock.service';
import { DisposedStockDeleteDialogComponent } from '../delete/disposed-stock-delete-dialog.component';

@Component({
  standalone: true,
  selector: 'jhi-disposed-stock',
  templateUrl: './disposed-stock.component.html',
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
export class DisposedStockComponent implements OnInit {
  subscription: Subscription | null = null;
  disposedStocks?: IDisposedStock[];
  isLoading = false;
  @Input() isVisible = true;
  @Input() disposeId?: number;
  sortState = sortStateSignal({});

  public readonly router = inject(Router);
  protected readonly disposedStockService = inject(DisposedStockService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: IDisposedStock): number => this.disposedStockService.getDisposedStockIdentifier(item);

  ngOnInit(): void {
    if (this.isVisible === undefined) {
      this.isVisible = true;
    }
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (!this.disposedStocks || this.disposedStocks.length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(disposedStock: IDisposedStock): void {
    const modalRef = this.modalService.open(DisposedStockDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.disposedStock = disposedStock;
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
    this.disposedStocks = this.refineData(dataFromBody);
  }

  protected refineData(data: IDisposedStock[]): IDisposedStock[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IDisposedStock[] | null): IDisposedStock[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    if (this.disposeId) {
      return this.disposedStockService.findByDisposeId(this.disposeId).pipe(tap(() => (this.isLoading = false)));
    }
    if (this.isVisible) {
      return this.disposedStockService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
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
