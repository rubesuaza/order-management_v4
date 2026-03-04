import { Inject, Injectable } from '@nestjs/common';
import { IOrderRepository, ORDER_REPOSITORY } from '../ports/order-repository.port';
import { createOrderId } from '../../domain/order-id';
import { OrderNotFoundException } from '../../domain/exception';

@Injectable()
export class PayOrderService {
  constructor(@Inject(ORDER_REPOSITORY) private readonly orderRepository: IOrderRepository) {}

  async execute(orderId: string): Promise<{ id: string; status: string }> {
    const order = await this.orderRepository.findById(createOrderId(orderId));
    if (!order) {
      throw new OrderNotFoundException(`Order with id ${orderId} not found`);
    }
    const paidOrder = order.markAsPaid();
    await this.orderRepository.save(paidOrder);
    return { id: paidOrder.id as string, status: paidOrder.status };
  }
}
