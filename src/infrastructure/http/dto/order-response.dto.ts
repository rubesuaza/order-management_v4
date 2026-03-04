import { ApiProperty } from '@nestjs/swagger';

export class OrderItemResponseDto {
  @ApiProperty()
  productId!: string;

  @ApiProperty()
  quantity!: number;

  @ApiProperty()
  unitPrice!: number;
}

export class OrderDetailResponseDto {
  @ApiProperty()
  orderId!: string;

  @ApiProperty()
  customerId!: string;

  @ApiProperty({ example: 'PAID' })
  status!: string;

  @ApiProperty({ type: [OrderItemResponseDto] })
  items!: OrderItemResponseDto[];

  @ApiProperty()
  totalAmount!: number;

  @ApiProperty({ example: 'USD' })
  currency!: string;
}

export class CreateOrderResponseDto {
  @ApiProperty()
  orderId!: string;

  @ApiProperty({ example: 'PENDING' })
  status!: string;

  @ApiProperty()
  totalAmount!: number;

  @ApiProperty()
  createdAt!: string;
}

export class PayOrderResponseDto {
  @ApiProperty()
  orderId!: string;

  @ApiProperty({ example: 'PAID' })
  status!: string;
}
