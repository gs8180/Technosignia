import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, Role, User } from '../models/models';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';
  private readonly TOKEN_KEY = 'contract_sys_jwt';
  private readonly USER_KEY = 'contract_sys_user';

  private currentUserSubject = new BehaviorSubject<User | null>(this.getStoredUser());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  public getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  public isLoggedIn(): boolean {
    return !!this.getToken() && !!this.currentUserValue;
  }

  public login(credentials: { username: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap((res) => {
        localStorage.setItem(this.TOKEN_KEY, res.token);
        const user: User = {
          id: res.id,
          username: res.username,
          email: res.email,
          fullName: res.fullName,
          role: res.role,
          active: true
        };
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
        this.currentUserSubject.next(user);
      })
    );
  }

  public updateCurrentUser(updatedFields: Partial<User>): void {
    const current = this.currentUserValue;
    if (current) {
      const updated: User = { ...current, ...updatedFields };
      localStorage.setItem(this.USER_KEY, JSON.stringify(updated));
      this.currentUserSubject.next(updated);
    }
  }

  public logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  public hasRole(roles: Role[]): boolean {
    const user = this.currentUserValue;
    if (!user) return false;
    return roles.includes(user.role);
  }

  public isAdmin(): boolean {
    return this.currentUserValue?.role === 'ROLE_ADMIN';
  }

  public isApprover(): boolean {
    const r = this.currentUserValue?.role;
    return r === 'ROLE_APPROVER' || r === 'ROLE_ADMIN';
  }

  public isManager(): boolean {
    const r = this.currentUserValue?.role;
    return r === 'ROLE_CONTRACT_MANAGER' || r === 'ROLE_ADMIN';
  }

  private getStoredUser(): User | null {
    const stored = localStorage.getItem(this.USER_KEY);
    if (!stored) return null;
    try {
      return JSON.parse(stored);
    } catch {
      return null;
    }
  }
}
