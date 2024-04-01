package com.shopease.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopease.entity.Order;

public interface CustomerOrderRepository extends CrudRepository<Order, Integer>{
	List<Order> findByCustomerEmailId(String customerEmailId);
}
