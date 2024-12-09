import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import WareResolve from './route/ware-routing-resolve.service';

const wareRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ware.component').then(m => m.WareComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ware-detail.component').then(m => m.WareDetailComponent),
    resolve: {
      ware: WareResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ware-update.component').then(m => m.WareUpdateComponent),
    resolve: {
      ware: WareResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ware-update.component').then(m => m.WareUpdateComponent),
    resolve: {
      ware: WareResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default wareRoute;
