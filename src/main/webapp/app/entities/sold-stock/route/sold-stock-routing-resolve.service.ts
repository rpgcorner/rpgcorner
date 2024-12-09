import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISoldStock } from '../sold-stock.model';
import { SoldStockService } from '../service/sold-stock.service';

const soldStockResolve = (route: ActivatedRouteSnapshot): Observable<null | ISoldStock> => {
  const id = route.params.id;
  if (id) {
    return inject(SoldStockService)
      .find(id)
      .pipe(
        mergeMap((soldStock: HttpResponse<ISoldStock>) => {
          if (soldStock.body) {
            return of(soldStock.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default soldStockResolve;
