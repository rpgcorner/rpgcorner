import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IWare } from '../ware.model';
import { WareService } from '../service/ware.service';

const wareResolve = (route: ActivatedRouteSnapshot): Observable<null | IWare> => {
  const id = route.params.id;
  if (id) {
    return inject(WareService)
      .find(id)
      .pipe(
        mergeMap((ware: HttpResponse<IWare>) => {
          if (ware.body) {
            return of(ware.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default wareResolve;
