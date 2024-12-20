/* eslint-disable */

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDispose, NewDispose } from '../dispose.model';

export type PartialUpdateDispose = Partial<IDispose> & Pick<IDispose, 'id'>;

type RestOf<T extends IDispose | NewDispose> = Omit<T, 'disposeDate'> & {
  disposeDate?: string | null;
};

export type RestDispose = RestOf<IDispose>;

export type NewRestDispose = RestOf<NewDispose>;

export type PartialUpdateRestDispose = RestOf<PartialUpdateDispose>;

export type EntityResponseType = HttpResponse<IDispose>;
export type EntityArrayResponseType = HttpResponse<IDispose[]>;

@Injectable({ providedIn: 'root' })
export class DisposeService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/disposes');

  create(dispose: NewDispose): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dispose);
    return this.http
      .post<RestDispose>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(dispose: IDispose): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dispose);
    return this.http
      .put<RestDispose>(`${this.resourceUrl}/${this.getDisposeIdentifier(dispose)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(dispose: PartialUpdateDispose): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dispose);
    return this.http
      .patch<RestDispose>(`${this.resourceUrl}/${this.getDisposeIdentifier(dispose)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDispose>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDispose[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDisposeIdentifier(dispose: Pick<IDispose, 'id'>): number {
    return dispose.id;
  }

  compareDispose(o1: Pick<IDispose, 'id'> | null, o2: Pick<IDispose, 'id'> | null): boolean {
    return o1 && o2 ? this.getDisposeIdentifier(o1) === this.getDisposeIdentifier(o2) : o1 === o2;
  }

  addDisposeToCollectionIfMissing<Type extends Pick<IDispose, 'id'>>(
    disposeCollection: Type[],
    ...disposesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const disposes: Type[] = disposesToCheck.filter(isPresent);
    if (disposes.length > 0) {
      const disposeCollectionIdentifiers = disposeCollection.map(disposeItem => this.getDisposeIdentifier(disposeItem));
      const disposesToAdd = disposes.filter(disposeItem => {
        const disposeIdentifier = this.getDisposeIdentifier(disposeItem);
        if (disposeCollectionIdentifiers.includes(disposeIdentifier)) {
          return false;
        }
        disposeCollectionIdentifiers.push(disposeIdentifier);
        return true;
      });
      return [...disposesToAdd, ...disposeCollection];
    }
    return disposeCollection;
  }

  protected convertDateFromClient<T extends IDispose | NewDispose | PartialUpdateDispose>(dispose: T): RestOf<T> {
    return {
      ...dispose,
      disposeDate: dispose.disposeDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restDispose: RestDispose): IDispose {
    return {
      ...restDispose,
      disposeDate: restDispose.disposeDate ? dayjs(restDispose.disposeDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDispose>): HttpResponse<IDispose> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDispose[]>): HttpResponse<IDispose[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
  searchByParam(name?: string, startDate?: string, endDate?: string): Observable<EntityArrayResponseType> {
    const options: any = {};
    if (name) options['name'] = name;
    if (startDate) options['startDate'] = startDate;
    if (endDate) options['endDate'] = endDate;
    return this.http.get<IDispose[]>(`${this.resourceUrl}/search`, { params: options, observe: 'response' });
  }
}
