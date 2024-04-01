package com.shopease.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.shopease.dto.OrderStatus;
import com.shopease.dto.PaymentThrough;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "order")
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderId;
	private String customerEmailId;
	private LocalDateTime dateOfOrder;
	private Double discount;
	private Double totalPrice;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;
	
	@Enumerated(EnumType.STRING)
	private PaymentThrough paymentThrough;
	
	private LocalDateTime dateOfDelivery;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "orderId")
	private List<OrderedProduct> orderedProducts;
	
	private String deliveryAddress;

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

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

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public PaymentThrough getPaymentThrough() {
		return paymentThrough;
	}

	public void setPaymentThrough(PaymentThrough paymentThrough) {
		this.paymentThrough = paymentThrough;
	}

	public LocalDateTime getDateOfDelivery() {
		return dateOfDelivery;
	}

	public void setDateOfDelivery(LocalDateTime dateOfDelivery) {
		this.dateOfDelivery = dateOfDelivery;
	}

	public List<OrderedProduct> getOrderedProducts() {
		return orderedProducts;
	}

	public void setOrderedProducts(List<OrderedProduct> orderedProducts) {
		this.orderedProducts = orderedProducts;
	}

	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", customerEmailId=" + customerEmailId + ", dateOfOrder=" + dateOfOrder
				+ ", discount=" + discount + ", totalPrice=" + totalPrice + ", orderStatus=" + orderStatus
				+ ", paymentThrough=" + paymentThrough + ", dateOfDelivery=" + dateOfDelivery + ", orderedProducts="
				+ orderedProducts + ", deliveryAddress=" + deliveryAddress + "]";
	}
	
}
