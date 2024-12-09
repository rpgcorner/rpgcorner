import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'rpgCornerApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'category',
    data: { pageTitle: 'rpgCornerApp.category.home.title' },
    loadChildren: () => import('./category/category.routes'),
  },
  {
    path: 'contact',
    data: { pageTitle: 'rpgCornerApp.contact.home.title' },
    loadChildren: () => import('./contact/contact.routes'),
  },
  {
    path: 'customer',
    data: { pageTitle: 'rpgCornerApp.customer.home.title' },
    loadChildren: () => import('./customer/customer.routes'),
  },
  {
    path: 'dispose',
    data: { pageTitle: 'rpgCornerApp.dispose.home.title' },
    loadChildren: () => import('./dispose/dispose.routes'),
  },
  {
    path: 'disposed-stock',
    data: { pageTitle: 'rpgCornerApp.disposedStock.home.title' },
    loadChildren: () => import('./disposed-stock/disposed-stock.routes'),
  },
  {
    path: 'inventory',
    data: { pageTitle: 'rpgCornerApp.inventory.home.title' },
    loadChildren: () => import('./inventory/inventory.routes'),
  },
  {
    path: 'product-return',
    data: { pageTitle: 'rpgCornerApp.productReturn.home.title' },
    loadChildren: () => import('./product-return/product-return.routes'),
  },
  {
    path: 'purchase',
    data: { pageTitle: 'rpgCornerApp.purchase.home.title' },
    loadChildren: () => import('./purchase/purchase.routes'),
  },
  {
    path: 'purchased-stock',
    data: { pageTitle: 'rpgCornerApp.purchasedStock.home.title' },
    loadChildren: () => import('./purchased-stock/purchased-stock.routes'),
  },
  {
    path: 'returned-stock',
    data: { pageTitle: 'rpgCornerApp.returnedStock.home.title' },
    loadChildren: () => import('./returned-stock/returned-stock.routes'),
  },
  {
    path: 'sale',
    data: { pageTitle: 'rpgCornerApp.sale.home.title' },
    loadChildren: () => import('./sale/sale.routes'),
  },
  {
    path: 'sold-stock',
    data: { pageTitle: 'rpgCornerApp.soldStock.home.title' },
    loadChildren: () => import('./sold-stock/sold-stock.routes'),
  },
  {
    path: 'supplier',
    data: { pageTitle: 'rpgCornerApp.supplier.home.title' },
    loadChildren: () => import('./supplier/supplier.routes'),
  },
  {
    path: 'ware',
    data: { pageTitle: 'rpgCornerApp.ware.home.title' },
    loadChildren: () => import('./ware/ware.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
