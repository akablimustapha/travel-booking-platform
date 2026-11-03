import { Component, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { errorMessage } from '../core/models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <form class="card form" (ngSubmit)="submit()" #f="ngForm">
      <h1>Connexion</h1>
      <label>Email <input type="email" name="email" [(ngModel)]="email" required email /></label>
      <label>Mot de passe <input type="password" name="password" [(ngModel)]="password" required /></label>
      @if (error()) {
        <p class="alert alert-error">{{ error() }}</p>
      }
      <button class="btn btn-primary btn-block" [disabled]="f.invalid || sending()">Se connecter</button>
      <p class="muted">Pas de compte ? <a routerLink="/inscription">Créer un compte</a></p>
      <p class="hint">Comptes démo : client&#64;demo.ma / Client123! · admin&#64;demo.ma / Admin123!</p>
    </form>
  `,
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  redirect = input<string>();
  email = '';
  password = '';
  sending = signal(false);
  error = signal('');

  submit(): void {
    this.sending.set(true);
    this.error.set('');
    this.auth.login(this.email, this.password).subscribe({
      next: (res) => this.router.navigateByUrl(this.redirect() || (res.role === 'ADMIN' ? '/admin' : '/')),
      error: (err) => {
        this.error.set(errorMessage(err));
        this.sending.set(false);
      },
    });
  }
}
