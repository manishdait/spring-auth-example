import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideNgxWebstorage, withLocalStorage } from 'ngx-webstorage';
import { tokenInterceptor } from './token.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes),
    provideNgxWebstorage(withLocalStorage()),
    provideHttpClient(withInterceptors([tokenInterceptor]))
  ]
};
