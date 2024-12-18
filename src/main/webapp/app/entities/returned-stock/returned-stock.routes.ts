/* eslint-disable */
import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ReturnedStockResolve from './route/returned-stock-routing-resolve.service';

const returnedStockRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/returned-stock.component').then(m => m.ReturnedStockComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/returned-stock-detail.component').then(m => m.ReturnedStockDetailComponent),
    resolve: {
      returnedStock: ReturnedStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/returned-stock-update.component').then(m => m.ReturnedStockUpdateComponent),
    resolve: {
      returnedStock: ReturnedStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':productReturnId/new',
    loadComponent: () => import('./update/returned-stock-update.component').then(m => m.ReturnedStockUpdateComponent),
    resolve: {
      returnedStock: ReturnedStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/returned-stock-update.component').then(m => m.ReturnedStockUpdateComponent),
    resolve: {
      returnedStock: ReturnedStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default returnedStockRoute;
