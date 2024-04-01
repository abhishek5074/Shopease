package com.shopease.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopease.dto.CustomerDTO;
import com.shopease.dto.OrderDTO;
import com.shopease.dto.OrderStatus;
import com.shopease.dto.OrderedProductDTO;
import com.shopease.dto.PaymentThrough;
import com.shopease.dto.ProductDTO;
import com.shopease.entity.Order;
import com.shopease.entity.OrderedProduct;
import com.shopease.exception.Shopease;
import com.shopease.repository.CustomerOrderRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CustomerOrderServiceImpl implements CustomerOrderService {
	
	@Autowired
	private CustomerOrderRepository orderRepository;
	
	@Autowired
	private CustomerService customerService;

	@Override
	public Integer placeOrder(OrderDTO orderDTO) throws Shopease {
		
		CustomerDTO customerDTO = customerService.getCustomerByEmailId(orderDTO.getCustomerEmailId());
		
		if(customerDTO.getAddress() == null || customerDTO.getAddress().isBlank()) {
			throw new Shopease("OrderService.ADDRESS_NOT_AVAILABLE");
		}
		
		Order order = new Order();
		order.setDeliveryAddress(customerDTO.getAddress());
		order.setCustomerEmailId(orderDTO.getCustomerEmailId());
		order.setDateOfDelivery(orderDTO.getDateOfDelivery());
		order.setDateOfOrder(LocalDateTime.now());
		order.setPaymentThrough(PaymentThrough.valueOf(orderDTO.getPaymentThrough()));
		
		if(order.getPaymentThrough().equals(PaymentThrough.CREDIT_CARD)) {
			order.setDiscount(10.00d);
		} else {
			order.setDiscount(5.00d);
		}
		
		order.setOrderStatus(OrderStatus.PLACED);
		Double price = 0.0;
		List<OrderedProduct> orderedProducts = new ArrayList<>();
		
		for(OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {
			if(orderedProductDTO.getProduct().getAvailableQuantity() < orderedProductDTO.getQuantity()) {
				throw new Shopease("OrderService.INSUFFICIENT_STOCK");
			}
			
			OrderedProduct orderedProduct = new OrderedProduct();
			orderedProduct.setProductId(orderedProductDTO.getProduct().getProductId());
			orderedProduct.setQuantity(orderedProductDTO.getQuantity());
			orderedProducts.add(orderedProduct);
			price = price + orderedProductDTO.getQuantity() * orderedProductDTO.getProduct().getPrice();
		}
		
		order.setOrderedProducts(orderedProducts);
		
		order.setTotalPrice(price * (100 - order.getDiscount()) / 100);
		
		orderRepository.save(order);
		
		return order.getOrderId();
	}

	@Override
	public OrderDTO getOrderDetails(Integer orderId) throws Shopease {
		
		Optional<Order> optionalOrder = orderRepository.findById(orderId);
		Order order = optionalOrder.orElseThrow(() -> new Shopease("OrderService.ORDER_NOT_FOUND"));
		
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderId(orderId);
		orderDTO.setCustomerEmailId(order.getCustomerEmailId());
		orderDTO.setDateOfDelivery(order.getDateOfDelivery());
		orderDTO.setDateOfOrder(order.getDateOfOrder());
		orderDTO.setPaymentThrough(order.getPaymentThrough().toString());
		orderDTO.setTotalPrice(order.getTotalPrice());
		orderDTO.setOrderStatus(order.getOrderStatus().toString());
		orderDTO.setDiscount(order.getDiscount());
		orderDTO.setDeliveryAddress(order.getDeliveryAddress());
		
		List<OrderedProductDTO> orderedProductDTOs = new ArrayList<>();
		
		//OrderedProduct [orderedProductId=60000, productId=1000, quantity=1]
		
		for(OrderedProduct orderedProduct : order.getOrderedProducts()) {
			OrderedProductDTO orderedProductDTO = new OrderedProductDTO();
			orderedProductDTO.setOrderedProductId(orderedProduct.getOrderedProductId());
			
			ProductDTO productDTO = new ProductDTO();
			productDTO.setProductId(orderedProduct.getProductId());
			
			orderedProductDTO.setQuantity(orderedProduct.getQuantity());
			orderedProductDTO.setProduct(productDTO);
			
			orderedProductDTOs.add(orderedProductDTO);
		}
		
		orderDTO.setOrderedProducts(orderedProductDTOs);
		return orderDTO;
	}

	@Override
	public List<OrderDTO> findOrdersByCustomerEmailId(String emailId) throws Shopease {
		List<Order> orders = orderRepository.findByCustomerEmailId(emailId);
		if(orders.isEmpty()) {
			throw new Shopease("OrderService.NO_ORDERS_FOUND");
		}
		List<OrderDTO> orderDTOs = new ArrayList<>();
		for(Order order : orders) {
			OrderDTO orderDTO = new OrderDTO();
			orderDTO.setOrderId(order.getOrderId());
			orderDTO.setCustomerEmailId(order.getCustomerEmailId());
			orderDTO.setDateOfDelivery(order.getDateOfDelivery());
			orderDTO.setDateOfOrder(order.getDateOfOrder());
			orderDTO.setPaymentThrough(order.getPaymentThrough().toString());
			orderDTO.setTotalPrice(order.getTotalPrice());
			orderDTO.setOrderStatus(order.getOrderStatus().toString());
			orderDTO.setDiscount(order.getDiscount());
			List<OrderedProductDTO> orderedProductDTOs = new ArrayList<>();
			
			for(OrderedProduct orderedProduct : order.getOrderedProducts()) {
				OrderedProductDTO orderedProductDTO = new OrderedProductDTO();
				ProductDTO productDTO = new ProductDTO();
				productDTO.setProductId(orderedProduct.getProductId());
				orderedProductDTO.setOrderedProductId(orderedProduct.getOrderedProductId());
				orderedProductDTO.setQuantity(orderedProduct.getQuantity());
				orderedProductDTO.setProduct(productDTO);
				orderedProductDTOs.add(orderedProductDTO);
			}
			orderDTO.setOrderedProducts(orderedProductDTOs);
			orderDTO.setDeliveryAddress(order.getDeliveryAddress());
			orderDTOs.add(orderDTO);
		}
		
		return orderDTOs;
	}

	@Override
	public void updateOrderStatus(Integer orderId, OrderStatus orderStatus) throws Shopease {
		Optional<Order> optionalOrder = orderRepository.findById(orderId);
		Order order = optionalOrder.orElseThrow(() -> new Shopease("OrderService.ORDER_NOT_FOUND"));
		order.setOrderStatus(orderStatus);
		
	}

	@Override
	public void updatePaymentThrough(Integer orderId, PaymentThrough paymentThrough) throws Shopease {
		Optional<Order> optionalOrder = orderRepository.findById(orderId);
		Order order = optionalOrder.orElseThrow(() -> new Shopease("OrderService.ORDER_NOT_FOUND"));
		if(order.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
			throw new Shopease("OrderService.TRANSACTION_ALREADY_DONE");
		}
		order.setPaymentThrough(paymentThrough);
	}

}
