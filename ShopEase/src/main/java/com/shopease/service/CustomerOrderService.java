package com.shopease.service;

import java.util.List;

import com.shopease.dto.OrderDTO;
import com.shopease.dto.OrderStatus;
import com.shopease.dto.PaymentThrough;
import com.shopease.exception.Shopease;

public interface CustomerOrderService {

	Integer placeOrder(OrderDTO orderDTO) throws Shopease;
	
	OrderDTO getOrderDetails(Integer orderId) throws Shopease;
	
	List<OrderDTO> findOrdersByCustomerEmailId(String emailId) throws Shopease;
	
	void updateOrderStatus(Integer orderId, OrderStatus orderStatus) throws Shopease;
	
	void updatePaymentThrough(Integer orderId, PaymentThrough paymentThrough) throws Shopease;
}
