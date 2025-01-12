import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDisposedStock, NewDisposedStock } from '../disposed-stock.model';
import { ISoldStock } from '../../sold-stock/sold-stock.model';

export type PartialUpdateDisposedStock = Partial<IDisposedStock> & Pick<IDisposedStock, 'id'>;

export type EntityResponseType = HttpResponse<IDisposedStock>;
export type EntityArrayResponseType = HttpResponse<IDisposedStock[]>;

@Injectable({ providedIn: 'root' })
export class DisposedStockService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/disposed-stocks');

  create(disposedStock: NewDisposedStock): Observable<EntityResponseType> {
    return this.http.post<IDisposedStock>(this.resourceUrl, disposedStock, { observe: 'response' });
  }

  update(disposedStock: IDisposedStock): Observable<EntityResponseType> {
    return this.http.put<IDisposedStock>(`${this.resourceUrl}/${this.getDisposedStockIdentifier(disposedStock)}`, disposedStock, {
      observe: 'response',
    });
  }

  partialUpdate(disposedStock: PartialUpdateDisposedStock): Observable<EntityResponseType> {
    return this.http.patch<IDisposedStock>(`${this.resourceUrl}/${this.getDisposedStockIdentifier(disposedStock)}`, disposedStock, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDisposedStock>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDisposedStock[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  findByDisposeId(disposeId: number): Observable<EntityArrayResponseType> {
    return this.http.get<IDisposedStock[]>(`${this.resourceUrl}/dispose/${disposeId}`, { observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDisposedStockIdentifier(disposedStock: Pick<IDisposedStock, 'id'>): number {
    return disposedStock.id;
  }

  compareDisposedStock(o1: Pick<IDisposedStock, 'id'> | null, o2: Pick<IDisposedStock, 'id'> | null): boolean {
    return o1 && o2 ? this.getDisposedStockIdentifier(o1) === this.getDisposedStockIdentifier(o2) : o1 === o2;
  }

  addDisposedStockToCollectionIfMissing<Type extends Pick<IDisposedStock, 'id'>>(
    disposedStockCollection: Type[],
    ...disposedStocksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const disposedStocks: Type[] = disposedStocksToCheck.filter(isPresent);
    if (disposedStocks.length > 0) {
      const disposedStockCollectionIdentifiers = disposedStockCollection.map(disposedStockItem =>
        this.getDisposedStockIdentifier(disposedStockItem),
      );
      const disposedStocksToAdd = disposedStocks.filter(disposedStockItem => {
        const disposedStockIdentifier = this.getDisposedStockIdentifier(disposedStockItem);
        if (disposedStockCollectionIdentifiers.includes(disposedStockIdentifier)) {
          return false;
        }
        disposedStockCollectionIdentifiers.push(disposedStockIdentifier);
        return true;
      });
      return [...disposedStocksToAdd, ...disposedStockCollection];
    }
    return disposedStockCollection;
  }
}
