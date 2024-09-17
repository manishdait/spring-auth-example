import { HttpBackend, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthRequest, AuthResponse, ForgotPasswordRequest, SignUpResponse, TokenRequest } from './auth.types';
import { Observable, Subject } from 'rxjs';
import { LocalStorageService } from 'ngx-webstorage';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  readonly url: string = 'http://localhost:8080/spring-auth-api/v1/auth';
  private $refreshToken: Subject<boolean> = new Subject<boolean>();
  private $refreshTokenRecive: Subject<boolean> = new Subject<boolean>();

  http:HttpClient;
  constructor(private backend: HttpBackend, private localStorage: LocalStorageService) {
    this.http = new HttpClient(backend);
    
    this.$refreshToken.subscribe((event) => {
      this.refreshToken();
    });
  }

  setRefreshToken() {
    this.$refreshToken.next(true);
  }

  getRefreshToken(): Observable<boolean> {
    return this.$refreshToken.asObservable()
  }

  setRefreshTokenRecive() {
    this.$refreshTokenRecive.next(true);
  }

  getRefreshTokenRecive(): Observable<boolean> {
    return this.$refreshTokenRecive.asObservable()
  }

  signup(request: AuthRequest): Observable<SignUpResponse> {
    return this.http.post<SignUpResponse>(`${this.url}/signup`, request);
  }

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.url}/login`, request);
  }

  verify(userId: string, request: TokenRequest): Observable<void> {
    return this.http.post<void>(`${this.url}/verify/${userId}`, request);
  }

  resendToken(userId: string): Observable<void> {
    return this.http.post<void>(`${this.url}/verify/${userId}?update=true`, {});
  }

  forgot(userId: string): Observable<void> {
    return this.http.post<void>(`${this.url}/forgot?userId=${userId}`, {});
  } 

  resetPassword(request: ForgotPasswordRequest, userId: string, tokenId: string):Observable<void> {
    return this.http.patch<void>(`${this.url}/reset/${userId}?tokenId=${tokenId}`, request);
  }

  resendLink(userId: string): Observable<void> {
    return this.http.patch<void>(`${this.url}/reset/${userId}?tokenId=token&update=true`, {});
  }

  storeUser(response: AuthResponse) {
    this.localStorage.store('user', response.user);
    this.localStorage.store('authToken', response.authToken);
    this.localStorage.store('refreshToken', response.refreshToken);
  }

  authToken(): string {
    return this.localStorage.retrieve('authToken');
  }

  refreshToken() {
    var request:TokenRequest = {
      token: this.localStorage.retrieve('refreshToken')
    }
    
    var userId = this.localStorage.retrieve('user');
    this.http.post<AuthResponse>(`${this.url}/refresh/${userId}`, request).subscribe((res) => {
      this.storeUser(res);
      this.setRefreshTokenRecive();
    })
  }

  getUser() {
    return this.localStorage.retrieve('user');
  }

  isAuthenticated() {
    return this.authToken() != null && this.authToken() != '' && this.localStorage.retrieve('user') != null && this.localStorage.retrieve('user') != '';
  }
}
