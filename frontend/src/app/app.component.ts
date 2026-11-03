import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <header class="topbar">
      <a routerLink="/" class="brand">✈ Travel Booking</a>
      <nav>
        <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">Offres</a>
        @if (auth.isLoggedIn()) {
          <a routerLink="/mes-reservations" routerLinkActive="active">Mes réservations</a>
          @if (auth.isAdmin()) {
            <a routerLink="/admin" routerLinkActive="active">Administration</a>
          }
          <span class="user">{{ auth.user()?.fullName }}</span>
          <button class="btn btn-ghost" (click)="auth.logout()">Déconnexion</button>
        } @else {
          <a routerLink="/login" class="btn btn-primary">Connexion</a>
        }
      </nav>
    </header>
    <main class="container">
      <router-outlet />
    </main>
    <footer class="footer">Projet démo · Spring Boot · Angular · PostgreSQL</footer>
  `,
})
export class AppComponent {
  auth = inject(AuthService);
}
