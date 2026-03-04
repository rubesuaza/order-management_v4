import { DomainException } from './domain.exception';

/**
 * Thrown when an illegal order state transition is attempted
 * (e.g. cancelling a SHIPPED order, shipping a PENDING order).
 */
export class InvalidOrderStateException extends DomainException {
  constructor(message: string) {
    super(message);
  }
}
