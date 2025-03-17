package com.assignment_two_starter.repository;

import com.assignment_two_starter.model.Address;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    Optional<Address> findByStreetAddressAndCityAndPostalCode(@NotBlank(message = "Shipping address is required") String shippingAddress, @NotBlank(message = "City is required") String city, @NotBlank(message = "Postal code is required") String postalCode);
}
