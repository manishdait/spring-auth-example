import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthRequest } from '../auth.types';
import { AuthService } from '../auth.service';
import { AlertService } from '../alert/service/alert.service';
import { AlertInterface } from '../alert/types/alert.interface';
import { AlertType } from '../alert/types/alertType.enum';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [RouterLink, FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.css'
})

export class SignUpComponent {
  signupForm: FormGroup;
  submitted: boolean = false;

  constructor (private authService: AuthService, private router: Router, private alertService: AlertService) {
    this.signupForm = new FormGroup(
      {
        username: new FormControl('', Validators.required),
        password: new FormControl('', Validators.required),
        email: new FormControl('', Validators.required)
      }
    );
  }

  get formControl() {
    return this.signupForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    
    var request: AuthRequest = {
      username: this.signupForm.get('username')!.value,
      password: this.signupForm.get('password')!.value,
      email: this.signupForm.get('email')!.value
    }

    this.authService.signup(request).subscribe(
      (res) => {
        var url = `/verify/${res.user}` 
        var alert: AlertInterface = {
          type: AlertType.info,
          message: "Your verification code has been sent to your email."
        }
        this.alertService.setAlert(alert);
        this.router.navigate([url]);
      }, (err) => {
        var alert: AlertInterface = {
          type: AlertType.error,
          message: err.error.message
        };
        this.alertService.setAlert(alert);
      }
    );
  }
}
