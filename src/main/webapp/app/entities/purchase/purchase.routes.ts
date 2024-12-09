import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PurchaseResolve from './route/purchase-routing-resolve.service';

const purchaseRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/purchase.component').then(m => m.PurchaseComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/purchase-detail.component').then(m => m.PurchaseDetailComponent),
    resolve: {
      purchase: PurchaseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/purchase-update.component').then(m => m.PurchaseUpdateComponent),
    resolve: {
      purchase: PurchaseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/purchase-update.component').then(m => m.PurchaseUpdateComponent),
    resolve: {
      purchase: PurchaseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default purchaseRoute;
