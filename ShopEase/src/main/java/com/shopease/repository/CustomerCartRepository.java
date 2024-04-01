package com.shopease.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.shopease.entity.CustomerCart;

public interface CustomerCartRepository extends CrudRepository<CustomerCart, Integer>{

	Optional<CustomerCart> findByCustomerEmailId(String customerEmailId);
}
