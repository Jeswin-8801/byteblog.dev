import { SignUpError } from '../interfaces/sign-up-error.interface';
import { SignUpSuccess } from '../interfaces/sign-up-success.interface';

export type SignUpResponse = SignUpSuccess | SignUpError;
