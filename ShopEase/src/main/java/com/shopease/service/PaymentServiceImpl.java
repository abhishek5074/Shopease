package com.shopease.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopease.dto.CardDTO;
import com.shopease.dto.TransactionDTO;
import com.shopease.dto.TransactionStatus;
import com.shopease.entity.Card;
import com.shopease.entity.Transaction;
import com.shopease.exception.Shopease;
import com.shopease.repository.CardRepository;
import com.shopease.repository.TransactionRepository;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class PaymentServiceImpl implements PaymentService{
	
	@Autowired
	private CardRepository cardRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public Integer addCustomerCard(String customerEmailId, CardDTO cardDTO) throws Shopease {
		Card newCard = new Card();
		newCard.setNameOnCard(cardDTO.getNameOnCard());
		newCard.setCardNumber(cardDTO.getCardNumber());
		newCard.setCardType(cardDTO.getCardType());
		newCard.setExpiryDate(cardDTO.getExpiryDate());
		newCard.setCvv(cardDTO.getCvv().toString());
		newCard.setCustomerEmailId(customerEmailId);
		
		cardRepository.save(newCard);
		return newCard.getCardId();
	}

	@Override
	public void updateCustomerCard(CardDTO cardDTO) throws Shopease {
		
		Optional<Card> optionalCard = cardRepository.findById(cardDTO.getCardId()); 
		Card card = optionalCard.orElseThrow(() -> new Shopease("PaymentService.CARD_NOT_FOUND"));
		
		card.setCardId(cardDTO.getCardId());
		card.setNameOnCard(cardDTO.getNameOnCard());
		card.setCardNumber(cardDTO.getCardNumber());
		card.setCardType(cardDTO.getCardType());
		card.setCvv(cardDTO.getCvv().toString());
		card.setExpiryDate(cardDTO.getExpiryDate());
		card.setCustomerEmailId(cardDTO.getCustomerEmailId());
		
	}

	@Override
	public void deleteCustomerCard(String customerEmailId, Integer cardId) throws Shopease {
		List<Card> listOfCustomerCards = cardRepository.findByCustomerEmailId(customerEmailId);
		System.out.println(customerEmailId);
		if(listOfCustomerCards.isEmpty())
			throw new Shopease("PaymentService.CUSTOMER_NOT_FOUND");
		
		Optional<Card> optionalCards = cardRepository.findById(cardId);
		Card card = optionalCards.orElseThrow(() -> new Shopease("PaymentService.CARD_NOT_FOUND"));
		cardRepository.delete(card);
	}

	@Override
	public CardDTO getCard(Integer cardId) throws Shopease {
		
		Optional<Card> optionalCards = cardRepository.findById(cardId);
		
		Card card = optionalCards.orElseThrow(() -> new Shopease("CARD_NOT_FOUND"));
		
		CardDTO cardDTO = new CardDTO();
		cardDTO.setCardId(card.getCardId());
		cardDTO.setNameOnCard(card.getNameOnCard());
		cardDTO.setCardNumber(card.getCardNumber());
		Integer cvv = Integer.valueOf(card.getCvv());
		
		cardDTO.setCvv(cvv);
		cardDTO.setCardType(card.getCardType());
		cardDTO.setExpiryDate(card.getExpiryDate());
		cardDTO.setCustomerEmailId(card.getCustomerEmailId());
		return cardDTO;
	}

	@Override
	public List<CardDTO> getCustomerCardOfCardType(String customerEmailId, String cardType) throws Shopease {
		
		List<Card> cards = cardRepository.findByCustomerEmailIdAndCardType(customerEmailId, cardType);
		
		if(cards.isEmpty()) {
			throw new Shopease("PaymentService.CARD_NOT_FOUND");
		}
		List<CardDTO> cardDTOs = new ArrayList<>();
		for(Card card : cards) {
			CardDTO cardDTO = new CardDTO();
			cardDTO.setCardId(card.getCardId());
			cardDTO.setCardNumber(card.getCardNumber());
			cardDTO.setCardType(card.getCardType());
			cardDTO.setCustomerEmailId(card.getCustomerEmailId());
			cardDTO.setCvv(Integer.getInteger(card.getCvv()));
			cardDTO.setExpiryDate(card.getExpiryDate());
			cardDTO.setNameOnCard(card.getNameOnCard());
			cardDTOs.add(cardDTO);
		}
		return cardDTOs;
	}

	@Override
	public Integer addTransaction(TransactionDTO transactionDTO) throws Shopease {
		if(transactionDTO.getTransactionStatus().equals(TransactionStatus.TRANSACTION_FAILED)) {
			throw new Shopease("PaymentService.TRANSACTION_FAILED_CVV_NOT_MATCHING");
		}
		Transaction transaction = new Transaction();
		transaction.setCardId(transactionDTO.getCard().getCardId());
		transaction.setOrderId(transactionDTO.getOrder().getOrderId());
		transaction.setTotalPrice(transactionDTO.getTotalPrice());
		transaction.setTransactionDate(transactionDTO.getTransactionDate());
		transaction.setTransactionStatus(transactionDTO.getTransactionStatus());
		transactionRepository.save(transaction);
		
		return transaction.getTransactionId();
	}

	@Override
	public TransactionDTO autheticatePayment(String customerEmailId, TransactionDTO transactionDTO)
			throws Shopease {
		if(!transactionDTO.getOrder().getCustomerEmailId().equals(customerEmailId)) {
			throw new Shopease("PaymentService.ORDER_DOES_NOT_BELONGS");
		}
		if(!transactionDTO.getOrder().getOrderStatus().equals("PLACED")) {
			throw new Shopease("PaymentService.TRANSACTION_ALREADY_DONE");
		}
		
		CardDTO cardDTO = getCard(transactionDTO.getCard().getCardId());
		
		if(!cardDTO.getCardType().equals(transactionDTO.getOrder().getPaymentThrough())) {
			throw new Shopease("PaymentService.PAYMENT_OPTION_SELECTED_NOT_MATCHING_CARD_TYPE");
		}
		
		if(cardDTO.getCvv().equals(transactionDTO.getCard().getCvv())) {
			transactionDTO.setTransactionStatus(TransactionStatus.TRANSACTION_SUCCESS);
		} else {
			transactionDTO.setTransactionStatus(TransactionStatus.TRANSACTION_FAILED);
		}
		
		return transactionDTO;
	}

	@Override
	public List<CardDTO> getCardsOfCustomer(String customerEmailId, String cardType) throws Shopease {
		List<Card> cardList = cardRepository.findByCustomerEmailIdAndCardType(customerEmailId, cardType);
		
		if(cardList.isEmpty())
			throw new Shopease("PaymentService.CARD_NOT_FOUND");
		
		List<CardDTO> cardDTOs = new ArrayList<>();
		for(Card card : cardList) {
			CardDTO cardDTO = new CardDTO();
			cardDTO.setCardId(card.getCardId());
			cardDTO.setCardNumber(card.getCardNumber());
			cardDTO.setCardType(card.getCardType());
			cardDTO.setCustomerEmailId(card.getCustomerEmailId());
			cardDTO.setCvv(Integer.getInteger(card.getCvv()));
			cardDTO.setExpiryDate(card.getExpiryDate());
			cardDTO.setNameOnCard(card.getNameOnCard());
			cardDTOs.add(cardDTO);
		}
		
		return cardDTOs;
	}

}
