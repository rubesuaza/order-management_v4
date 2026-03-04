import { Money } from './money';
import { CurrencyMismatchException } from '../exception';

describe('Money', () => {
  describe('constructor', () => {
    it('creates Money with amount and default currency USD', () => {
      const money = new Money(10.5, 'USD');
      expect(money.amount).toBe(10.5);
      expect(money.currency).toBe('USD');
    });

    it('creates Money with explicit currency', () => {
      const money = new Money(20, 'EUR');
      expect(money.currency).toBe('EUR');
    });
  });

  describe('add', () => {
    it('adds two amounts in same currency', () => {
      const a = new Money(10, 'USD');
      const b = new Money(5, 'USD');
      const result = a.add(b);
      expect(result.amount).toBe(15);
      expect(result.currency).toBe('USD');
      expect(result).not.toBe(a);
      expect(result).not.toBe(b);
    });

    it('throws CurrencyMismatchException when currencies differ', () => {
      const a = new Money(10, 'USD');
      const b = new Money(5, 'EUR');
      expect(() => a.add(b)).toThrow(CurrencyMismatchException);
      expect(() => a.add(b)).toThrow(/currency|mismatch/i);
    });
  });

  describe('subtract', () => {
    it('subtracts two amounts in same currency', () => {
      const a = new Money(10, 'USD');
      const b = new Money(3, 'USD');
      const result = a.subtract(b);
      expect(result.amount).toBe(7);
      expect(result.currency).toBe('USD');
    });

    it('throws CurrencyMismatchException when currencies differ', () => {
      const a = new Money(10, 'USD');
      const b = new Money(3, 'GBP');
      expect(() => a.subtract(b)).toThrow(CurrencyMismatchException);
    });
  });

  describe('multiply', () => {
    it('multiplies amount by factor and returns new Money', () => {
      const money = new Money(10, 'USD');
      const result = money.multiply(2);
      expect(result.amount).toBe(20);
      expect(result.currency).toBe('USD');
      expect(result).not.toBe(money);
    });

    it('handles decimal factor', () => {
      const money = new Money(10, 'USD');
      const result = money.multiply(0.5);
      expect(result.amount).toBe(5);
    });
  });

  describe('immutability', () => {
    it('does not mutate original when calling add', () => {
      const original = new Money(10, 'USD');
      original.add(new Money(5, 'USD'));
      expect(original.amount).toBe(10);
    });
  });
});
