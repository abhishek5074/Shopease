package com.shopease.api;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.shopease.dto.CardDTO;
import com.shopease.dto.OrderDTO;
import com.shopease.dto.TransactionDTO;
import com.shopease.exception.Shopease;
import com.shopease.service.PaymentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping(value = "/payment-api")
@Validated
public class PaymentAPI {

	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private RestClient client;
	
	Log logger = LogFactory.getLog(PaymentAPI.class);
	
	@PostMapping(value = "/customer/{customerEmailId}/cards")
	public ResponseEntity<String> addNewCard(@RequestBody CardDTO cardDTO, @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.email.format}") @PathVariable("customerEmailId") String customerEmailId) throws Shopease {
		logger.info("Received request to add new card for customer : " + customerEmailId);
		cardDTO.setCustomerEmailId(customerEmailId);
		
		int cardId;
		cardId = paymentService.addCustomerCard(customerEmailId, cardDTO);
		String message = environment.getProperty("PaymentAPI.NEW_CARD_ADDED_SUCCESS");
		String toReturn = message + cardId;
		return new ResponseEntity<String>(toReturn,HttpStatus.OK);
	}
	
	@PutMapping(value = "/update/card")
	public ResponseEntity<String> updateCustomerCard(@Valid @RequestBody CardDTO cardDTO) throws Shopease {
		logger.info("Received request to update card : " + cardDTO.getCardId() + " of customer : " + cardDTO.getCustomerEmailId());
		
		paymentService.updateCustomerCard(cardDTO);
		String modificationSuccessMsg = environment.getProperty("PaymentAPI.UPDATE_CARD_SUCCESS");
		return new ResponseEntity<String>(modificationSuccessMsg,HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/customer/{customerEmailId}/card/{cardId}/delete")
	public ResponseEntity<String> deleteCustomerCard(@PathVariable("cardId") Integer cardId, @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z.]+", message = "{invalid.email.format}") @PathVariable("customerEmailId") String customerEmailId) throws Shopease {
		logger.info("Received request to delete card : " + cardId + " of cusotmer : " + customerEmailId);
		
		paymentService.deleteCustomerCard(customerEmailId, cardId);
		String modificationSuccessMsg = environment.getProperty("PaymentAPI.CUSTOMER_CARD_DELETED_SUCCESS");
		return new ResponseEntity<String>(modificationSuccessMsg,HttpStatus.OK);
	}
	
	@GetMapping(value = "/customer/{customerEmailId}/card-type/{cardType}")
	public ResponseEntity<List<CardDTO>> getCardsOfCustomer(@PathVariable String customerEmailId, @PathVariable String cardType) throws Shopease {
		logger.info("Received request to fetch card of customer : " + customerEmailId + " having card type as : " + cardType);
		List<CardDTO> cardList = paymentService.getCustomerCardOfCardType(customerEmailId, cardType);
		return new ResponseEntity<List<CardDTO>>(cardList,HttpStatus.OK);
		}
	
	@PostMapping(value = "/customer/{customerEmailId}/order/{orderId}")
	public ResponseEntity<String> payForOrder( @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z.]+", message = "{invalid.email.format}") @PathVariable("customerEmailId") String customerEmailId, @NotNull(message = "{orderId.absent}") @PathVariable("orderId") Integer orderId, @Valid @RequestBody CardDTO cardDTO) throws Shopease {
		// order details
		OrderDTO orderDTO = client.get().uri("http://localhost:8080/Shopease/order-api/order/"+orderId).retrieve().body(OrderDTO.class);
		CardDTO dto = paymentService.getCard(cardDTO.getCardId());
		
		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setCard(dto);
		transactionDTO.setOrder(orderDTO);
		transactionDTO.setTotalPrice(orderDTO.getTotalPrice());
		transactionDTO.setTransactionDate(orderDTO.getDateOfOrder());
		
		TransactionDTO transactionDTO2 = paymentService.autheticatePayment(customerEmailId, transactionDTO);
		// add transaction
		paymentService.addTransaction(transactionDTO2);
		// order api updateOrderAfterPayment
		TransactionDTO transactionDTO3 = new TransactionDTO();
		transactionDTO3.setTransactionStatus(transactionDTO2.getTransactionStatus());
		client.put().uri("http://localhost:8080/Shopease/order-api/order/"+orderDTO.getOrderId()+"/update/order-status").contentType(MediaType.APPLICATION_JSON).body(transactionDTO3).retrieve().toBodilessEntity();
		String success = environment.getProperty("PaymentAPI.TRANSACTION_SUCCESSFULL_ONE") + transactionDTO2.getTotalPrice() + environment.getProperty("PaymentAPI.TRANSACTION_SUCCESSFULL_TWO") + transactionDTO2.getOrder().getOrderId() + environment.getProperty("PaymentAPI.TRANSACTION_SUCCESSFULL_THREE") + transactionDTO2.getTransactionId();
		
		
		return new ResponseEntity<String>(success,HttpStatus.OK);
	}
 }
