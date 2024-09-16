import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlertInterface } from './types/alert.interface';
import { AlertType } from './types/alertType.enum';
import { AlertService } from './service/alert.service';

@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alert.component.html',
  styleUrl: './alert.component.css'
})
export class AlertComponent {
  alert?: AlertInterface;
  timeoutId?: number;

  constructor(private alertService: AlertService) {}

  ngOnInit() {
    this.alertService.getAlert().subscribe(
      alert => {
        this.alert = alert;
        this.resetTimer();
      }
    )
  }

  resetTimer():void {
    if (this.timeoutId) {
      window.clearTimeout(this.timeoutId);
    }

    this.timeoutId = window.setTimeout(() => {
      this.alert = undefined;
    }, 3000);
  }

}
