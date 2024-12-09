import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPurchase } from '../purchase.model';
import { PurchaseService } from '../service/purchase.service';

const purchaseResolve = (route: ActivatedRouteSnapshot): Observable<null | IPurchase> => {
  const id = route.params.id;
  if (id) {
    return inject(PurchaseService)
      .find(id)
      .pipe(
        mergeMap((purchase: HttpResponse<IPurchase>) => {
          if (purchase.body) {
            return of(purchase.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default purchaseResolve;
