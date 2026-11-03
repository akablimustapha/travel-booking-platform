import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './core/guards';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/offers-list.component').then((m) => m.OffersListComponent) },
  { path: 'offres/:id', loadComponent: () => import('./pages/offer-detail.component').then((m) => m.OfferDetailComponent) },
  { path: 'login', loadComponent: () => import('./pages/login.component').then((m) => m.LoginComponent) },
  { path: 'inscription', loadComponent: () => import('./pages/register.component').then((m) => m.RegisterComponent) },
  {
    path: 'mes-reservations',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/my-bookings.component').then((m) => m.MyBookingsComponent),
  },
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./pages/admin/admin.component').then((m) => m.AdminComponent),
  },
  { path: '**', redirectTo: '' },
];
