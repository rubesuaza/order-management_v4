/**
 * Abstract base class for all domain exceptions.
 * Ensures correct prototype chain for instanceof checks in TypeScript.
 */
export abstract class DomainException extends Error {
  constructor(message: string) {
    super(message);
    this.name = this.constructor.name;
    Object.setPrototypeOf(this, new.target.prototype);
  }
}
