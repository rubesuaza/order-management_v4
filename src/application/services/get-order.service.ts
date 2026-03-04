import { Inject, Injectable } from '@nestjs/common';
import { IOrderRepository, ORDER_REPOSITORY } from '../ports/order-repository.port';
import { Order } from '../../domain/entities/order';
import { createOrderId } from '../../domain/order-id';
import { OrderNotFoundException } from '../../domain/exception';

@Injectable()
export class GetOrderService {
  constructor(@Inject(ORDER_REPOSITORY) private readonly orderRepository: IOrderRepository) {}

  async execute(orderId: string): Promise<Order> {
    const order = await this.orderRepository.findById(createOrderId(orderId));
    if (!order) {
      throw new OrderNotFoundException(`Order with id ${orderId} not found`);
    }
    return order;
  }
}
