import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

// Importez les modules Material nécessaires
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatOptionModule,
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;

  constructor(private fb: FormBuilder, private router: Router) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phones: this.fb.array([]),
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.addPhone();
  }

  get phones() {
    return this.registerForm.get('phones') as FormArray;
  }

  addPhone() {
    const phoneGroup = this.fb.group({
      phoneNumber: ['', Validators.required],
      phoneType: ['Mobile', Validators.required]
    });
    this.phones.push(phoneGroup);
  }

  removePhone(index: number) {
    this.phones.removeAt(index);
  }

  getPhoneGroup(index: number) {
    return this.phones.at(index) as FormGroup;
  }

  register() {
    if (this.registerForm.valid) {
      // Logique d'inscription
      console.log(this.registerForm.value);
      this.router.navigate(['/login']);
    }
  }
}