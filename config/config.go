package config

import (
	"os"

	"github.com/joho/godotenv"
)

// Config holds application configuration from environment variables.
type Config struct {
	ServerPort string
	DBHost     string
	DBPort     string
	DBUser     string
	DBPassword string
	DBName     string
	DBSSLMode  string
}

// Load reads configuration from environment (and optional .env file).
func Load() (*Config, error) {
	_ = godotenv.Load()

	return &Config{
		ServerPort: getEnv("SERVER_PORT", "8080"),
		DBHost:     getEnv("DB_HOST", "localhost"),
		DBPort:     getEnv("DB_PORT", "5432"),
		DBUser:     getEnv("DB_USER", "postgres"),
		DBPassword: getEnv("DB_PASSWORD", ""),
		DBName:     getEnv("DB_NAME", "order_management"),
		DBSSLMode:  getEnv("DB_SSL_MODE", "disable"),
	}, nil
}

func getEnv(key, defaultVal string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return defaultVal
}
