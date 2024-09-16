import { Component } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';
import { AlertService } from '../alert/service/alert.service';
import { ForgotPasswordRequest } from '../auth.types';
import { AlertInterface } from '../alert/types/alert.interface';
import { AlertType } from '../alert/types/alertType.enum';

@Component({
  selector: 'app-reset',
  standalone: true,
  imports: [RouterLink, FormsModule, ReactiveFormsModule],
  templateUrl: './reset.component.html',
  styleUrl: './reset.component.css'
})

export class ResetComponent {
  password: FormControl;
  userId?: string;
  tokenId?: string;

  submitted: boolean = false;

  constructor (private authService: AuthService, private alertService: AlertService, private router: Router, private route: ActivatedRoute) {
    this.password = new FormControl('', Validators.required);
    this.userId = route.snapshot.paramMap.get('userId')?.toString();
    this.tokenId = route.snapshot.queryParamMap.get('tokenId')?.toString();
  }

  resendLink() {
    if (this.userId) {
      this.authService.resendLink(this.userId).subscribe(
        (res) => {
          var alert: AlertInterface = {
            type: AlertType.info,
            message: "Password reset link sent to your email."
          }
          this.alertService.setAlert(alert);
        }, (err) => {
          console.error(err.error);
          var alert: AlertInterface = {
            type: AlertType.error,
            message: err.error.message
          }
          this.alertService.setAlert(alert);
        }
      );
    }
  }

  onSubmit() {
    this.submitted = true;
    var request: ForgotPasswordRequest = {
      password: this.password.value
    }

    if (this.userId && this.tokenId) {
      this.authService.resetPassword(request, this.userId, this.tokenId).subscribe(
        (res) => {
          var alert: AlertInterface = {
            type: AlertType.sucess,
            message: "Passwrod Reset Sucessfully."
          }
          this.alertService.setAlert(alert);
          this.router.navigate(['/login']);
        }, (err) => {
          console.error(err.error);
          if (err.error.error === 'Token Expired') {
            var alert: AlertInterface = {
              type: AlertType.warning,
              message: err.error.message
            }
            this.alertService.setAlert(alert);
          } else {
            var alert: AlertInterface = {
              type: AlertType.error,
              message: err.error.message
            }
            this.alertService.setAlert(alert);
          }
        }
      );
    }
  }
}
