package com.shopease.service;

import java.util.List;

import com.shopease.dto.CardDTO;
import com.shopease.dto.TransactionDTO;
import com.shopease.exception.Shopease;

public interface PaymentService {
	
	Integer addCustomerCard(String customerEmailId, CardDTO cardDTO) throws Shopease;

	void updateCustomerCard(CardDTO cardDTO) throws Shopease;
	
	void deleteCustomerCard(String customerEmailId, Integer cardId) throws Shopease;
	
	CardDTO getCard(Integer cardId) throws Shopease;
	
	List<CardDTO> getCustomerCardOfCardType(String customerEmailId, String cardType) throws Shopease;
	
	Integer addTransaction(TransactionDTO transactionDTO) throws Shopease;
	
	TransactionDTO autheticatePayment(String customerEmailId, TransactionDTO transactionDTO) throws Shopease;
	
	List<CardDTO> getCardsOfCustomer(String customerEmailId, String cardType) throws Shopease;
}
