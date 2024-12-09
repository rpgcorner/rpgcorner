import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPurchasedStock } from '../purchased-stock.model';
import { PurchasedStockService } from '../service/purchased-stock.service';

const purchasedStockResolve = (route: ActivatedRouteSnapshot): Observable<null | IPurchasedStock> => {
  const id = route.params.id;
  if (id) {
    return inject(PurchasedStockService)
      .find(id)
      .pipe(
        mergeMap((purchasedStock: HttpResponse<IPurchasedStock>) => {
          if (purchasedStock.body) {
            return of(purchasedStock.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default purchasedStockResolve;
