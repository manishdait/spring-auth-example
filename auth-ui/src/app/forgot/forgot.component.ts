import { Component } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AlertService } from '../alert/service/alert.service';
import { AuthService } from '../auth.service';
import { AuthRequest } from '../auth.types';
import { AlertInterface } from '../alert/types/alert.interface';
import { AlertType } from '../alert/types/alertType.enum';

@Component({
  selector: 'app-forgot',
  standalone: true,
  imports: [RouterLink, FormsModule, ReactiveFormsModule],
  templateUrl: './forgot.component.html',
  styleUrl: './forgot.component.css'
})

export class ForgotComponent {
  userId: FormControl;
  submitted: boolean = false;

  constructor (private alertService: AlertService, private authService: AuthService) {
    this.userId = new FormControl('', Validators.required)
  }


  onSubmit() {
    this.submitted = true;
    var userId: string = this.userId.value;
    
    this.authService.forgot(userId).subscribe(
      (res) => {
        var alert: AlertInterface = {
          type: AlertType.info,
          message: 'Password reset link sent to your email.'
        }
        this.alertService.setAlert(alert);
      }, (err) => {
        console.error(err.error)
        var alert: AlertInterface = {
          type: AlertType.error,
          message: err.error.message
        }
        this.alertService.setAlert(alert);
      }
    );
  }
}
