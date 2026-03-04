import { DomainException } from './domain.exception';

/**
 * Thrown when an OrderItem has invalid data
 * (e.g. quantity <= 0, negative unit price).
 */
export class InvalidItemException extends DomainException {
  constructor(message: string) {
    super(message);
  }
}
