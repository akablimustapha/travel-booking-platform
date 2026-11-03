import { Component, OnInit, inject, input, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { OFFER_TYPE_LABELS, Offer, errorMessage } from '../core/models';

@Component({
  selector: 'app-offer-detail',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, FormsModule, RouterLink],
  template: `
    <a routerLink="/" class="back">← Toutes les offres</a>
    @if (offer(); as o) {
      <div class="detail">
        <div>
          <span class="badge badge-{{ o.type.toLowerCase() }}">{{ labels[o.type] }}</span>
          <h1>{{ o.title }}</h1>
          <p class="muted">{{ o.destination }}</p>
          <p>{{ o.description }}</p>
          <ul class="facts">
            <li>📅 Départ : {{ o.departureDate | date: 'fullDate' }}</li>
            <li>🏁 Retour : {{ o.returnDate | date: 'fullDate' }}</li>
            <li>💺 {{ o.availableSeats }} / {{ o.totalSeats }} places disponibles</li>
          </ul>
        </div>

        <aside class="card booking-box">
          <p class="price">{{ o.price | currency: 'MAD' : 'symbol' : '1.0-0' }} <small>/ personne</small></p>
          @if (o.availableSeats === 0) {
            <p class="alert alert-error">Complet</p>
          } @else {
            <label>
              Nombre de places
              <input type="number" min="1" [max]="maxSeats(o)" [(ngModel)]="seats" />
            </label>
            <p>Total : <strong>{{ o.price * seats | currency: 'MAD' : 'symbol' : '1.0-0' }}</strong></p>
            @if (auth.isLoggedIn()) {
              <button class="btn btn-primary btn-block" [disabled]="sending()" (click)="book(o)">
                {{ sending() ? 'Réservation…' : 'Réserver' }}
              </button>
            } @else {
              <a class="btn btn-primary btn-block" routerLink="/login" [queryParams]="{ redirect: '/offres/' + o.id }">
                Se connecter pour réserver
              </a>
            }
          }
          @if (error()) {
            <p class="alert alert-error">{{ error() }}</p>
          }
        </aside>
      </div>
    } @else if (error()) {
      <p class="alert alert-error">{{ error() }}</p>
    } @else {
      <p class="muted">Chargement…</p>
    }
  `,
})
export class OfferDetailComponent implements OnInit {
  private api = inject(ApiService);
  private router = inject(Router);
  auth = inject(AuthService);

  /** Paramètre de route ":id" (withComponentInputBinding). */
  id = input.required<string>();

  readonly labels = OFFER_TYPE_LABELS;
  offer = signal<Offer | null>(null);
  error = signal('');
  sending = signal(false);
  seats = 1;

  ngOnInit(): void {
    this.api.getOffer(Number(this.id())).subscribe({
      next: (o) => this.offer.set(o),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  maxSeats(o: Offer): number {
    return Math.min(10, o.availableSeats);
  }

  book(o: Offer): void {
    this.sending.set(true);
    this.error.set('');
    this.api.book(o.id, this.seats).subscribe({
      next: () => this.router.navigate(['/mes-reservations'], { queryParams: { ok: 1 } }),
      error: (err) => {
        this.error.set(errorMessage(err));
        this.sending.set(false);
      },
    });
  }
}
