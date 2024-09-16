import { Component } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AlertService } from '../alert/service/alert.service';
import { AuthService } from '../auth.service';
import { AuthRequest } from '../auth.types';
import { AlertInterface } from '../alert/types/alert.interface';
import { AlertType } from '../alert/types/alertType.enum';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, FormsModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})

export class LoginComponent {
  loginForm:FormGroup;
  submitted: boolean = false;

  constructor(private router: Router, private authService:AuthService, private alertService: AlertService) {
    this.loginForm = new FormGroup({
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required)
    });
  }

  get formControl() {
    return this.loginForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    var request: AuthRequest = {
      username: this.loginForm.get('username')!.value,
      password: this.loginForm.get('password')!.value,
      email: ''
    }

    this.authService.login(request).subscribe(
      (res) => {
        this.authService.storeUser(res);
        var url = `/private/${res.user}`;
        this.router.navigate([url]);
      }, (err) => {
        var alert: AlertInterface = {
          type: AlertType.error,
          message: err.error.message
        }
        this.alertService.setAlert(alert);
      }
    );
  }
}
