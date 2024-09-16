import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthRequest, AuthResponse, ForgotPasswordRequest, SignUpResponse, TokenRequest } from './auth.types';
import { Observable } from 'rxjs';
import { LocalStorageService } from 'ngx-webstorage';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  readonly url: string = 'http://localhost:8080/spring-auth-api/v1/auth';

  constructor(private http:HttpClient, private localStorage: LocalStorageService) {}

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

  refreshToken(): string {
    return this.localStorage.retrieve('refreshToken');
  }
}
