import { OrderItem } from './order-item';
import { Money } from '../value-objects';
import { InvalidItemException } from '../exception';

describe('OrderItem', () => {
  const validProductId = '550e8400-e29b-41d4-a716-446655440001';
  const validId = '550e8400-e29b-41d4-a716-446655440002';
  const validUnitPrice = new Money(10, 'USD');

  it('creates item with valid quantity and unit price', () => {
    const item = OrderItem.create(validId, validProductId, 2, validUnitPrice);
    expect(item.id).toBe(validId);
    expect(item.productId).toBe(validProductId);
    expect(item.quantity).toBe(2);
    expect(item.unitPrice).toBe(validUnitPrice);
  });

  it('throws InvalidItemException when quantity is zero', () => {
    expect(() =>
      OrderItem.create(validId, validProductId, 0, validUnitPrice),
    ).toThrow(InvalidItemException);
    expect(() =>
      OrderItem.create(validId, validProductId, 0, validUnitPrice),
    ).toThrow(/quantity|greater than zero/i);
  });

  it('throws InvalidItemException when quantity is negative', () => {
    expect(() =>
      OrderItem.create(validId, validProductId, -1, validUnitPrice),
    ).toThrow(InvalidItemException);
  });

  it('throws InvalidItemException when unit price amount is negative', () => {
    const negativePrice = new Money(-5, 'USD');
    expect(() =>
      OrderItem.create(validId, validProductId, 1, negativePrice),
    ).toThrow(InvalidItemException);
    expect(() =>
      OrderItem.create(validId, validProductId, 1, negativePrice),
    ).toThrow(/price|negative/i);
  });

  it('accepts zero as unit price amount (free item)', () => {
    const freePrice = new Money(0, 'USD');
    const item = OrderItem.create(validId, validProductId, 1, freePrice);
    expect(item.unitPrice.amount).toBe(0);
  });

  it('line total is unitPrice * quantity', () => {
    const item = OrderItem.create(validId, validProductId, 3, validUnitPrice);
    const lineTotal = item.getLineTotal();
    expect(lineTotal.amount).toBe(30);
    expect(lineTotal.currency).toBe('USD');
  });
});
