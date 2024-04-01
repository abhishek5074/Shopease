package com.shopease.api;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopease.dto.ProductDTO;
import com.shopease.exception.Shopease;
import com.shopease.service.CustomerProductService;

@RestController
@RequestMapping(value = "/product-api")
public class ProductAPI {
	
	@Autowired
	private CustomerProductService customerProductService;
	
	@Autowired
	private Environment environment;
	
	Log logger = LogFactory.getLog(ProductAPI.class); 
	
	@GetMapping(value = "/products")
	public ResponseEntity<List<ProductDTO>> getAllProducts() throws Shopease{
		logger.info("Received a request to get product details for all products");
		List<ProductDTO> list = customerProductService.getAllProducts();
		return new ResponseEntity<>(list,HttpStatus.OK);
	}

	@GetMapping(value = "/product/{productId}")
	public ResponseEntity<ProductDTO> getProduct(@PathVariable Integer productId) throws Shopease {
		logger.info("Received a request to get product details for product with productId as : " + productId);
		ProductDTO product = customerProductService.getProductById(productId);
		return new ResponseEntity<>(product,HttpStatus.OK);
	}
	
	@PutMapping(value = "/product/{productId}")
	public ResponseEntity<String> reduceAvailableQuantity(@PathVariable Integer productId, @RequestBody ProductDTO quantity ) throws Exception {
		logger.info("Received a reqeust to update the available quantity for product with productId as " + productId);
		customerProductService.reduceAvailableQuantity(productId, quantity.getAvailableQuantity());
		return new ResponseEntity<>(environment.getProperty("ProductAPI.REDUCE_QUANTITY_SUCCESSFULL"),HttpStatus.OK);
	}
}
