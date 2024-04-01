package com.shopease.service;

import java.util.Set;

import com.shopease.dto.CartProductDTO;
import com.shopease.dto.CustomerCartDTO;
import com.shopease.exception.Shopease;

public interface CustomerCartService {

	Integer addProductToCart(CustomerCartDTO customerCart) throws Shopease;
	
	Set<CartProductDTO> getProductsFromCart(String customerEmailId) throws Shopease;
	
	void modifyQuantityOfProductInCart(String customerEmailId, Integer productId, Integer quantity) throws Shopease;
	
	void deleteProductFromCart(String customerEmailId, Integer productId) throws Shopease;
	
	void deleteAllProductsFromCart(String customerEmailId) throws Shopease;

}
