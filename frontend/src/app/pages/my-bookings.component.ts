import { Component, OnInit, inject, input, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { ApiService } from '../core/api.service';
import { Booking, STATUS_LABELS, errorMessage } from '../core/models';

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [CurrencyPipe, DatePipe],
  template: `
    <h1>Mes réservations</h1>
    @if (ok()) {
      <p class="alert alert-success">Réservation enregistrée ! Elle sera confirmée par l'agence.</p>
    }
    @if (error()) {
      <p class="alert alert-error">{{ error() }}</p>
    }
    <div class="table-wrap">
      <table>
        <thead>
          <tr><th>Offre</th><th>Départ</th><th>Places</th><th>Total</th><th>Statut</th><th></th></tr>
        </thead>
        <tbody>
          @for (b of bookings(); track b.id) {
            <tr>
              <td>{{ b.offerTitle }}</td>
              <td>{{ b.departureDate | date: 'd MMM y' }}</td>
              <td>{{ b.seats }}</td>
              <td>{{ b.totalPrice | currency: 'MAD' : 'symbol' : '1.0-0' }}</td>
              <td><span class="status status-{{ b.status.toLowerCase() }}">{{ statusLabels[b.status] }}</span></td>
              <td>
                @if (b.status !== 'CANCELLED') {
                  <button class="btn btn-ghost" (click)="cancel(b)">Annuler</button>
                }
              </td>
            </tr>
          } @empty {
            <tr><td colspan="6" class="muted">Aucune réservation pour le moment.</td></tr>
          }
        </tbody>
      </table>
    </div>
  `,
})
export class MyBookingsComponent implements OnInit {
  private api = inject(ApiService);

  /** Query param "?ok=1" après une réservation réussie. */
  ok = input<string>();
  readonly statusLabels = STATUS_LABELS;
  bookings = signal<Booking[]>([]);
  error = signal('');

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.api.myBookings().subscribe({
      next: (list) => this.bookings.set(list),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  cancel(b: Booking): void {
    if (!confirm(`Annuler la réservation « ${b.offerTitle} » ?`)) return;
    this.api.cancelMyBooking(b.id).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
