import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { errorMessage } from '../core/models';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <form class="card form" (ngSubmit)="submit()" #f="ngForm">
      <h1>Créer un compte</h1>
      <label>Nom complet <input name="fullName" [(ngModel)]="fullName" required /></label>
      <label>Email <input type="email" name="email" [(ngModel)]="email" required email /></label>
      <label>Mot de passe (6 caractères min.)
        <input type="password" name="password" [(ngModel)]="password" required minlength="6" />
      </label>
      @if (error()) {
        <p class="alert alert-error">{{ error() }}</p>
      }
      <button class="btn btn-primary btn-block" [disabled]="f.invalid || sending()">Créer mon compte</button>
      <p class="muted">Déjà inscrit ? <a routerLink="/login">Se connecter</a></p>
    </form>
  `,
})
export class RegisterComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  fullName = '';
  email = '';
  password = '';
  sending = signal(false);
  error = signal('');

  submit(): void {
    this.sending.set(true);
    this.error.set('');
    this.auth.register(this.fullName, this.email, this.password).subscribe({
      next: () => this.router.navigateByUrl('/'),
      error: (err) => {
        this.error.set(errorMessage(err));
        this.sending.set(false);
      },
    });
  }
}
