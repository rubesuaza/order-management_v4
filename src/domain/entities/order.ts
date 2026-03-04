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

  /**
   * Reconstructs an Order from persistence (no business validations).
   * Used by infrastructure mappers when loading from DB.
   */
  static reconstitute(
    id: OrderId,
    status: OrderStatus,
    createdAt: Date,
    items: ReadonlyArray<OrderItem>,
    customerId: string,
    totalAmount: Money,
  ): Order {
    return new Order(id, status, createdAt, items, customerId, totalAmount);
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
    const total = items
      .slice(1)
      .reduce(
        (acc, item) => acc.add(item.getLineTotal()),
        items[0].getLineTotal(),
      );
    return new Order(
      id,
      OrderStatus.PENDING,
      new Date(),
      items,
      customerId,
      total,
    );
  }

  private meetsMinimumOrderAmount(): boolean {
    return this.totalAmount.amount >= MINIMUM_ORDER_AMOUNT_USD;
  }

  markAsPaid(): Order {
    if (this.status !== OrderStatus.PENDING) {
      throw new InvalidOrderStateException(
        'Order can only be marked as PAID when in PENDING status',
      );
    }
    if (!this.meetsMinimumOrderAmount()) {
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

  private isShippedOrDelivered(): boolean {
    return (
      this.status === OrderStatus.SHIPPED || this.status === OrderStatus.DELIVERED
    );
  }

  private isAlreadyCancelled(): boolean {
    return this.status === OrderStatus.CANCELLED;
  }

  cancel(): Order {
    if (this.isShippedOrDelivered()) {
      throw new InvalidOrderStateException(
        'A SHIPPED or DELIVERED order cannot be cancelled',
      );
    }
    if (this.isAlreadyCancelled()) {
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
