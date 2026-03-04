import { Inject, Injectable } from '@nestjs/common';
import { v4 as uuidv4 } from 'uuid';
import { IOrderRepository, ORDER_REPOSITORY } from '../ports/order-repository.port';
import { Order } from '../../domain/entities/order';
import { OrderItem } from '../../domain/entities/order-item';
import { createOrderId } from '../../domain/order-id';
import { Money } from '../../domain/value-objects/money';
import { InvalidItemException } from '../../domain/exception';

export interface CreateOrderCommand {
  customerId: string;
  items: { productId: string; quantity: number; unitPrice: number }[];
}

@Injectable()
export class CreateOrderService {
  constructor(@Inject(ORDER_REPOSITORY) private readonly orderRepository: IOrderRepository) {}

  async execute(command: CreateOrderCommand): Promise<Order> {
    if (!command.items?.length) {
      throw new InvalidItemException('Order must have at least one item');
    }
    const orderId = createOrderId(uuidv4());
    const items = command.items.map((item) =>
      OrderItem.create(
        uuidv4(),
        item.productId,
        item.quantity,
        new Money(item.unitPrice),
      ),
    );
    const order = Order.create(orderId, command.customerId, items);
    await this.orderRepository.save(order);
    return order;
  }
}
