import { Body, Controller, Get, Param, Post, UseFilters } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse } from '@nestjs/swagger';
import { CreateOrderDto } from '../dto/create-order.dto';
import {
  CreateOrderResponseDto,
  OrderDetailResponseDto,
  PayOrderResponseDto,
} from '../dto/order-response.dto';
import { DomainExceptionFilter } from '../filters/domain-exception.filter';
import { CreateOrderService } from '../../../application/services/create-order.service';
import { GetOrderService } from '../../../application/services/get-order.service';
import { PayOrderService } from '../../../application/services/pay-order.service';

@ApiTags('orders')
@Controller('v1/orders')
@UseFilters(DomainExceptionFilter)
export class OrdersController {
  constructor(
    private readonly createOrderService: CreateOrderService,
    private readonly getOrderService: GetOrderService,
    private readonly payOrderService: PayOrderService,
  ) {}

  @Post()
  @ApiOperation({ summary: 'Create a new order' })
  @ApiResponse({ status: 201, description: 'Order created', type: CreateOrderResponseDto })
  @ApiResponse({ status: 400, description: 'Validation failed or invalid items' })
  async create(@Body() dto: CreateOrderDto): Promise<CreateOrderResponseDto> {
    const order = await this.createOrderService.execute({
      customerId: dto.customerId,
      items: dto.items.map((i) => ({
        productId: i.productId,
        quantity: i.quantity,
        unitPrice: i.unitPrice,
      })),
    });
    return {
      orderId: order.id as string,
      status: order.status,
      totalAmount: order.totalAmount.amount,
      createdAt: order.createdAt.toISOString(),
    };
  }

  @Get(':orderId')
  @ApiOperation({ summary: 'Get order details' })
  @ApiResponse({ status: 200, description: 'Order details', type: OrderDetailResponseDto })
  @ApiResponse({ status: 404, description: 'Order not found' })
  async getById(@Param('orderId') orderId: string): Promise<OrderDetailResponseDto> {
    const order = await this.getOrderService.execute(orderId);
    return {
      orderId: order.id as string,
      customerId: order.customerId,
      status: order.status,
      items: order.items.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
        unitPrice: item.unitPrice.amount,
      })),
      totalAmount: order.totalAmount.amount,
      currency: order.totalAmount.currency,
    };
  }

  @Post(':orderId/pay')
  @ApiOperation({ summary: 'Pay an order' })
  @ApiResponse({ status: 200, description: 'Order paid', type: PayOrderResponseDto })
  @ApiResponse({ status: 404, description: 'Order not found' })
  @ApiResponse({ status: 409, description: 'Order already paid or cancelled' })
  async pay(@Param('orderId') orderId: string): Promise<PayOrderResponseDto> {
    const result = await this.payOrderService.execute(orderId);
    return { orderId: result.id, status: result.status };
  }
}
