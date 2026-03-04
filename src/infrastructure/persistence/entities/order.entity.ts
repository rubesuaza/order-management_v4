import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  OneToMany,
  Index,
  CreateDateColumn,
} from 'typeorm';
import { OrderItemEntity } from './order-item.entity';

@Entity('orders')
@Index('idx_orders_customer_id', ['customerId'])
@Index('idx_orders_status', ['status'])
export class OrderEntity {
  @PrimaryGeneratedColumn('uuid')
  id!: string;

  @Column({ name: 'customer_id', type: 'uuid' })
  customerId!: string;

  @Column({ type: 'varchar', length: 20 })
  status!: string;

  @Column({
    name: 'total_amount',
    type: 'decimal',
    precision: 19,
    scale: 2,
  })
  totalAmount!: string;

  @Column({ type: 'varchar', length: 3 })
  currency!: string;

  @CreateDateColumn({ name: 'created_at' })
  createdAt!: Date;

  @OneToMany(() => OrderItemEntity, (item) => item.order, { cascade: true })
  items!: OrderItemEntity[];
}
