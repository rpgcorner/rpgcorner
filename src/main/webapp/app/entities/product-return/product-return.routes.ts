import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ProductReturnResolve from './route/product-return-routing-resolve.service';

const productReturnRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/product-return.component').then(m => m.ProductReturnComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/product-return-detail.component').then(m => m.ProductReturnDetailComponent),
    resolve: {
      productReturn: ProductReturnResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/product-return-update.component').then(m => m.ProductReturnUpdateComponent),
    resolve: {
      productReturn: ProductReturnResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/product-return-update.component').then(m => m.ProductReturnUpdateComponent),
    resolve: {
      productReturn: ProductReturnResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default productReturnRoute;
