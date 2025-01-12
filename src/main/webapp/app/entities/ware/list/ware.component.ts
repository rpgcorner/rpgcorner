/* eslint-disable */
import { Component, NgZone, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { FormatMediumDatePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { IWare } from '../ware.model';
import { EntityArrayResponseType, WareService } from '../service/ware.service';
import { WareDeleteDialogComponent } from '../delete/ware-delete-dialog.component';

@Component({
  standalone: true,
  selector: 'jhi-ware',
  templateUrl: './ware.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, FormatMediumDatePipe],
})
export class WareComponent implements OnInit {
  searchTerm: string = '';
  isActive = false;
  subscription: Subscription | null = null;
  wares?: IWare[];
  isLoading = false;

  sortState = sortStateSignal({});

  public readonly router = inject(Router);
  protected readonly wareService = inject(WareService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: IWare): number => this.wareService.getWareIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (!this.wares || this.wares.length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(ware: IWare): void {
    const modalRef = this.modalService.open(WareDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ware = ware;
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
    this.wares = this.refineData(dataFromBody);
  }

  protected refineData(data: IWare[]): IWare[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IWare[] | null): IWare[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.wareService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
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
    this.wareService.searchByParam(searchParam, this.isActive).subscribe({
      next: response => {
        this.wares = response.body || [];
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }
}
