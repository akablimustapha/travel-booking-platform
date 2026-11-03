export type OfferType = 'OMRA' | 'HAJJ' | 'VOYAGE';
export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED';
export type Role = 'CLIENT' | 'ADMIN';

export interface Offer {
  id: number;
  title: string;
  description: string;
  type: OfferType;
  destination: string;
  price: number;
  departureDate: string;
  returnDate: string;
  totalSeats: number;
  availableSeats: number;
}

export type OfferRequest = Omit<Offer, 'id' | 'availableSeats'>;

export interface Booking {
  id: number;
  offerId: number;
  offerTitle: string;
  departureDate: string;
  customerName: string;
  customerEmail: string;
  seats: number;
  totalPrice: number;
  status: BookingStatus;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  fullName: string;
  email: string;
  role: Role;
}

export interface ApiError {
  status: number;
  message: string;
  errors: Record<string, string>;
}

export const OFFER_TYPE_LABELS: Record<OfferType, string> = {
  OMRA: 'Omra',
  HAJJ: 'Hajj',
  VOYAGE: 'Voyage',
};

export const STATUS_LABELS: Record<BookingStatus, string> = {
  PENDING: 'En attente',
  CONFIRMED: 'Confirmée',
  CANCELLED: 'Annulée',
};

/** Extrait un message lisible d'une erreur HTTP renvoyée par l'API. */
export function errorMessage(err: unknown): string {
  const body = (err as { error?: ApiError })?.error;
  if (body?.errors && Object.keys(body.errors).length) {
    return Object.values(body.errors).join(' · ');
  }
  return body?.message ?? 'Une erreur est survenue. Réessayez.';
}
