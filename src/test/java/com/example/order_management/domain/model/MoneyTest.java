package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithDefaultCurrency() {
        Money money = new Money(new BigDecimal("100.00"));
        
        assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(money.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldCreateMoneyWithSpecificCurrency() {
        Money money = new Money(new BigDecimal("50.00"), "EUR");
        
        assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(money.getCurrency()).isEqualTo("EUR");
    }

    @Test
    void shouldAddTwoMoneyObjectsWithSameCurrency() {
        Money money1 = new Money(new BigDecimal("100.00"), "USD");
        Money money2 = new Money(new BigDecimal("50.00"), "USD");
        
        Money result = money1.add(money2);
        
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        Money money1 = new Money(new BigDecimal("100.00"), "USD");
        Money money2 = new Money(new BigDecimal("50.00"), "EUR");
        
        assertThatThrownBy(() -> money1.add(money2))
                .isInstanceOf(CurrencyMismatchException.class)
                .hasMessageContaining("Currency mismatch");
    }

    @Test
    void shouldSubtractTwoMoneyObjectsWithSameCurrency() {
        Money money1 = new Money(new BigDecimal("100.00"), "USD");
        Money money2 = new Money(new BigDecimal("30.00"), "USD");
        
        Money result = money1.subtract(money2);
        
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("70.00"));
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldThrowExceptionWhenSubtractingDifferentCurrencies() {
        Money money1 = new Money(new BigDecimal("100.00"), "USD");
        Money money2 = new Money(new BigDecimal("30.00"), "EUR");
        
        assertThatThrownBy(() -> money1.subtract(money2))
                .isInstanceOf(CurrencyMismatchException.class)
                .hasMessageContaining("Currency mismatch");
    }

    @Test
    void shouldMultiplyMoneyByInteger() {
        Money money = new Money(new BigDecimal("25.50"), "USD");
        
        Money result = money.multiply(3);
        
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("76.50"));
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldCompareMoneyValues() {
        Money money1 = new Money(new BigDecimal("100.00"), "USD");
        Money money2 = new Money(new BigDecimal("50.00"), "USD");
        Money money3 = new Money(new BigDecimal("100.00"), "USD");
        
        assertThat(money1.isGreaterThan(money2)).isTrue();
        assertThat(money2.isLessThan(money1)).isTrue();
        assertThat(money1.isEqualTo(money3)).isTrue();
    }

    @Test
    void shouldBeImmutable() {
        Money original = new Money(new BigDecimal("100.00"), "USD");
        Money added = original.add(new Money(new BigDecimal("50.00"), "USD"));
        
        assertThat(original.getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(added.getAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
    }
}
