import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import appConfig, { dbConfig } from './config/app.config';
import { OrderEntity } from './infrastructure/persistence/entities/order.entity';
import { OrderItemEntity } from './infrastructure/persistence/entities/order-item.entity';
import { OrderRepository } from './infrastructure/persistence/repositories/order.repository';
import { ORDER_REPOSITORY } from './application/ports/order-repository.port';
import { CreateOrderService } from './application/services/create-order.service';
import { GetOrderService } from './application/services/get-order.service';
import { PayOrderService } from './application/services/pay-order.service';
import { OrdersController } from './infrastructure/http/controllers/orders.controller';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      load: [appConfig, dbConfig],
      envFilePath: ['.env', '.env.local'],
    }),
    TypeOrmModule.forRootAsync({
      imports: [ConfigModule],
      inject: [ConfigService],
      useFactory: (config: ConfigService) => ({
        type: 'postgres',
        host: config.get<string>('db.host'),
        port: config.get<number>('db.port'),
        username: config.get<string>('db.username'),
        password: config.get<string>('db.password'),
        database: config.get<string>('db.database'),
        entities: [OrderEntity, OrderItemEntity],
        synchronize: config.get<string>('app.nodeEnv') === 'development',
        logging: config.get<string>('app.nodeEnv') === 'development',
      }),
    }),
    TypeOrmModule.forFeature([OrderEntity, OrderItemEntity]),
  ],
  controllers: [OrdersController],
  providers: [
    { provide: ORDER_REPOSITORY, useClass: OrderRepository },
    CreateOrderService,
    GetOrderService,
    PayOrderService,
  ],
})
export class AppModule {}
