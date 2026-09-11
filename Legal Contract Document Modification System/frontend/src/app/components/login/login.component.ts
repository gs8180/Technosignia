import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';
  loading = false;
  showPassword = false;
  returnUrl?: string;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    if (this.authService.isLoggedIn()) {
      this.navigateForRole();
    }
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'];
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (!this.username.trim() || !this.password) {
      this.errorMessage = 'Please enter both username and password.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login({ username: this.username.trim(), password: this.password }).subscribe({
      next: (res) => {
        this.loading = false;
        if (this.returnUrl && this.returnUrl !== '/' && this.returnUrl !== '/dashboard') {
          this.router.navigateByUrl(this.returnUrl);
        } else {
          this.navigateForRole(res.role);
        }
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 401 || err.status === 400) {
          this.errorMessage = 'Invalid username or password. Please verify your credentials.';
        } else if (err.status === 0) {
          this.errorMessage = 'Unable to connect to authentication server. Please ensure backend is running.';
        } else {
          this.errorMessage = err.error?.message || 'Authentication failed. Please try again.';
        }
      }
    });
  }

  private navigateForRole(role?: string): void {
    const userRole = role || this.authService.currentUserValue?.role;
    switch (userRole) {
      case 'ROLE_APPROVER':
        this.router.navigate(['/approvals']);
        break;
      case 'ROLE_CONTRACT_MANAGER':
        this.router.navigate(['/contracts']);
        break;
      case 'ROLE_ADMIN':
        this.router.navigate(['/dashboard']);
        break;
      case 'ROLE_USER':
      default:
        this.router.navigate(['/contracts']);
        break;
    }
  }
}
