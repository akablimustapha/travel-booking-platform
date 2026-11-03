import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import {
  Booking,
  OFFER_TYPE_LABELS,
  Offer,
  OfferRequest,
  OfferType,
  STATUS_LABELS,
  errorMessage,
} from '../../core/models';

const EMPTY_OFFER: OfferRequest = {
  title: '',
  description: '',
  type: 'OMRA',
  destination: '',
  price: 0,
  departureDate: '',
  returnDate: '',
  totalSeats: 10,
};

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [FormsModule, CurrencyPipe, DatePipe],
  template: `
    <h1>Administration</h1>
    <div class="tabs">
      <button class="chip" [class.selected]="tab() === 'bookings'" (click)="tab.set('bookings')">
        Réservations ({{ pendingCount() }} en attente)
      </button>
      <button class="chip" [class.selected]="tab() === 'offers'" (click)="tab.set('offers')">Offres</button>
    </div>

    @if (error()) {
      <p class="alert alert-error">{{ error() }}</p>
    }

    @if (tab() === 'bookings') {
      <div class="table-wrap">
        <table>
          <thead>
            <tr><th>Client</th><th>Offre</th><th>Places</th><th>Total</th><th>Date</th><th>Statut</th><th></th></tr>
          </thead>
          <tbody>
            @for (b of bookings(); track b.id) {
              <tr>
                <td>{{ b.customerName }}<br /><small class="muted">{{ b.customerEmail }}</small></td>
                <td>{{ b.offerTitle }}</td>
                <td>{{ b.seats }}</td>
                <td>{{ b.totalPrice | currency: 'MAD' : 'symbol' : '1.0-0' }}</td>
                <td>{{ b.createdAt | date: 'short' }}</td>
                <td><span class="status status-{{ b.status.toLowerCase() }}">{{ statusLabels[b.status] }}</span></td>
                <td>
                  <div class="actions">
                  @if (b.status === 'PENDING') {
                    <button class="btn btn-primary" (click)="confirmBooking(b)">Confirmer</button>
                  }
                  @if (b.status !== 'CANCELLED') {
                    <button class="btn btn-ghost" (click)="cancelBooking(b)">Annuler</button>
                  }
                </div>
                </td>
              </tr>
            } @empty {
              <tr><td colspan="7" class="muted">Aucune réservation.</td></tr>
            }
          </tbody>
        </table>
      </div>
    } @else {
      <form class="card form form-wide" (ngSubmit)="saveOffer()" #f="ngForm">
        <h2>{{ editingId() ? "Modifier l'offre" : 'Nouvelle offre' }}</h2>
        <div class="form-grid">
          <label>Titre <input name="title" [(ngModel)]="form.title" required /></label>
          <label>Type
            <select name="type" [(ngModel)]="form.type">
              @for (t of types; track t) {
                <option [value]="t">{{ typeLabels[t] }}</option>
              }
            </select>
          </label>
          <label>Destination <input name="destination" [(ngModel)]="form.destination" required /></label>
          <label>Prix (MAD) <input type="number" name="price" [(ngModel)]="form.price" required min="1" /></label>
          <label>Départ <input type="date" name="departureDate" [(ngModel)]="form.departureDate" required /></label>
          <label>Retour <input type="date" name="returnDate" [(ngModel)]="form.returnDate" required /></label>
          <label>Places <input type="number" name="totalSeats" [(ngModel)]="form.totalSeats" required min="1" /></label>
        </div>
        <label>Description <textarea name="description" rows="3" [(ngModel)]="form.description"></textarea></label>
        <div class="row">
          <button class="btn btn-primary" [disabled]="f.invalid">{{ editingId() ? 'Enregistrer' : 'Créer' }}</button>
          @if (editingId()) {
            <button type="button" class="btn btn-ghost" (click)="resetForm()">Annuler</button>
          }
        </div>
      </form>

      <div class="table-wrap">
        <table>
          <thead>
            <tr><th>Offre</th><th>Type</th><th>Départ</th><th>Prix</th><th>Places</th><th></th></tr>
          </thead>
          <tbody>
            @for (o of offers(); track o.id) {
              <tr>
                <td>{{ o.title }}</td>
                <td>{{ typeLabels[o.type] }}</td>
                <td>{{ o.departureDate | date: 'd MMM y' }}</td>
                <td>{{ o.price | currency: 'MAD' : 'symbol' : '1.0-0' }}</td>
                <td>{{ o.totalSeats - o.availableSeats }} / {{ o.totalSeats }}</td>
                <td>
                  <div class="actions">
                  <button class="btn btn-ghost" (click)="edit(o)">Modifier</button>
                  <button class="btn btn-ghost" (click)="deleteOffer(o)">Supprimer</button>
                </div>
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    }
  `,
})
export class AdminComponent implements OnInit {
  private api = inject(ApiService);

  readonly types: OfferType[] = ['OMRA', 'HAJJ', 'VOYAGE'];
  readonly typeLabels = OFFER_TYPE_LABELS;
  readonly statusLabels = STATUS_LABELS;

  tab = signal<'bookings' | 'offers'>('bookings');
  bookings = signal<Booking[]>([]);
  offers = signal<Offer[]>([]);
  editingId = signal<number | null>(null);
  error = signal('');
  form: OfferRequest = { ...EMPTY_OFFER };

  pendingCount = () => this.bookings().filter((b) => b.status === 'PENDING').length;

  ngOnInit(): void {
    this.loadBookings();
    this.loadOffers();
  }

  loadBookings(): void {
    this.api.allBookings().subscribe({ next: (l) => this.bookings.set(l), error: (e) => this.fail(e) });
  }

  loadOffers(): void {
    this.api.getOffers().subscribe({ next: (l) => this.offers.set(l), error: (e) => this.fail(e) });
  }

  confirmBooking(b: Booking): void {
    this.api.confirmBooking(b.id).subscribe({ next: () => this.loadBookings(), error: (e) => this.fail(e) });
  }

  cancelBooking(b: Booking): void {
    if (!confirm(`Annuler la réservation de ${b.customerName} ?`)) return;
    this.api.cancelBooking(b.id).subscribe({
      next: () => {
        this.loadBookings();
        this.loadOffers();
      },
      error: (e) => this.fail(e),
    });
  }

  edit(o: Offer): void {
    const { id, availableSeats, ...request } = o;
    this.form = { ...request };
    this.editingId.set(id);
  }

  resetForm(): void {
    this.form = { ...EMPTY_OFFER };
    this.editingId.set(null);
  }

  saveOffer(): void {
    this.error.set('');
    const id = this.editingId();
    const request$ = id ? this.api.updateOffer(id, this.form) : this.api.createOffer(this.form);
    request$.subscribe({
      next: () => {
        this.resetForm();
        this.loadOffers();
      },
      error: (e) => this.fail(e),
    });
  }

  deleteOffer(o: Offer): void {
    if (!confirm(`Supprimer l'offre « ${o.title} » ?`)) return;
    this.api.deleteOffer(o.id).subscribe({ next: () => this.loadOffers(), error: (e) => this.fail(e) });
  }

  private fail(err: unknown): void {
    this.error.set(errorMessage(err));
  }
}
