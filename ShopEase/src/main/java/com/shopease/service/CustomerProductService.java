package com.shopease.service;

import java.util.List;

import com.shopease.dto.ProductDTO;
import com.shopease.exception.Shopease;

public interface CustomerProductService {
	
	List<ProductDTO> getAllProducts() throws Shopease;
	
	ProductDTO getProductById(Integer productId) throws Shopease;
	
	void reduceAvailableQuantity(Integer productId, Integer quantity) throws Shopease;


}
