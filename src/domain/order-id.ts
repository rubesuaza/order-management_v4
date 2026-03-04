/**
 * Branded type for Order identity (UUID string).
 * Prevents mixing with other string IDs at type level.
 */
export type OrderId = string & { readonly __brand: 'OrderId' };

export function createOrderId(id: string): OrderId {
  return id as OrderId;
}
