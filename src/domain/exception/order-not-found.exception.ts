import { DomainException } from './domain.exception';

/**
 * Thrown when an order is requested by ID but does not exist.
 */
export class OrderNotFoundException extends DomainException {
  constructor(message: string) {
    super(message);
  }
}
