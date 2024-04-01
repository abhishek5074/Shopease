package com.shopease.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopease.dto.CustomerDTO;
import com.shopease.entity.Customer;
import com.shopease.exception.Shopease;
import com.shopease.repository.CustomerRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService{
	
	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public CustomerDTO authenticateCustomer(String emailId, String password) throws Shopease {
		CustomerDTO customerDTO = null;
		
		Optional<Customer> optionalCustomer = customerRepository.findById(emailId.toLowerCase());
		Customer customer = optionalCustomer.orElseThrow(() ->  new Shopease("CustomerService.CUSTOMER_NOT_FOUND"));
		
		if(!password.equals(customer.getPassword()))
			throw new Shopease("CustomerService.INVALID_CREDENTIALS");
		
		customerDTO = CustomerDTO.customerEntityToCustomerDTO(customer);
		return customerDTO;
	}

	@Override
	public String registerNewCustomer(CustomerDTO customerDTO) throws Shopease {
		String registeredWithEmailId=null;
		boolean isEmailNotAvailable = customerRepository.findById(customerDTO.getEmailId().toLowerCase()).isEmpty();
		boolean isPhoneNumberNotAvailable = customerRepository.findByPhoneNumber(customerDTO.getPhoneNumber()).isEmpty();
		if(isEmailNotAvailable) {
			if(isPhoneNumberNotAvailable) {
				Customer customer = new Customer();
				customer.setEmailId(customerDTO.getEmailId().toLowerCase());
				customer.setName(customerDTO.getName());
				customer.setPassword(customerDTO.getPassword());
				customer.setPhoneNumber(customerDTO.getPhoneNumber());
				customer.setAddress(customerDTO.getAddress());
				customerRepository.save(customer);
				registeredWithEmailId = customer.getEmailId();
			} else {
				throw new Shopease("CustomerService.PHONE_NUMBER_ALREADY_IN_USE");
			}
		}  else {
			throw new Shopease("CustomerService.EMAIL_ID_ALREADY_IN_USE");
		}
		return registeredWithEmailId;
	}

	@Override
	public void updateShippingAddress(String customerEmailId, String address) throws Shopease {
		Optional<Customer> optionalCustomer = customerRepository.findById(customerEmailId.toLowerCase());
		Customer customer = optionalCustomer.orElseThrow(() -> new Shopease("CustomerService.CUSTOMER_NOT_FOUND"));
		customer.setAddress(address);
	}

	@Override
	public void deleteShippingAddress(String customerEmailId) throws Shopease {
		Optional<Customer> optionalCustomer = customerRepository.findById(customerEmailId.toLowerCase());
		Customer customer = optionalCustomer.orElseThrow(() -> new Shopease("CustomeService.CUSTOMER_NOT_FOUND"));
		customer.setAddress(null);
	}

	@Override
	public CustomerDTO getCustomerByEmailId(String emailId) throws Shopease {
		Optional<Customer> optionalCustomer = customerRepository.findById(emailId.toLowerCase());
		Customer customer = optionalCustomer.orElseThrow(() -> new Shopease("CustomerService.CUSTOMER_NOT_FOUND"));
		return CustomerDTO.customerEntityToCustomerDTO(customer);
	}

	
}
