import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { OFFER_TYPE_LABELS, Offer, OfferType, errorMessage } from '../core/models';

@Component({
  selector: 'app-offers-list',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, DatePipe],
  template: `
    <section class="hero">
      <h1>Réservez votre Omra, votre Hajj ou votre prochain voyage</h1>
      <p>Places en temps réel, réservation en ligne et suivi depuis votre espace client.</p>
    </section>

    <div class="filters">
      <button class="chip" [class.selected]="!type()" (click)="load()">Toutes</button>
      @for (t of types; track t) {
        <button class="chip" [class.selected]="type() === t" (click)="load(t)">{{ labels[t] }}</button>
      }
    </div>

    @if (error()) {
      <p class="alert alert-error">{{ error() }}</p>
    }
    @if (loading()) {
      <p class="muted">Chargement…</p>
    } @else {
      <div class="grid">
        @for (offer of offers(); track offer.id) {
          <article class="card">
            <span class="badge badge-{{ offer.type.toLowerCase() }}">{{ labels[offer.type] }}</span>
            <h2>{{ offer.title }}</h2>
            <p class="muted">{{ offer.destination }}</p>
            <p>📅 {{ offer.departureDate | date: 'd MMM y' }} → {{ offer.returnDate | date: 'd MMM y' }}</p>
            <p class="seats" [class.low]="offer.availableSeats <= 3">
              {{ offer.availableSeats }} / {{ offer.totalSeats }} places disponibles
            </p>
            <div class="card-footer">
              <strong class="price">{{ offer.price | currency: 'MAD' : 'symbol' : '1.0-0' }}</strong>
              <a class="btn btn-primary" [routerLink]="['/offres', offer.id]">Voir l'offre</a>
            </div>
          </article>
        } @empty {
          <p class="muted">Aucune offre pour le moment.</p>
        }
      </div>
    }
  `,
})
export class OffersListComponent implements OnInit {
  private api = inject(ApiService);

  readonly types: OfferType[] = ['OMRA', 'HAJJ', 'VOYAGE'];
  readonly labels = OFFER_TYPE_LABELS;
  offers = signal<Offer[]>([]);
  type = signal<OfferType | undefined>(undefined);
  loading = signal(true);
  error = signal('');

  ngOnInit(): void {
    this.load();
  }

  load(type?: OfferType): void {
    this.type.set(type);
    this.loading.set(true);
    this.error.set('');
    this.api.getOffers(type).subscribe({
      next: (offers) => {
        this.offers.set(offers);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }
}
