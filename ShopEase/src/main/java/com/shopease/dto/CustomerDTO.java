package com.shopease.dto;

import com.shopease.entity.Customer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerDTO {
	
	@NotNull(message = "{email.absent}")
	@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.email.format}")
	private String emailId;
	
	@Pattern(regexp = "([A-Za-z]+\\s[A-Za-z]+)*",message = "{customer.invalid.name}")
	private String name;
	
	@NotNull(message = "{password.absent}")
	@Pattern(regexp = ".*[A-Z]+.*",message = "{invalid.password.format.uppercase}")
	@Pattern(regexp = ".*[a-z]+.*",message = "{invalid.password.format.lowercase}")
	@Pattern(regexp = ".*[^a-zA-Z0-9-].*",message = "{invalid.password.format.specialcharacter}")
	private String password;
	private String newPassword;
	
	@Size(max = 10, min = 10, message = "{customer.invalid.phonenumber}")
	@Pattern(regexp = "[0-9]+",message = "{customer.invalid.phonenumber}")
	private String phoneNumber;
	
	@NotNull(message = "{customer.address.absent}")
	private String address;
	
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public static CustomerDTO customerEntityToCustomerDTO(Customer customer) {
		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setAddress(customer.getAddress());
		customerDTO.setEmailId(customer.getEmailId());
		customerDTO.setName(customer.getName());
		customerDTO.setNewPassword(customer.getPassword());
		customerDTO.setPhoneNumber(customer.getPhoneNumber());
		customerDTO.setPassword(customer.getPassword());
		return customerDTO;
	}

}
