package com.shopease.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopease.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, String>{
	
	List<Customer> findByPhoneNumber(String phoneNumber);

}
