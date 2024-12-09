import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PurchasedStockResolve from './route/purchased-stock-routing-resolve.service';

const purchasedStockRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/purchased-stock.component').then(m => m.PurchasedStockComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/purchased-stock-detail.component').then(m => m.PurchasedStockDetailComponent),
    resolve: {
      purchasedStock: PurchasedStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/purchased-stock-update.component').then(m => m.PurchasedStockUpdateComponent),
    resolve: {
      purchasedStock: PurchasedStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/purchased-stock-update.component').then(m => m.PurchasedStockUpdateComponent),
    resolve: {
      purchasedStock: PurchasedStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default purchasedStockRoute;
