import { Money } from '../value-objects';
import { OrderItem } from './order-item';
import { OrderStatus } from '../order-status.enum';
import { OrderId } from '../order-id';
import { InvalidOrderStateException, CurrencyMismatchException } from '../exception';

const MINIMUM_ORDER_AMOUNT_USD = 10;

/**
 * Aggregate root: Order.
 * Identity: OrderId. Invariants: at least one item, total = sum(line totals), same currency.
 * State transitions: PENDING -> PAID (min 10 USD), PAID -> SHIPPED; PENDING|PAID -> CANCELLED.
 */
export class Order {
  readonly id: OrderId;
  readonly status: OrderStatus;
  readonly createdAt: Date;
  readonly items: ReadonlyArray<OrderItem>;
  readonly customerId: string;
  readonly totalAmount: Money;

  private constructor(
    id: OrderId,
    status: OrderStatus,
    createdAt: Date,
    items: ReadonlyArray<OrderItem>,
    customerId: string,
    totalAmount: Money,
  ) {
    this.id = id;
    this.status = status;
    this.createdAt = createdAt;
    this.items = items;
    this.customerId = customerId;
    this.totalAmount = totalAmount;
  }

  static create(
    id: OrderId,
    customerId: string,
    items: ReadonlyArray<OrderItem>,
  ): Order {
    if (!items.length) {
      throw new InvalidOrderStateException(
        'An Order must have at least one OrderItem to be created or finalized',
      );
    }
    let total: Money = items[0].getLineTotal();
    for (let i = 1; i < items.length; i++) {
      total = total.add(items[i].getLineTotal());
    }
    return new Order(
      id,
      OrderStatus.PENDING,
      new Date(),
      items,
      customerId,
      total,
    );
  }

  markAsPaid(): Order {
    if (this.status !== OrderStatus.PENDING) {
      throw new InvalidOrderStateException(
        'Order can only be marked as PAID when in PENDING status',
      );
    }
    if (this.totalAmount.amount < MINIMUM_ORDER_AMOUNT_USD) {
      throw new InvalidOrderStateException(
        'Order cannot be placed (status PAID): minimum order value is 10.00 USD',
      );
    }
    return new Order(
      this.id,
      OrderStatus.PAID,
      this.createdAt,
      this.items,
      this.customerId,
      this.totalAmount,
    );
  }

  ship(): Order {
    if (this.status !== OrderStatus.PAID) {
      throw new InvalidOrderStateException(
        'Order can only be SHIPPED when in PAID status',
      );
    }
    return new Order(
      this.id,
      OrderStatus.SHIPPED,
      this.createdAt,
      this.items,
      this.customerId,
      this.totalAmount,
    );
  }

  cancel(): Order {
    if (this.status === OrderStatus.SHIPPED || this.status === OrderStatus.DELIVERED) {
      throw new InvalidOrderStateException(
        'A SHIPPED or DELIVERED order cannot be cancelled',
      );
    }
    if (this.status === OrderStatus.CANCELLED) {
      throw new InvalidOrderStateException(
        'Order is already CANCELLED',
      );
    }
    return new Order(
      this.id,
      OrderStatus.CANCELLED,
      this.createdAt,
      this.items,
      this.customerId,
      this.totalAmount,
    );
  }
}
