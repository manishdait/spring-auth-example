import { AlertType } from "./alertType.enum";

export interface AlertInterface {
  type: AlertType,
  message: string,
}