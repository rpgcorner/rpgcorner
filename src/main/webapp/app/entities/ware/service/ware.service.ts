/* eslint-disable */
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IWare, NewWare } from '../ware.model';
import { ICategory } from '../../category/category.model';

export type PartialUpdateWare = Partial<IWare> & Pick<IWare, 'id'>;

type RestOf<T extends IWare | NewWare> = Omit<T, 'created'> & {
  created?: string | null;
};

export type RestWare = RestOf<IWare>;

export type NewRestWare = RestOf<NewWare>;

export type PartialUpdateRestWare = RestOf<PartialUpdateWare>;

export type EntityResponseType = HttpResponse<IWare>;
export type EntityArrayResponseType = HttpResponse<IWare[]>;

@Injectable({ providedIn: 'root' })
export class WareService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/wares');

  create(ware: NewWare): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ware);
    return this.http.post<RestWare>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(ware: IWare): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ware);
    return this.http
      .put<RestWare>(`${this.resourceUrl}/${this.getWareIdentifier(ware)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(ware: PartialUpdateWare): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ware);
    return this.http
      .patch<RestWare>(`${this.resourceUrl}/${this.getWareIdentifier(ware)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestWare>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestWare[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getWareIdentifier(ware: Pick<IWare, 'id'>): number {
    return ware.id;
  }

  compareWare(o1: Pick<IWare, 'id'> | null, o2: Pick<IWare, 'id'> | null): boolean {
    return o1 && o2 ? this.getWareIdentifier(o1) === this.getWareIdentifier(o2) : o1 === o2;
  }

  addWareToCollectionIfMissing<Type extends Pick<IWare, 'id'>>(
    wareCollection: Type[],
    ...waresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const wares: Type[] = waresToCheck.filter(isPresent);
    if (wares.length > 0) {
      const wareCollectionIdentifiers = wareCollection.map(wareItem => this.getWareIdentifier(wareItem));
      const waresToAdd = wares.filter(wareItem => {
        const wareIdentifier = this.getWareIdentifier(wareItem);
        if (wareCollectionIdentifiers.includes(wareIdentifier)) {
          return false;
        }
        wareCollectionIdentifiers.push(wareIdentifier);
        return true;
      });
      return [...waresToAdd, ...wareCollection];
    }
    return wareCollection;
  }

  protected convertDateFromClient<T extends IWare | NewWare | PartialUpdateWare>(ware: T): RestOf<T> {
    return {
      ...ware,
      created: ware.created?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restWare: RestWare): IWare {
    return {
      ...restWare,
      created: restWare.created ? dayjs(restWare.created) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestWare>): HttpResponse<IWare> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestWare[]>): HttpResponse<IWare[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }

  searchByParam(name?: string, active?: boolean): Observable<EntityArrayResponseType> {
    const options = {
      ...(name && { name }),
      ...(active !== undefined && { active: active.toString() }),
    };
    return this.http.get<IWare[]>(`${this.resourceUrl}/search`, { params: options, observe: 'response' });
  }
}
