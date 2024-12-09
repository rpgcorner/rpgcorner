import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPurchasedStock, NewPurchasedStock } from '../purchased-stock.model';

export type PartialUpdatePurchasedStock = Partial<IPurchasedStock> & Pick<IPurchasedStock, 'id'>;

export type EntityResponseType = HttpResponse<IPurchasedStock>;
export type EntityArrayResponseType = HttpResponse<IPurchasedStock[]>;

@Injectable({ providedIn: 'root' })
export class PurchasedStockService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/purchased-stocks');

  create(purchasedStock: NewPurchasedStock): Observable<EntityResponseType> {
    return this.http.post<IPurchasedStock>(this.resourceUrl, purchasedStock, { observe: 'response' });
  }

  update(purchasedStock: IPurchasedStock): Observable<EntityResponseType> {
    return this.http.put<IPurchasedStock>(`${this.resourceUrl}/${this.getPurchasedStockIdentifier(purchasedStock)}`, purchasedStock, {
      observe: 'response',
    });
  }

  partialUpdate(purchasedStock: PartialUpdatePurchasedStock): Observable<EntityResponseType> {
    return this.http.patch<IPurchasedStock>(`${this.resourceUrl}/${this.getPurchasedStockIdentifier(purchasedStock)}`, purchasedStock, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPurchasedStock>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPurchasedStock[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPurchasedStockIdentifier(purchasedStock: Pick<IPurchasedStock, 'id'>): number {
    return purchasedStock.id;
  }

  comparePurchasedStock(o1: Pick<IPurchasedStock, 'id'> | null, o2: Pick<IPurchasedStock, 'id'> | null): boolean {
    return o1 && o2 ? this.getPurchasedStockIdentifier(o1) === this.getPurchasedStockIdentifier(o2) : o1 === o2;
  }

  addPurchasedStockToCollectionIfMissing<Type extends Pick<IPurchasedStock, 'id'>>(
    purchasedStockCollection: Type[],
    ...purchasedStocksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const purchasedStocks: Type[] = purchasedStocksToCheck.filter(isPresent);
    if (purchasedStocks.length > 0) {
      const purchasedStockCollectionIdentifiers = purchasedStockCollection.map(purchasedStockItem =>
        this.getPurchasedStockIdentifier(purchasedStockItem),
      );
      const purchasedStocksToAdd = purchasedStocks.filter(purchasedStockItem => {
        const purchasedStockIdentifier = this.getPurchasedStockIdentifier(purchasedStockItem);
        if (purchasedStockCollectionIdentifiers.includes(purchasedStockIdentifier)) {
          return false;
        }
        purchasedStockCollectionIdentifiers.push(purchasedStockIdentifier);
        return true;
      });
      return [...purchasedStocksToAdd, ...purchasedStockCollection];
    }
    return purchasedStockCollection;
  }
}
