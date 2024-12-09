import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReturnedStock } from '../returned-stock.model';
import { ReturnedStockService } from '../service/returned-stock.service';

const returnedStockResolve = (route: ActivatedRouteSnapshot): Observable<null | IReturnedStock> => {
  const id = route.params.id;
  if (id) {
    return inject(ReturnedStockService)
      .find(id)
      .pipe(
        mergeMap((returnedStock: HttpResponse<IReturnedStock>) => {
          if (returnedStock.body) {
            return of(returnedStock.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default returnedStockResolve;
