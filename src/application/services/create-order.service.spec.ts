import { Test, TestingModule } from '@nestjs/testing';
import { CreateOrderService } from './create-order.service';
import { ORDER_REPOSITORY, IOrderRepository } from '../ports/order-repository.port';
import { Order } from '../../domain/entities/order';
import { OrderItem } from '../../domain/entities/order-item';
import { createOrderId } from '../../domain/order-id';
import { Money } from '../../domain/value-objects/money';
import { InvalidItemException } from '../../domain/exception';

describe('CreateOrderService', () => {
  let service: CreateOrderService;
  let orderRepository: jest.Mocked<IOrderRepository>;

  const mockSave = jest.fn().mockResolvedValue(undefined);

  beforeEach(async () => {
    mockSave.mockClear();
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        CreateOrderService,
        {
          provide: ORDER_REPOSITORY,
          useValue: { save: mockSave, findById: jest.fn() },
        },
      ],
    }).compile();

    service = module.get<CreateOrderService>(CreateOrderService);
    orderRepository = module.get(ORDER_REPOSITORY);
  });

  describe('execute', () => {
    it('creates order with valid items and saves it', async () => {
      const command = {
        customerId: 'customer-1',
        items: [
          { productId: 'product-1', quantity: 2, unitPrice: 10 },
          { productId: 'product-2', quantity: 1, unitPrice: 5 },
        ],
      };

      const result = await service.execute(command);

      expect(result).toBeInstanceOf(Order);
      expect(result.customerId).toBe('customer-1');
      expect(result.items).toHaveLength(2);
      expect(result.totalAmount.amount).toBe(25);
      expect(result.totalAmount.currency).toBe('USD');
      expect(mockSave).toHaveBeenCalledTimes(1);
      expect(mockSave).toHaveBeenCalledWith(result);
    });

    it('throws InvalidItemException when items array is empty', async () => {
      const command = { customerId: 'customer-1', items: [] };

      await expect(service.execute(command)).rejects.toThrow(InvalidItemException);
      await expect(service.execute(command)).rejects.toThrow(/at least one item/i);
      expect(mockSave).not.toHaveBeenCalled();
    });

    it('throws when items is null or undefined', async () => {
      await expect(
        service.execute({ customerId: 'c1', items: null as any }),
      ).rejects.toThrow();
      await expect(
        service.execute({ customerId: 'c1', items: undefined as any }),
      ).rejects.toThrow();
      expect(mockSave).not.toHaveBeenCalled();
    });

    it('generates new order id and item ids via uuid', async () => {
      const command = {
        customerId: 'c1',
        items: [{ productId: 'p1', quantity: 1, unitPrice: 10 }],
      };

      const result = await service.execute(command);

      expect(result.id).toBeDefined();
      expect(typeof result.id).toBe('string');
      expect(result.items[0].id).toBeDefined();
      expect(result.items[0].productId).toBe('p1');
    });
  });
});
