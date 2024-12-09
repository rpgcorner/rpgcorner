import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISoldStock, NewSoldStock } from '../sold-stock.model';

export type PartialUpdateSoldStock = Partial<ISoldStock> & Pick<ISoldStock, 'id'>;

export type EntityResponseType = HttpResponse<ISoldStock>;
export type EntityArrayResponseType = HttpResponse<ISoldStock[]>;

@Injectable({ providedIn: 'root' })
export class SoldStockService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sold-stocks');

  create(soldStock: NewSoldStock): Observable<EntityResponseType> {
    return this.http.post<ISoldStock>(this.resourceUrl, soldStock, { observe: 'response' });
  }

  update(soldStock: ISoldStock): Observable<EntityResponseType> {
    return this.http.put<ISoldStock>(`${this.resourceUrl}/${this.getSoldStockIdentifier(soldStock)}`, soldStock, { observe: 'response' });
  }

  partialUpdate(soldStock: PartialUpdateSoldStock): Observable<EntityResponseType> {
    return this.http.patch<ISoldStock>(`${this.resourceUrl}/${this.getSoldStockIdentifier(soldStock)}`, soldStock, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISoldStock>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISoldStock[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSoldStockIdentifier(soldStock: Pick<ISoldStock, 'id'>): number {
    return soldStock.id;
  }

  compareSoldStock(o1: Pick<ISoldStock, 'id'> | null, o2: Pick<ISoldStock, 'id'> | null): boolean {
    return o1 && o2 ? this.getSoldStockIdentifier(o1) === this.getSoldStockIdentifier(o2) : o1 === o2;
  }

  addSoldStockToCollectionIfMissing<Type extends Pick<ISoldStock, 'id'>>(
    soldStockCollection: Type[],
    ...soldStocksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const soldStocks: Type[] = soldStocksToCheck.filter(isPresent);
    if (soldStocks.length > 0) {
      const soldStockCollectionIdentifiers = soldStockCollection.map(soldStockItem => this.getSoldStockIdentifier(soldStockItem));
      const soldStocksToAdd = soldStocks.filter(soldStockItem => {
        const soldStockIdentifier = this.getSoldStockIdentifier(soldStockItem);
        if (soldStockCollectionIdentifiers.includes(soldStockIdentifier)) {
          return false;
        }
        soldStockCollectionIdentifiers.push(soldStockIdentifier);
        return true;
      });
      return [...soldStocksToAdd, ...soldStockCollection];
    }
    return soldStockCollection;
  }
}
