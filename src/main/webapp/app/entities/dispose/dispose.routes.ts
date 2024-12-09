import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DisposeResolve from './route/dispose-routing-resolve.service';

const disposeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/dispose.component').then(m => m.DisposeComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/dispose-detail.component').then(m => m.DisposeDetailComponent),
    resolve: {
      dispose: DisposeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/dispose-update.component').then(m => m.DisposeUpdateComponent),
    resolve: {
      dispose: DisposeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/dispose-update.component').then(m => m.DisposeUpdateComponent),
    resolve: {
      dispose: DisposeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default disposeRoute;
