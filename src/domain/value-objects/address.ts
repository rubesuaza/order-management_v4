/**
 * Immutable value object for address.
 */
export class Address {
  readonly street: string;
  readonly city: string;
  readonly zipCode: string;
  readonly country: string;

  constructor(street: string, city: string, zipCode: string, country: string) {
    this.street = street;
    this.city = city;
    this.zipCode = zipCode;
    this.country = country;
    Object.freeze(this);
  }
}
