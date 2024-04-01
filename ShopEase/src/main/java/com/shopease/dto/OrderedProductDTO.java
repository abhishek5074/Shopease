package com.shopease.dto;

public class OrderedProductDTO {
	
	private Integer orderedProductId;
	private ProductDTO product;
	private Integer quantity;
	
	public Integer getOrderedProductId() {
		return orderedProductId;
	}
	public void setOrderedProductId(Integer orderedProductId) {
		this.orderedProductId = orderedProductId;
	}
	public ProductDTO getProduct() {
		return product;
	}
	public void setProduct(ProductDTO product) {
		this.product = product;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "OrderedProductDTO [orderedProductId=" + orderedProductId + ", product=" + product + ", quantity="
				+ quantity + "]";
	}

}
