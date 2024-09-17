import { Routes } from '@angular/router';
import { SignUpComponent } from './sign-up/sign-up.component';
import { LoginComponent } from './login/login.component';
import { VerifyComponent } from './verify/verify.component';
import { ForgotComponent } from './forgot/forgot.component';
import { ResetComponent } from './reset/reset.component';
import { PrivateComponent } from './private/private.component';
import { privateGuard } from './private/guard/private.guard';

export const routes: Routes = [
  {path: "signup", component: SignUpComponent},
  {path: "login", component: LoginComponent},
  {path: "verify/:userId", component: VerifyComponent},
  {path: "forgot", component: ForgotComponent},
  {path: "reset/:userId", component: ResetComponent},
  {path: "private", component: PrivateComponent, canActivate: [privateGuard]},
  {path: "**", redirectTo: "private"}
];
