import { Order } from '../../domain/entities/order';
import { OrderId } from '../../domain/order-id';

/** Injection token for the order repository port (NestJS DI). */
export const ORDER_REPOSITORY = Symbol('ORDER_REPOSITORY');

/**
 * Port for persisting and retrieving Order aggregates.
 * Implemented by the infrastructure layer.
 */
export interface IOrderRepository {
  save(order: Order): Promise<void>;
  findById(id: OrderId): Promise<Order | null>;
}
