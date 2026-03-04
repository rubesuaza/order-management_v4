import { Test, TestingModule } from '@nestjs/testing';
import { GetOrderService } from './get-order.service';
import { ORDER_REPOSITORY, IOrderRepository } from '../ports/order-repository.port';
import { Order } from '../../domain/entities/order';
import { OrderItem } from '../../domain/entities/order-item';
import { createOrderId } from '../../domain/order-id';
import { Money } from '../../domain/value-objects/money';
import { OrderNotFoundException } from '../../domain/exception';

describe('GetOrderService', () => {
  let service: GetOrderService;
  let orderRepository: jest.Mocked<IOrderRepository>;

  const orderIdStr = '550e8400-e29b-41d4-a716-446655440010';
  const orderId = createOrderId(orderIdStr);

  function createOrder(): Order {
    const items = [
      OrderItem.create('item-1', 'product-1', 2, new Money(10, 'USD')),
    ];
    return Order.create(orderId, 'customer-1', items);
  }

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        GetOrderService,
        {
          provide: ORDER_REPOSITORY,
          useValue: { save: jest.fn(), findById: jest.fn() },
        },
      ],
    }).compile();

    service = module.get<GetOrderService>(GetOrderService);
    orderRepository = module.get(ORDER_REPOSITORY);
  });

  describe('execute', () => {
    it('returns order when found', async () => {
      const order = createOrder();
      (orderRepository.findById as jest.Mock).mockResolvedValue(order);

      const result = await service.execute(orderIdStr);

      expect(result).toBe(order);
      expect(result.id).toBe(orderId);
      expect(result.customerId).toBe('customer-1');
      expect(orderRepository.findById).toHaveBeenCalledWith(orderId);
    });

    it('throws OrderNotFoundException when order does not exist', async () => {
      (orderRepository.findById as jest.Mock).mockResolvedValue(null);

      await expect(service.execute(orderIdStr)).rejects.toThrow(
        OrderNotFoundException,
      );
      await expect(service.execute(orderIdStr)).rejects.toThrow(
        /not found|Order with id/,
      );
    });
  });
});
