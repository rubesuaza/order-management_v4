import { Order } from '../../../domain/entities/order';
import { OrderItem } from '../../../domain/entities/order-item';
import { OrderStatus } from '../../../domain/order-status.enum';
import { createOrderId } from '../../../domain/order-id';
import { Money } from '../../../domain/value-objects/money';
import { OrderEntity } from '../entities/order.entity';
import { OrderItemEntity } from '../entities/order-item.entity';

export class OrderMapper {
  private static mapStatusFromPersistence(status: string): OrderStatus {
    return (OrderStatus[status as keyof typeof OrderStatus] ?? OrderStatus.PENDING) as OrderStatus;
  }

  /**
   * Reconstruct domain aggregate from persistence (e.g. after findById).
   */
  static toDomainFromPersistence(entity: OrderEntity): Order {
    const currency = entity.currency;
    const items: OrderItem[] = (entity.items ?? []).map((itemEntity) =>
      OrderItem.create(
        itemEntity.id,
        itemEntity.productId,
        itemEntity.quantity,
        new Money(parseFloat(itemEntity.unitPrice), currency),
      ),
    );
    const status = OrderMapper.mapStatusFromPersistence(entity.status);
    return Order.reconstitute(
      createOrderId(entity.id),
      status,
      entity.createdAt,
      items,
      entity.customerId,
      new Money(parseFloat(entity.totalAmount), currency),
    );
  }

  static toPersistence(order: Order): { order: OrderEntity; items: OrderItemEntity[] } {
    const orderEntity = new OrderEntity();
    orderEntity.id = order.id as string;
    orderEntity.customerId = order.customerId;
    orderEntity.status = order.status;
    orderEntity.totalAmount = order.totalAmount.amount.toFixed(2);
    orderEntity.currency = order.totalAmount.currency;
    orderEntity.createdAt = order.createdAt;

    const items = order.items.map((item) => {
      const itemEntity = new OrderItemEntity();
      itemEntity.id = item.id;
      itemEntity.orderId = order.id as string;
      itemEntity.productId = item.productId;
      itemEntity.quantity = item.quantity;
      itemEntity.unitPrice = item.unitPrice.amount.toFixed(2);
      return itemEntity;
    });

    return { order: orderEntity, items };
  }
}
