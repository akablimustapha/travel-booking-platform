import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Booking, Offer, OfferRequest, OfferType } from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);

  // --- Offres (public) ---
  getOffers(type?: OfferType): Observable<Offer[]> {
    const params = type ? new HttpParams().set('type', type) : undefined;
    return this.http.get<Offer[]>('/api/offers', { params });
  }

  getOffer(id: number): Observable<Offer> {
    return this.http.get<Offer>(`/api/offers/${id}`);
  }

  // --- Réservations (client connecté) ---
  book(offerId: number, seats: number): Observable<Booking> {
    return this.http.post<Booking>('/api/bookings', { offerId, seats });
  }

  myBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>('/api/bookings/me');
  }

  cancelMyBooking(id: number): Observable<Booking> {
    return this.http.patch<Booking>(`/api/bookings/${id}/cancel`, {});
  }

  // --- Administration ---
  createOffer(offer: OfferRequest): Observable<Offer> {
    return this.http.post<Offer>('/api/admin/offers', offer);
  }

  updateOffer(id: number, offer: OfferRequest): Observable<Offer> {
    return this.http.put<Offer>(`/api/admin/offers/${id}`, offer);
  }

  deleteOffer(id: number): Observable<void> {
    return this.http.delete<void>(`/api/admin/offers/${id}`);
  }

  allBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>('/api/admin/bookings');
  }

  confirmBooking(id: number): Observable<Booking> {
    return this.http.patch<Booking>(`/api/admin/bookings/${id}/confirm`, {});
  }

  cancelBooking(id: number): Observable<Booking> {
    return this.http.patch<Booking>(`/api/admin/bookings/${id}/cancel`, {});
  }
}
