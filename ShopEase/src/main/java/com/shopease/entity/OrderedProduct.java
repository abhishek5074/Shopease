package com.shopease.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordered_product")
public class OrderedProduct {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderedProductId;
	private Integer productId;
	private Integer quantity;
	
	public Integer getOrderedProductId() {
		return orderedProductId;
	}
	public void setOrderedProductId(Integer orderedProductId) {
		this.orderedProductId = orderedProductId;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "OrderedProduct [orderedProductId=" + orderedProductId + ", productId=" + productId + ", quantity="
				+ quantity + "]";
	}
	
}
