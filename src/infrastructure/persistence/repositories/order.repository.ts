import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { DataSource, Repository } from 'typeorm';
import { IOrderRepository } from '../../../application/ports/order-repository.port';
import { Order } from '../../../domain/entities/order';
import { OrderId } from '../../../domain/order-id';
import { OrderEntity } from '../entities/order.entity';
import { OrderItemEntity } from '../entities/order-item.entity';
import { OrderMapper } from '../mappers/order.mapper';

@Injectable()
export class OrderRepository implements IOrderRepository {
  constructor(
    @InjectRepository(OrderEntity)
    private readonly orderRepo: Repository<OrderEntity>,
    @InjectRepository(OrderItemEntity)
    private readonly orderItemRepo: Repository<OrderItemEntity>,
    private readonly dataSource: DataSource,
  ) {}

  async save(order: Order): Promise<void> {
    const { order: orderEntity, items } = OrderMapper.toPersistence(order);
    await this.dataSource.transaction(async (manager) => {
      await manager.save(OrderEntity, orderEntity);
      await manager.save(OrderItemEntity, items);
    });
  }

  async findById(id: OrderId): Promise<Order | null> {
    const orderId = id as string;
    const entity = await this.orderRepo.findOne({
      where: { id: orderId },
      relations: { items: true },
    });
    if (!entity) return null;
    return OrderMapper.toDomainFromPersistence(entity);
  }
}
