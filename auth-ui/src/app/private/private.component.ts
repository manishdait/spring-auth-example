import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { UserService } from './service/user.service';
import { User } from './types/user.types';

@Component({
  selector: 'app-private',
  standalone: true,
  imports: [],
  templateUrl: './private.component.html',
  styleUrl: './private.component.css'
})
export class PrivateComponent implements OnInit {
  user?: User;
  userId?: string
  constructor (private userService: UserService, private authService: AuthService) {
    this.userId = authService.getUser();
    authService.getRefreshTokenRecive().subscribe(
      (event) => {
        this.getUser();
      }
    );
  }

  ngOnInit(): void {
    this.getUser()
  }

  getUser() {
    if (this.userId){
      this.userService.getUser(this.userId).subscribe(
        (res) => {
          this.user = res;
        }
      );
    }
  }
}
