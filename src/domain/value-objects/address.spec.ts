import { Address } from './address';

describe('Address', () => {
  it('holds street, city, zipCode and country', () => {
    const address = new Address('123 Main St', 'New York', '10001', 'US');
    expect(address.street).toBe('123 Main St');
    expect(address.city).toBe('New York');
    expect(address.zipCode).toBe('10001');
    expect(address.country).toBe('US');
  });

  it('is immutable (frozen)', () => {
    const address = new Address('A', 'B', 'C', 'D');
    expect(Object.isFrozen(address)).toBe(true);
  });
});
