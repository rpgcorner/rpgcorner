import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DisposedStockResolve from './route/disposed-stock-routing-resolve.service';

const disposedStockRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/disposed-stock.component').then(m => m.DisposedStockComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/disposed-stock-detail.component').then(m => m.DisposedStockDetailComponent),
    resolve: {
      disposedStock: DisposedStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/disposed-stock-update.component').then(m => m.DisposedStockUpdateComponent),
    resolve: {
      disposedStock: DisposedStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/disposed-stock-update.component').then(m => m.DisposedStockUpdateComponent),
    resolve: {
      disposedStock: DisposedStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default disposedStockRoute;
