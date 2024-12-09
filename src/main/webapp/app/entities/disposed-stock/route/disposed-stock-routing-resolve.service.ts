import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDisposedStock } from '../disposed-stock.model';
import { DisposedStockService } from '../service/disposed-stock.service';

const disposedStockResolve = (route: ActivatedRouteSnapshot): Observable<null | IDisposedStock> => {
  const id = route.params.id;
  if (id) {
    return inject(DisposedStockService)
      .find(id)
      .pipe(
        mergeMap((disposedStock: HttpResponse<IDisposedStock>) => {
          if (disposedStock.body) {
            return of(disposedStock.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default disposedStockResolve;
