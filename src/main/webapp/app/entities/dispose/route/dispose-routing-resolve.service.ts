import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDispose } from '../dispose.model';
import { DisposeService } from '../service/dispose.service';

const disposeResolve = (route: ActivatedRouteSnapshot): Observable<null | IDispose> => {
  const id = route.params.id;
  if (id) {
    return inject(DisposeService)
      .find(id)
      .pipe(
        mergeMap((dispose: HttpResponse<IDispose>) => {
          if (dispose.body) {
            return of(dispose.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default disposeResolve;
