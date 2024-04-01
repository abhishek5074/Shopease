package com.shopease.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopease.dto.CustomerCredDTO;
import com.shopease.dto.CustomerDTO;
import com.shopease.exception.Shopease;
import com.shopease.service.CustomerService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(value = "/customer-api")
@Validated
public class CustomerAPI {
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private Environment environment;
	
	static Log logger = LogFactory.getLog(CustomerAPI.class);
	
	@PostMapping(value = "/login")
	public ResponseEntity<CustomerDTO> authenticateCustomer(@Valid @RequestBody CustomerCredDTO custCredDTO) throws Shopease{
		logger.info("CUSTOMER TRYING TO LOGIN, VALIDATING CREDENTIALS. CUSTOEMR EMAIL ID: " + custCredDTO.getEmailId());
		CustomerDTO customerDTOFromDB = customerService.authenticateCustomer(custCredDTO.getEmailId(), custCredDTO.getPassword());
		logger.info("CUSTOMER LOGIN SUCCESS, CUSTOEMR EMAIL : "+customerDTOFromDB.getEmailId());
		return new ResponseEntity<>(customerDTOFromDB,HttpStatus.OK);
	}
	
	@PostMapping(value = "/register")
	public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerDTO customerDTO) throws Shopease{
		logger.info("CUSTOMER TRYING TO REGISTER. CUSTOMER EMAIL ID: " + customerDTO.getEmailId());
		String registeredWithEmailId;
		registeredWithEmailId = customerService.registerNewCustomer(customerDTO);
		registeredWithEmailId = environment.getProperty("CustomerAPI.CUSTOMER_REGISTRATION_SUCCESS") + registeredWithEmailId;
		return new ResponseEntity<>(registeredWithEmailId,HttpStatus.OK);
	}

	@PutMapping(value = "/customer/{customerEmailId}/address/")
	public ResponseEntity<String> updateShippingAddress(@PathVariable @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.email.format}") String customerEmailId, @RequestBody CustomerDTO customerDTO) throws Shopease {
		System.out.println("Request to update address " + customerEmailId + " for address " + customerDTO.getAddress());
		customerService.updateShippingAddress(customerEmailId, customerDTO.getAddress());
		String modificationSuccessMsg = environment.getProperty("CustomerAPI.UPDATE_ADDRESS_SUCCESS");
		return new ResponseEntity<>(modificationSuccessMsg,HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/customer/{customerEmailId}")
	public ResponseEntity<String> deleteShippingAddress(@PathVariable @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.email.format}") String customerEmailId) throws Shopease {
		customerService.deleteShippingAddress(customerEmailId);
		String modificationSuccessMsg = environment.getProperty("CustomerAPI.CUSTOMER_ADDRESS_DELETED_SUCCESS");
		return new ResponseEntity<>(modificationSuccessMsg,HttpStatus.OK);
	}
}
