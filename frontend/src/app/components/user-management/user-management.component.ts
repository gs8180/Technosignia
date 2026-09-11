import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Role, User } from '../../models/models';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  loading = true;

  // Modal State
  showModal = false;
  isEditMode = false;
  isSelfProfile = false;
  editUserId: number | null = null;
  formUsername = '';
  formEmail = '';
  formPassword = '';
  formFullName = '';
  formRole: Role = 'ROLE_USER';
  formActive = true;
  modalError = '';
  successToast = '';

  constructor(
    private apiService: ApiService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.apiService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  isCurrentUser(user: User): boolean {
    return user.id === this.authService.currentUserValue?.id;
  }

  openSelfProfile(): void {
    const current = this.authService.currentUserValue;
    if (!current) return;
    const found = this.users.find(u => u.id === current.id) || current;
    this.openEditModal(found);
  }

  openAddModal(): void {
    this.isEditMode = false;
    this.isSelfProfile = false;
    this.editUserId = null;
    this.formUsername = '';
    this.formEmail = '';
    this.formPassword = '';
    this.formFullName = '';
    this.formRole = 'ROLE_USER';
    this.formActive = true;
    this.modalError = '';
    this.showModal = true;
  }

  openEditModal(user: User): void {
    this.isEditMode = true;
    this.editUserId = user.id;
    this.isSelfProfile = this.isCurrentUser(user);
    this.formUsername = user.username;
    this.formEmail = user.email;
    this.formFullName = user.fullName;
    this.formRole = user.role;
    this.formActive = user.active;
    this.formPassword = '';
    this.modalError = '';
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  saveUser(): void {
    if (!this.isEditMode && (!this.formUsername || !this.formEmail || !this.formPassword || !this.formFullName)) {
      this.modalError = 'Please fill all required fields';
      return;
    }

    if (this.isEditMode && this.editUserId) {
      const payload: {
        fullName: string;
        email: string;
        role: Role;
        active: boolean;
        password?: string;
      } = {
        fullName: this.formFullName.trim(),
        email: this.formEmail.trim(),
        role: this.formRole,
        active: this.formActive
      };

      if (this.formPassword && this.formPassword.trim().length > 0) {
        payload.password = this.formPassword.trim();
      }

      this.apiService.updateUser(this.editUserId, payload).subscribe({
        next: (updatedUser) => {
          if (this.editUserId === this.authService.currentUserValue?.id) {
            this.authService.updateCurrentUser({
              fullName: updatedUser.fullName,
              email: updatedUser.email,
              role: updatedUser.role
            });
            this.triggerToast('Your admin profile has been updated successfully!');
          } else {
            this.triggerToast(`User '${updatedUser.username}' updated successfully.`);
          }
          this.closeModal();
          this.loadUsers();
        },
        error: (err) => {
          this.modalError = err.error?.message || 'Error updating user';
        }
      });
    } else {
      const payload = {
        username: this.formUsername.trim(),
        email: this.formEmail.trim(),
        password: this.formPassword.trim(),
        fullName: this.formFullName.trim(),
        role: this.formRole
      };
      this.apiService.createUser(payload).subscribe({
        next: () => {
          this.triggerToast('New user created successfully.');
          this.closeModal();
          this.loadUsers();
        },
        error: (err) => {
          this.modalError = err.error?.message || 'Error creating user';
        }
      });
    }
  }

  deleteUser(user: User): void {
    if (this.isCurrentUser(user)) {
      alert('You cannot delete your own logged-in administrator account.');
      return;
    }
    if (confirm(`Are you sure you want to delete user '${user.username}'?`)) {
      this.apiService.deleteUser(user.id).subscribe({
        next: () => {
          this.triggerToast(`User '${user.username}' deleted.`);
          this.loadUsers();
        },
        error: (err) => alert(err.error?.message || 'Error deleting user')
      });
    }
  }

  triggerToast(msg: string): void {
    this.successToast = msg;
    setTimeout(() => {
      this.successToast = '';
    }, 4000);
  }

  formatRole(role: string): string {
    return role.replace('ROLE_', '').replace('_', ' ');
  }
}
