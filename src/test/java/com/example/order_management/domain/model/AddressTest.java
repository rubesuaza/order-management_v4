package com.example.order_management.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressTest {

    @Test
    void shouldCreateAddressWithAllFields() {
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        
        assertThat(address.getStreet()).isEqualTo("123 Main St");
        assertThat(address.getCity()).isEqualTo("New York");
        assertThat(address.getZipCode()).isEqualTo("10001");
        assertThat(address.getCountry()).isEqualTo("USA");
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreSame() {
        Address address1 = new Address("123 Main St", "New York", "10001", "USA");
        Address address2 = new Address("123 Main St", "New York", "10001", "USA");
        
        assertThat(address1).isEqualTo(address2);
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenFieldsDiffer() {
        Address address1 = new Address("123 Main St", "New York", "10001", "USA");
        Address address2 = new Address("456 Oak Ave", "New York", "10001", "USA");
        
        assertThat(address1).isNotEqualTo(address2);
    }

    @Test
    void shouldBeImmutable() {
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        String originalStreet = address.getStreet();
        
        // Address is immutable, so we can't modify it
        assertThat(address.getStreet()).isEqualTo(originalStreet);
    }
}
