import { LoginSuccess } from "../interfaces/login-success.interface";
import { LoginError } from "../interfaces/login-error.interface";
export type LoginResponse = LoginSuccess | LoginError;