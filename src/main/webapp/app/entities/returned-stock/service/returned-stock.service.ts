import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReturnedStock, NewReturnedStock } from '../returned-stock.model';

export type PartialUpdateReturnedStock = Partial<IReturnedStock> & Pick<IReturnedStock, 'id'>;

export type EntityResponseType = HttpResponse<IReturnedStock>;
export type EntityArrayResponseType = HttpResponse<IReturnedStock[]>;

@Injectable({ providedIn: 'root' })
export class ReturnedStockService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/returned-stocks');

  create(returnedStock: NewReturnedStock): Observable<EntityResponseType> {
    return this.http.post<IReturnedStock>(this.resourceUrl, returnedStock, { observe: 'response' });
  }

  update(returnedStock: IReturnedStock): Observable<EntityResponseType> {
    return this.http.put<IReturnedStock>(`${this.resourceUrl}/${this.getReturnedStockIdentifier(returnedStock)}`, returnedStock, {
      observe: 'response',
    });
  }

  partialUpdate(returnedStock: PartialUpdateReturnedStock): Observable<EntityResponseType> {
    return this.http.patch<IReturnedStock>(`${this.resourceUrl}/${this.getReturnedStockIdentifier(returnedStock)}`, returnedStock, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IReturnedStock>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IReturnedStock[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getReturnedStockIdentifier(returnedStock: Pick<IReturnedStock, 'id'>): number {
    return returnedStock.id;
  }

  compareReturnedStock(o1: Pick<IReturnedStock, 'id'> | null, o2: Pick<IReturnedStock, 'id'> | null): boolean {
    return o1 && o2 ? this.getReturnedStockIdentifier(o1) === this.getReturnedStockIdentifier(o2) : o1 === o2;
  }

  addReturnedStockToCollectionIfMissing<Type extends Pick<IReturnedStock, 'id'>>(
    returnedStockCollection: Type[],
    ...returnedStocksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const returnedStocks: Type[] = returnedStocksToCheck.filter(isPresent);
    if (returnedStocks.length > 0) {
      const returnedStockCollectionIdentifiers = returnedStockCollection.map(returnedStockItem =>
        this.getReturnedStockIdentifier(returnedStockItem),
      );
      const returnedStocksToAdd = returnedStocks.filter(returnedStockItem => {
        const returnedStockIdentifier = this.getReturnedStockIdentifier(returnedStockItem);
        if (returnedStockCollectionIdentifiers.includes(returnedStockIdentifier)) {
          return false;
        }
        returnedStockCollectionIdentifiers.push(returnedStockIdentifier);
        return true;
      });
      return [...returnedStocksToAdd, ...returnedStockCollection];
    }
    return returnedStockCollection;
  }
}
