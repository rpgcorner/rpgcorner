import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SoldStockResolve from './route/sold-stock-routing-resolve.service';

const soldStockRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sold-stock.component').then(m => m.SoldStockComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/sold-stock-detail.component').then(m => m.SoldStockDetailComponent),
    resolve: {
      soldStock: SoldStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sold-stock-update.component').then(m => m.SoldStockUpdateComponent),
    resolve: {
      soldStock: SoldStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':salesId/new',
    loadComponent: () => import('./update/sold-stock-update.component').then(m => m.SoldStockUpdateComponent),
    resolve: {
      soldStock: SoldStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sold-stock-update.component').then(m => m.SoldStockUpdateComponent),
    resolve: {
      soldStock: SoldStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default soldStockRoute;
