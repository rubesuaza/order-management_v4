import { Money } from '../value-objects';
import { InvalidItemException } from '../exception';

/**
 * Domain entity: line item of an order.
 * Identity: id (UUID). Invariants: quantity > 0, unitPrice.amount >= 0.
 */
export class OrderItem {
  readonly id: string;
  readonly productId: string;
  readonly quantity: number;
  readonly unitPrice: Money;

  private constructor(
    id: string,
    productId: string,
    quantity: number,
    unitPrice: Money,
  ) {
    this.id = id;
    this.productId = productId;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
  }

  static create(
    id: string,
    productId: string,
    quantity: number,
    unitPrice: Money,
  ): OrderItem {
    if (quantity <= 0) {
      throw new InvalidItemException(
        'OrderItem quantity must be strictly greater than zero',
      );
    }
    if (unitPrice.amount < 0) {
      throw new InvalidItemException(
        'OrderItem unit price amount cannot be negative',
      );
    }
    return new OrderItem(id, productId, quantity, unitPrice);
  }

  getLineTotal(): Money {
    return this.unitPrice.multiply(this.quantity);
  }
}
