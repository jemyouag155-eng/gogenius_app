import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Login } from './login.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private apiUrl = `${environment.API_URL}/auth/login`; // À adapter selon votre API

  constructor(private http: HttpClient) {}

  /**
   * Envoie la requête de connexion avec les données conformes au tableau
   * @param credentials Objet Login contenant login, password (crypté) et keepSession (MD5)
   */
  login(credentials: Login): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<any>(this.apiUrl, credentials, { headers });
  }
}