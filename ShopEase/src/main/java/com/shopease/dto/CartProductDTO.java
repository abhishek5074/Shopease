package com.shopease.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;

public class CartProductDTO {
	
	private Integer cartProductId;
	@Valid
	private ProductDTO product;
	@PositiveOrZero(message = "{cartproduct.invalid.quantity}")
	private Integer quantity;

	public Integer getCartProductId() {
		return cartProductId;
	}

	public void setCartProductId(Integer cartProductId) {
		this.cartProductId = cartProductId;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((product.getProductId() == null) ? 0 : product.getProductId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CartProductDTO other = (CartProductDTO) obj;
		if(product.getProductId() == null) {
			if(other.product.getProductId() != null)
				return false;
		} else if(!product.getProductId().equals(other.product.getProductId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CartProductDTO [cartProductId=" + cartProductId + ", product=" + product.toString() + ", quantity=" + quantity
				+ "]";
	}
	
}
