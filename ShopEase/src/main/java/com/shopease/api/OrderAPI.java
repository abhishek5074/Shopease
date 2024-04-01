package com.shopease.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.shopease.dto.CartProductDTO;
import com.shopease.dto.OrderDTO;
import com.shopease.dto.OrderStatus;
import com.shopease.dto.OrderedProductDTO;
import com.shopease.dto.PaymentThrough;
import com.shopease.dto.PaymentThroughRequest;
import com.shopease.dto.ProductDTO;
import com.shopease.dto.TransactionStatusRequest;
import com.shopease.exception.Shopease;
import com.shopease.service.CustomerOrderService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@CrossOrigin
@RestController
@RequestMapping(value = "/order-api")
@Validated
public class OrderAPI {
	
	@Autowired
	private CustomerOrderService orderService;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private RestClient client;

	@PostMapping(value = "/place-order")
	public ResponseEntity<String> placeOrder(@Valid @RequestBody OrderDTO order) throws Shopease {
		List<CartProductDTO> cartProductDTOsResponse = client.get().uri("http://localhost:8080/Shopease/cart-api/customer/"+order.getCustomerEmailId()+"/products").retrieve().body(new ParameterizedTypeReference<>() {});
		
		// Delete Order From Cart
		client.delete().uri("http://localhost:8080/Shopease/cart-api/customer/"+order.getCustomerEmailId()+"/products").retrieve().body(String.class);
		List<OrderedProductDTO> orderedProductDTOs = new ArrayList<>();
		for(CartProductDTO cartProductDTO : cartProductDTOsResponse) {
			OrderedProductDTO orderedProductDTO = new OrderedProductDTO();
			orderedProductDTO.setProduct(cartProductDTO.getProduct());
			orderedProductDTO.setQuantity(cartProductDTO.getQuantity());
			orderedProductDTOs.add(orderedProductDTO);
		}
		
		order.setOrderedProducts(orderedProductDTOs);
		
		Integer orderId = orderService.placeOrder(order);
		String modificationSuccessMsg = environment.getProperty("OrderAPI.ORDERED_PLACE_SUCCESSFULLY");
		
		return new ResponseEntity<String>(modificationSuccessMsg + orderId, HttpStatus.OK);
	}
	
	
	@GetMapping(value = "/order/{orderId}")
	public ResponseEntity<OrderDTO> getOrderDetails( @NotNull(message = "{orderId.absent}") @PathVariable Integer orderId) throws Shopease {
		OrderDTO orderDTO = orderService.getOrderDetails(orderId);
		for(OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {
			ProductDTO productResponse = client.get().uri("http://localhost:8080/Shopease/product-api/product/"+orderedProductDTO.getProduct().getProductId()).retrieve().body(ProductDTO.class);
			orderedProductDTO.setProduct(productResponse);
		}
		
		return new ResponseEntity<OrderDTO>(orderDTO,HttpStatus.OK);
	}
	
	@GetMapping(value = "customer/{customerEmailId}/orders")
	public ResponseEntity<List<OrderDTO>> getOrdersOfCustomer(@NotNull(message = "{email.absent}") @PathVariable String customerEmailId) throws Shopease {
		List<OrderDTO> orderDTOs = orderService.findOrdersByCustomerEmailId(customerEmailId);
		for(OrderDTO orderDTO : orderDTOs) {
			for(OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts())
			{
				ProductDTO productResponse = client.get().uri("http://localhost:8080/Shopease/product-api/product/"+orderedProductDTO.getProduct().getProductId()).retrieve().body(ProductDTO.class);
				orderedProductDTO.setProduct(productResponse);
			}
		}
		return new ResponseEntity<List<OrderDTO>>(orderDTOs,HttpStatus.OK);
		
	}
	
	@PutMapping(value = "/order/{orderId}/update/order-status")
	public void updateOrderAfterPayment(@NotNull(message = "{orderId.absent}") @PathVariable Integer orderId, @RequestBody TransactionStatusRequest transactionStatus) throws Shopease {
		if (transactionStatus.getTransactionStatus().equals("TRANSACTION_SUCCESS")) {
			orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
			OrderDTO orderDTO = orderService.getOrderDetails(orderId);
			for (OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {
				ProductDTO productDTO = new ProductDTO();
				System.out.println("Reached here" + orderedProductDTO);
				productDTO.setAvailableQuantity(orderedProductDTO.getQuantity());
				client.put().uri("http://localhost:8080/Shopease/product-api/product/"+orderedProductDTO.getProduct().getProductId()).contentType(MediaType.APPLICATION_JSON).body(productDTO).retrieve().body(String.class);
			}
		} else {
			System.out.println("Transaction failure");
			orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
		}
	}
	
	@PutMapping(value = "/order/{orderId}/update/payment-through")
	public void updatePaymentOption(@NotNull(message = "{orderId.absent}") @PathVariable Integer orderId, @RequestBody PaymentThroughRequest paymentThrough) throws Shopease {
		if (paymentThrough.getPaymentThrough().equals("DEBIT_CARD")) {
			orderService.updatePaymentThrough(orderId, PaymentThrough.DEBIT_CARD);
		} else {
			orderService.updatePaymentThrough(orderId, PaymentThrough.CREDIT_CARD);
		}
	}
}
