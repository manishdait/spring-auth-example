import { Component } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AlertService } from '../alert/service/alert.service';
import { AuthService } from '../auth.service';
import { TokenRequest } from '../auth.types';
import { AlertInterface } from '../alert/types/alert.interface';
import { AlertType } from '../alert/types/alertType.enum';

@Component({
  selector: 'app-verify',
  standalone: true,
  imports: [RouterLink, FormsModule, ReactiveFormsModule],
  templateUrl: './verify.component.html',
  styleUrl: './verify.component.css'
})
export class VerifyComponent {
  verificationCode: FormControl;
  submitted: boolean = false;
  userId?: string | null;

  constructor (private router: Router, private authService:AuthService, private alertService: AlertService, private route: ActivatedRoute) {
    this.verificationCode = new FormControl('', Validators.required);

    this.userId = route.snapshot.paramMap.get('userId');
    console.log(this.userId);
    
  }

  resendToken() {
    if(this.userId){
      this.authService.resendToken(this.userId).subscribe(
        (res) => {
          var alert: AlertInterface = {
            type: AlertType.info,
            message: "Your verification code has been sent to your email."
          }
          this.alertService.setAlert(alert);
        }
      );
    }
  }

  onSubmit() {
    this.submitted = true;
    var request: TokenRequest = {
      token: this.verificationCode.value
    }

    if (this.userId) {
      this.authService.verify(this.userId, request).subscribe(
        (res) => {
          var alert: AlertInterface = {
            type: AlertType.sucess,
            message: "Your account has been activated"
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
