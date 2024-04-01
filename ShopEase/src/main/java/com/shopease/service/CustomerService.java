package com.shopease.service;

import com.shopease.dto.CustomerDTO;
import com.shopease.exception.Shopease;

public interface CustomerService {
	
	CustomerDTO authenticateCustomer(String emailId, String password) throws Shopease;
	
	String registerNewCustomer(CustomerDTO customerEmailId) throws Shopease;
	
	void updateShippingAddress(String customerEmailId, String address) throws Shopease;
	
	void deleteShippingAddress(String customerEmailId) throws Shopease;
	
	CustomerDTO getCustomerByEmailId(String emailId) throws Shopease;

}
