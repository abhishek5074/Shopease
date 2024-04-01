package com.shopease.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
	
	private Integer orderId;
	private String customerEmailId;
	private LocalDateTime dateOfOrder;
	private Double totalPrice;
	private String orderStatus;
	private Double discount;
	private String paymentThrough;
	private LocalDateTime dateOfDelivery;
	private String deliveryAddress;
	
	private List<OrderedProductDTO> orderedProducts;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getCustomerEmailId() {
		return customerEmailId;
	}

	public void setCustomerEmailId(String customerEmailId) {
		this.customerEmailId = customerEmailId;
	}

	public LocalDateTime getDateOfOrder() {
		return dateOfOrder;
	}

	public void setDateOfOrder(LocalDateTime dateOfOrder) {
		this.dateOfOrder = dateOfOrder;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public String getPaymentThrough() {
		return paymentThrough;
	}

	public void setPaymentThrough(String paymentThrough) {
		this.paymentThrough = paymentThrough;
	}

	public LocalDateTime getDateOfDelivery() {
		return dateOfDelivery;
	}

	public void setDateOfDelivery(LocalDateTime dateOfDelivery) {
		this.dateOfDelivery = dateOfDelivery;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public List<OrderedProductDTO> getOrderedProducts() {
		return orderedProducts;
	}

	public void setOrderedProducts(List<OrderedProductDTO> orderedProducts) {
		this.orderedProducts = orderedProducts;
	}

	@Override
	public String toString() {
		return "OrderDTO [orderId=" + orderId + ", customerEmailId=" + customerEmailId + ", dateOfOrder=" + dateOfOrder
				+ ", totalPrice=" + totalPrice + ", orderStatus=" + orderStatus + ", discount=" + discount
				+ ", paymentThrough=" + paymentThrough + ", dateOfDelivery=" + dateOfDelivery + ", deliveryAddress="
				+ deliveryAddress + ", orderedProducts=" + orderedProducts + "]";
	}

}
