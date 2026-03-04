import { Test, TestingModule } from '@nestjs/testing';
import { PayOrderService } from './pay-order.service';
import { ORDER_REPOSITORY, IOrderRepository } from '../ports/order-repository.port';
import { Order } from '../../domain/entities/order';
import { OrderItem } from '../../domain/entities/order-item';
import { createOrderId } from '../../domain/order-id';
import { Money } from '../../domain/value-objects/money';
import { OrderStatus } from '../../domain/order-status.enum';
import { OrderNotFoundException } from '../../domain/exception';

describe('PayOrderService', () => {
  let service: PayOrderService;
  let orderRepository: jest.Mocked<IOrderRepository>;

  const orderIdStr = '550e8400-e29b-41d4-a716-446655440010';
  const orderId = createOrderId(orderIdStr);

  function createPendingOrder(): Order {
    const items = [
      OrderItem.create('item-1', 'product-1', 1, new Money(10, 'USD')),
    ];
    return Order.create(orderId, 'customer-1', items);
  }

  beforeEach(async () => {
    const mockSave = jest.fn().mockResolvedValue(undefined);
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        PayOrderService,
        {
          provide: ORDER_REPOSITORY,
          useValue: { save: mockSave, findById: jest.fn() },
        },
      ],
    }).compile();

    service = module.get<PayOrderService>(PayOrderService);
    orderRepository = module.get(ORDER_REPOSITORY);
  });

  describe('execute', () => {
    it('marks order as PAID and saves it', async () => {
      const pendingOrder = createPendingOrder();
      (orderRepository.findById as jest.Mock).mockResolvedValue(pendingOrder);
      const mockSave = orderRepository.save as jest.Mock;

      const result = await service.execute(orderIdStr);

      expect(result.id).toBe(orderIdStr);
      expect(result.status).toBe(OrderStatus.PAID);
      expect(mockSave).toHaveBeenCalledTimes(1);
      const savedOrder = mockSave.mock.calls[0][0];
      expect(savedOrder.status).toBe(OrderStatus.PAID);
    });

    it('throws OrderNotFoundException when order does not exist', async () => {
      (orderRepository.findById as jest.Mock).mockResolvedValue(null);

      await expect(service.execute(orderIdStr)).rejects.toThrow(
        OrderNotFoundException,
      );
      await expect(service.execute(orderIdStr)).rejects.toThrow(
        /not found|Order with id/,
      );
      expect(orderRepository.save).not.toHaveBeenCalled();
    });

    it('throws when order is not in PENDING (e.g. already PAID)', async () => {
      const paidOrder = createPendingOrder().markAsPaid();
      (orderRepository.findById as jest.Mock).mockResolvedValue(paidOrder);

      await expect(service.execute(orderIdStr)).rejects.toThrow();
      expect(orderRepository.save).not.toHaveBeenCalled();
    });
  });
});
