import { OrderMapper } from './order.mapper';
import { Order } from '../../../domain/entities/order';
import { OrderItem } from '../../../domain/entities/order-item';
import { createOrderId } from '../../../domain/order-id';
import { Money } from '../../../domain/value-objects/money';
import { OrderStatus } from '../../../domain/order-status.enum';
import { OrderEntity } from '../entities/order.entity';
import { OrderItemEntity } from '../entities/order-item.entity';

describe('OrderMapper', () => {
  describe('toDomainFromPersistence', () => {
    it('maps OrderEntity with items to domain Order', () => {
      const createdAt = new Date('2025-01-15T10:00:00Z');
      const entity = new OrderEntity();
      entity.id = 'order-uuid-1';
      entity.customerId = 'customer-uuid-1';
      entity.status = OrderStatus.PENDING;
      entity.totalAmount = '25.00';
      entity.currency = 'USD';
      entity.createdAt = createdAt;
      entity.items = [
        Object.assign(new OrderItemEntity(), {
          id: 'item-1',
          orderId: 'order-uuid-1',
          productId: 'product-1',
          quantity: 2,
          unitPrice: '10.00',
        }),
        Object.assign(new OrderItemEntity(), {
          id: 'item-2',
          orderId: 'order-uuid-1',
          productId: 'product-2',
          quantity: 1,
          unitPrice: '5.00',
        }),
      ];

      const order = OrderMapper.toDomainFromPersistence(entity);

      expect(order).toBeInstanceOf(Order);
      expect(order.id).toBe('order-uuid-1');
      expect(order.customerId).toBe('customer-uuid-1');
      expect(order.status).toBe(OrderStatus.PENDING);
      expect(order.totalAmount.amount).toBe(25);
      expect(order.totalAmount.currency).toBe('USD');
      expect(order.createdAt).toEqual(createdAt);
      expect(order.items).toHaveLength(2);
      expect(order.items[0].id).toBe('item-1');
      expect(order.items[0].productId).toBe('product-1');
      expect(order.items[0].quantity).toBe(2);
      expect(order.items[0].unitPrice.amount).toBe(10);
      expect(order.items[1].unitPrice.amount).toBe(5);
    });

    it('handles entity with empty items', () => {
      const entity = new OrderEntity();
      entity.id = 'order-1';
      entity.customerId = 'cust-1';
      entity.status = OrderStatus.PENDING;
      entity.totalAmount = '0.00';
      entity.currency = 'USD';
      entity.createdAt = new Date();
      entity.items = [];

      const order = OrderMapper.toDomainFromPersistence(entity);

      expect(order.items).toHaveLength(0);
      expect(order.totalAmount.amount).toBe(0);
    });
  });

  describe('toPersistence', () => {
    it('maps domain Order to OrderEntity and OrderItemEntity list', () => {
      const orderId = createOrderId('order-uuid-1');
      const items = [
        OrderItem.create('item-1', 'product-1', 2, new Money(10, 'USD')),
        OrderItem.create('item-2', 'product-2', 1, new Money(5, 'USD')),
      ];
      const createdAt = new Date('2025-01-15T10:00:00Z');
      const order = Order.reconstitute(
        orderId,
        OrderStatus.PENDING,
        createdAt,
        items,
        'customer-uuid-1',
        new Money(25, 'USD'),
      );

      const { order: orderEntity, items: itemEntities } =
        OrderMapper.toPersistence(order);

      expect(orderEntity.id).toBe('order-uuid-1');
      expect(orderEntity.customerId).toBe('customer-uuid-1');
      expect(orderEntity.status).toBe(OrderStatus.PENDING);
      expect(orderEntity.totalAmount).toBe('25.00');
      expect(orderEntity.currency).toBe('USD');
      expect(orderEntity.createdAt).toEqual(createdAt);
      expect(itemEntities).toHaveLength(2);
      expect(itemEntities[0].id).toBe('item-1');
      expect(itemEntities[0].orderId).toBe('order-uuid-1');
      expect(itemEntities[0].productId).toBe('product-1');
      expect(itemEntities[0].quantity).toBe(2);
      expect(itemEntities[0].unitPrice).toBe('10.00');
      expect(itemEntities[1].unitPrice).toBe('5.00');
    });
  });
});
