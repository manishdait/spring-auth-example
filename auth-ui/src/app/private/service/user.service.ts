import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { User } from "../types/user.types";

@Injectable({
  providedIn: "root"
})
export class UserService {
  readonly url: string = 'http://localhost:8080/spring-auth-api/v1/private';

  constructor (private http: HttpClient) {}

  getUser(userId: string): Observable<User> {
    return this.http.get<User>(`${this.url}/${userId}`);
  }
}