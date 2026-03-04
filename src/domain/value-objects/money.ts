import { CurrencyMismatchException } from '../exception';

const DEFAULT_CURRENCY = 'USD';
const DECIMAL_PLACES = 2;

function round(amount: number): number {
  return Math.round(amount * 100) / 100;
}

/**
 * Immutable value object for monetary amounts.
 * All operations return new instances. Uses same-currency rule.
 */
export class Money {
  readonly amount: number;
  readonly currency: string;

  constructor(amount: number, currency: string = DEFAULT_CURRENCY) {
    this.amount = round(amount);
    this.currency = currency;
    Object.freeze(this);
  }

  add(other: Money): Money {
    if (this.currency !== other.currency) {
      throw new CurrencyMismatchException(
        `Cannot add amounts with different currencies: ${this.currency} and ${other.currency}`,
      );
    }
    return new Money(this.amount + other.amount, this.currency);
  }

  subtract(other: Money): Money {
    if (this.currency !== other.currency) {
      throw new CurrencyMismatchException(
        `Cannot subtract amounts with different currencies: ${this.currency} and ${other.currency}`,
      );
    }
    return new Money(this.amount - other.amount, this.currency);
  }

  multiply(factor: number): Money {
    return new Money(round(this.amount * factor), this.currency);
  }
}
