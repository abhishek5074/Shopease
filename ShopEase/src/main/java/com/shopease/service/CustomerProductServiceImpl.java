package com.shopease.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopease.dto.ProductDTO;
import com.shopease.entity.Product;
import com.shopease.repository.ProductRepository;

import jakarta.transaction.Transactional;

import com.shopease.exception.Shopease;

@Service
@Transactional
public class CustomerProductServiceImpl implements CustomerProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Override
	public List<ProductDTO> getAllProducts() throws Shopease{
		Iterable<Product> allProducts =  productRepository.findAll();
		List<ProductDTO> allProductsList = new ArrayList<>();
		allProducts.forEach( p -> {
			ProductDTO dto = ProductDTO.ProductToProductDTO(p);
			allProductsList.add(dto);
		});
		if(allProductsList.isEmpty())
			throw new Shopease("ProductService.PRODUCT_NOT_AVAILABLE=Sorry product is not available");
		return allProductsList;
	}

	@Override
	public ProductDTO getProductById(Integer productId) throws Shopease {
		Optional<Product> prodOp = productRepository.findById(productId);
		Product product = prodOp.orElseThrow(() -> new Shopease("ProductService.PRODUCT_NOT_AVAILABLE"));
		
		ProductDTO productDTO = ProductDTO.ProductToProductDTO(product);
		System.out.println(productDTO);
		return productDTO;
	}

	@Override
	public void reduceAvailableQuantity(Integer productId, Integer quantity) throws Shopease{
		Optional<Product> productOp = productRepository.findById(productId);
		Product product = productOp.orElseThrow(() -> new Shopease("ProductService.PRODUCT_NOT_AVAILABLE"));
		product.setAvailableQuantity(product.getAvailableQuantity()-quantity);
	}
	
}
