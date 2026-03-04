import {
  ExceptionFilter,
  Catch,
  ArgumentsHost,
  HttpStatus,
  Logger,
} from '@nestjs/common';
import { Response } from 'express';
import {
  DomainException,
  InvalidOrderStateException,
  OrderNotFoundException,
  InvalidItemException,
  CurrencyMismatchException,
} from '../../../domain/exception';

@Catch(DomainException)
export class DomainExceptionFilter implements ExceptionFilter {
  private readonly logger = new Logger(DomainExceptionFilter.name);

  catch(exception: DomainException, host: ArgumentsHost): void {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    const status = this.mapToHttpStatus(exception);
    this.logger.warn(`Domain exception: ${exception.message} -> ${status}`);
    response.status(status).json({
      statusCode: status,
      message: exception.message,
      error: exception.name,
    });
  }

  private mapToHttpStatus(exception: DomainException): number {
    if (exception instanceof OrderNotFoundException) return HttpStatus.NOT_FOUND;
    if (exception instanceof InvalidOrderStateException) return HttpStatus.CONFLICT;
    if (exception instanceof InvalidItemException) return HttpStatus.BAD_REQUEST;
    if (exception instanceof CurrencyMismatchException) return HttpStatus.BAD_REQUEST;
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
