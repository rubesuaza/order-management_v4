import { Order } from './order';
import { OrderItem } from './order-item';
import { Money } from '../value-objects';
import { OrderStatus } from '../order-status.enum';
import { createOrderId } from '../order-id';
import {
  InvalidOrderStateException,
  CurrencyMismatchException,
} from '../exception';

const customerId = '550e8400-e29b-41d4-a716-446655440000';
const orderId = createOrderId('550e8400-e29b-41d4-a716-446655440010');

function createItem(id: string, qty: number, unitPrice: number): OrderItem {
  return OrderItem.create(id, 'product-' + id, qty, new Money(unitPrice, 'USD'));
}

describe('Order', () => {
  describe('create', () => {
    it('creates order with at least one item and status PENDING', () => {
      const items = [createItem('item-1', 2, 5)];
      const order = Order.create(orderId, customerId, items);
      expect(order.id).toBe(orderId);
      expect(order.customerId).toBe(customerId);
      expect(order.status).toBe(OrderStatus.PENDING);
      expect(order.items).toHaveLength(1);
      expect(order.totalAmount.amount).toBe(10);
      expect(order.totalAmount.currency).toBe('USD');
      expect(order.createdAt).toBeInstanceOf(Date);
    });

    it('calculates totalAmount as sum of line totals', () => {
      const items = [
        createItem('item-1', 2, 10), // 20
        createItem('item-2', 1, 5),  // 5
      ];
      const order = Order.create(orderId, customerId, items);
      expect(order.totalAmount.amount).toBe(25);
    });

    it('throws when items array is empty', () => {
      expect(() => Order.create(orderId, customerId, [])).toThrow(
        /at least one|empty/i,
      );
    });

    it('throws when items have mixed currencies (total calculation)', () => {
      const itemUsd = OrderItem.create(
        'item-1',
        'p1',
        1,
        new Money(10, 'USD'),
      );
      const itemEur = OrderItem.create(
        'item-2',
        'p2',
        1,
        new Money(10, 'EUR'),
      );
      expect(() =>
        Order.create(orderId, customerId, [itemUsd, itemEur]),
      ).toThrow(CurrencyMismatchException);
    });
  });

  describe('markAsPaid', () => {
    it('transitions from PENDING to PAID when total >= 10 USD', () => {
      const items = [createItem('item-1', 1, 10)];
      const order = Order.create(orderId, customerId, items);
      const paid = order.markAsPaid();
      expect(paid.status).toBe(OrderStatus.PAID);
    });

    it('throws when total is less than 10 USD', () => {
      const items = [createItem('item-1', 1, 5)]; // total 5
      const order = Order.create(orderId, customerId, items);
      expect(() => order.markAsPaid()).toThrow(/minimum|10|order value/i);
    });

    it('allows exactly 10 USD', () => {
      const items = [createItem('item-1', 2, 5)]; // total 10
      const order = Order.create(orderId, customerId, items);
      const paid = order.markAsPaid();
      expect(paid.status).toBe(OrderStatus.PAID);
    });

    it('throws when order is not PENDING', () => {
      const items = [createItem('item-1', 1, 10)];
      const order = Order.create(orderId, customerId, items);
      const paid = order.markAsPaid();
      expect(() => paid.markAsPaid()).toThrow(InvalidOrderStateException);
    });
  });

  describe('ship', () => {
    it('transitions from PAID to SHIPPED', () => {
      const items = [createItem('item-1', 1, 10)];
      const order = Order.create(orderId, customerId, items).markAsPaid();
      const shipped = order.ship();
      expect(shipped.status).toBe(OrderStatus.SHIPPED);
    });

    it('throws when order is not PAID', () => {
      const items = [createItem('item-1', 1, 10)];
      const order = Order.create(orderId, customerId, items);
      expect(() => order.ship()).toThrow(InvalidOrderStateException);
      expect(() => order.ship()).toThrow(/PAID|ship/i);
    });
  });

  describe('cancel', () => {
    it('transitions from PENDING to CANCELLED', () => {
      const items = [createItem('item-1', 1, 10)];
      const order = Order.create(orderId, customerId, items);
      const cancelled = order.cancel();
      expect(cancelled.status).toBe(OrderStatus.CANCELLED);
    });

    it('transitions from PAID to CANCELLED', () => {
      const items = [createItem('item-1', 1, 10)];
      const order = Order.create(orderId, customerId, items).markAsPaid();
      const cancelled = order.cancel();
      expect(cancelled.status).toBe(OrderStatus.CANCELLED);
    });

    it('throws when order is SHIPPED', () => {
      const items = [createItem('item-1', 1, 10)];
      const order = Order
        .create(orderId, customerId, items)
        .markAsPaid()
        .ship();
      expect(() => order.cancel()).toThrow(InvalidOrderStateException);
      expect(() => order.cancel()).toThrow(/cannot.*cancel|SHIPPED/i);
    });

    it('throws when order is already CANCELLED', () => {
      const items = [createItem('item-1', 1, 10)];
      const order = Order.create(orderId, customerId, items).cancel();
      expect(() => order.cancel()).toThrow(InvalidOrderStateException);
    });
  });

  describe('immutability of transitions', () => {
    it('markAsPaid returns new Order instance', () => {
      const items = [createItem('item-1', 1, 10)];
      const order = Order.create(orderId, customerId, items);
      const paid = order.markAsPaid();
      expect(paid).not.toBe(order);
      expect(order.status).toBe(OrderStatus.PENDING);
    });
  });
});
