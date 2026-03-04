import { NestFactory } from '@nestjs/core';
import { ValidationPipe } from '@nestjs/common';
import { AppModule } from './app.module';
import { ConfigService } from '@nestjs/config';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  const configService = app.get(ConfigService);

  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
    }),
  );

  const port = configService.get<number>('app.port', 3000);
  const apiPrefix = configService.get<string>('app.apiPrefix', 'api');

  if (apiPrefix) {
    app.setGlobalPrefix(apiPrefix);
  }

  await app.listen(port);
  return port;
}

bootstrap()
  .then((port) => {
    console.log(`Application is running on: http://localhost:${port}`);
  })
  .catch((err) => {
    console.error('Application failed to start', err);
    process.exit(1);
  });
