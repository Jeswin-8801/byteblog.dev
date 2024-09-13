import { LoginSuccessDto } from '../../../models/dtos/login-success-dto';
import { LoginError } from '../interfaces/login-error.interface';
export type LoginResponse = LoginSuccessDto | LoginError;
