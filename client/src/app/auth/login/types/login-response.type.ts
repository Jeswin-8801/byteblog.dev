import { LoginSuccessDto } from '../../../models/dtos/auth/login-success-dto';
import { LoginError } from '../interfaces/login-error.interface';
export type LoginResponse = LoginSuccessDto | LoginError;
