import { DomainException } from './domain.exception';

/**
 * Thrown when a monetary operation involves different currencies.
 */
export class CurrencyMismatchException extends DomainException {
  constructor(message: string) {
    super(message);
  }
}
