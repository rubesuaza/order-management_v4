package com.example.order_management.domain.model;

import java.util.Objects;

/**
 * Value Object representing a physical address.
 * Immutable.
 */
public final class Address {
    
    private final String street;
    private final String city;
    private final String zipCode;
    private final String country;
    
    public Address(String street, String city, String zipCode, String country) {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be null or blank");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be null or blank");
        }
        if (zipCode == null || zipCode.isBlank()) {
            throw new IllegalArgumentException("ZipCode cannot be null or blank");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be null or blank");
        }
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }
    
    public String getStreet() {
        return street;
    }
    
    public String getCity() {
        return city;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
               Objects.equals(city, address.city) &&
               Objects.equals(zipCode, address.zipCode) &&
               Objects.equals(country, address.country);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(street, city, zipCode, country);
    }
    
    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s", street, city, zipCode, country);
    }
}
