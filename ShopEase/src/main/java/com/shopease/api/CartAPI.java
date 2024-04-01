package com.shopease.api;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.shopease.dto.CartProductDTO;
import com.shopease.dto.CustomerCartDTO;
import com.shopease.dto.ProductDTO;
import com.shopease.exception.Shopease;
import com.shopease.service.CustomerCartService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping(value = "/cart-api")
@Validated
public class CartAPI {
	
	@Autowired
	private CustomerCartService customerCartService;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private RestClient client;
	
	Log logger = LogFactory.getLog(CartAPI.class);
	
	
	@PostMapping(value = "/products")
	public ResponseEntity<String> addProductToCart(@Valid @RequestBody CustomerCartDTO customerCardDTO) throws Shopease {
		logger.info("Received a request to add product to cart " + customerCardDTO.getCartId());
		customerCartService.addProductToCart(customerCardDTO);
		String message = environment.getProperty("CustomerCartAPI.PRODUCT_ADDED_TO_CART") + customerCardDTO.getCustomerEmailId();
		return new ResponseEntity<>(message,HttpStatus.OK);
		
	}
	
	@GetMapping(value = "/customer/{customerEmailId}/products")
	public ResponseEntity<Set<CartProductDTO>> getProductsFromCart(@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.customeremail.format}") @PathVariable("customerEmailId") String customerEmailId) throws Shopease {
		logger.info("Received a request to get products details from " + customerEmailId + " cart");
		
		Set<CartProductDTO> cartProductDTOs = customerCartService.getProductsFromCart(customerEmailId);
		System.out.println(cartProductDTOs.toString());
		for (CartProductDTO cartProductDTO : cartProductDTOs) {
			logger.info("Product call");
			ProductDTO productDTO = client.	
					get()
					.uri("http://localhost:8080/Shopease/product-api/product/"+cartProductDTO.getProduct().getProductId())
					.retrieve()
					.body(ProductDTO.class);
			cartProductDTO.setProduct(productDTO);
			
		}
		return new ResponseEntity<Set<CartProductDTO>>(cartProductDTOs,HttpStatus.OK);
	}
	
	
	@DeleteMapping(value = "/customer/{customerEmailId}/product/{productId}")
	public ResponseEntity<String> deleteProductFromCart(@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",message = "{invalid.customeremail.format}") @PathVariable("customerEmailId") String customerEmailId, @NotNull(message = "{invalid.email.format}") @PathVariable("productId") Integer productId) throws Shopease {
		logger.info("Received a request to delete the product " + productId + " from " + customerEmailId + " cart");
		customerCartService.deleteProductFromCart(customerEmailId, productId);
		String message  = environment.getProperty("CustomerCartAPI.PRODUCT_DELETED_FROM_CART_SUCCESS");
		return new ResponseEntity<>(message,HttpStatus.OK);
	}
	
	@PutMapping(value = "/customer/{customerEmailId}/product/{productId}")
	public ResponseEntity<String> modifyQuantityOfProductInCart(@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.customeremail.format}") @PathVariable("customerEmailId") String customerEmailId, @NotNull(message = "{invalid.customeremail.format") @PathVariable("productId") Integer productId, @RequestBody String quantity) throws Shopease {
		logger.info("Received a request to modify the quantity of " + productId + " prouct from  " + customerEmailId
				+ " cart to " + quantity);
		customerCartService.modifyQuantityOfProductInCart(customerEmailId,productId,Integer.parseInt(quantity));
		String message = environment.getProperty("CustomerCartAPI.PRODUCT_QUANTITY_UPDATE_FROM_CART_SUCCESS");
		return new ResponseEntity<>(message,HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/customer/{customerEmailId}/products")
	public ResponseEntity<String> deleteAllProductsFromCart(@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.customeremail.format}") @PathVariable("customerEmailId") String customerEmailId) throws Shopease {
		logger.info("Received a request to clear " + customerEmailId + " cart");
		customerCartService.deleteAllProductsFromCart(customerEmailId);
		String message = environment.getProperty("CustomerCartAPI.ALL_PRODUCTS_DELETED");
		return new ResponseEntity<>(message,HttpStatus.OK);
	}

}
