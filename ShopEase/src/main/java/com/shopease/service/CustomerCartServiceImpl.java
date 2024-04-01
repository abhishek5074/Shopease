package com.shopease.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopease.dto.CartProductDTO;
import com.shopease.dto.CustomerCartDTO;
import com.shopease.dto.ProductDTO;
import com.shopease.entity.CartProduct;
import com.shopease.entity.CustomerCart;
import com.shopease.exception.Shopease;
import com.shopease.repository.CartProductRepository;
import com.shopease.repository.CustomerCartRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CustomerCartServiceImpl implements CustomerCartService {
	
	@Autowired
	private CustomerCartRepository customerCartRepository;
	
	@Autowired
	private CartProductRepository cartProductRepository;


	@Override
	public Integer addProductToCart(CustomerCartDTO customerCartDTO) throws Shopease {
		Set<CartProduct> cartProducts = new HashSet<>();
		Integer cartId = null;
		for(CartProductDTO cartProductDTO : customerCartDTO.getCartProducts()) {
			CartProduct cartProduct = new CartProduct();
			cartProduct.setProductId(cartProductDTO.getProduct().getProductId());
			cartProduct.setQuantity(cartProductDTO.getQuantity());
			cartProducts.add(cartProduct);
		}
		Optional<CustomerCart> cartOptional = customerCartRepository.findByCustomerEmailId(customerCartDTO.getCustomerEmailId());
		if(cartOptional.isEmpty()) {
			CustomerCart newCart = new CustomerCart();
			newCart.setCustomerEmailId(customerCartDTO.getCustomerEmailId());
			newCart.setCartProducts(cartProducts);
			customerCartRepository.save(newCart);
			cartId = newCart.getCartId();
		} else {
			CustomerCart cart = cartOptional.get();
			for( CartProduct cartProductToBeAdded : cartProducts) {
				Boolean found = false;
				for (CartProduct cartProductFromCart : cart.getCartProducts()) {
					if(cartProductFromCart.equals(cartProductToBeAdded)) {
						cartProductFromCart.setQuantity(cartProductToBeAdded.getQuantity() + cartProductFromCart.getQuantity());
						found = true;
					}
				}
				if(found == false) {
					cart.getCartProducts().add(cartProductToBeAdded);
				}
			}
			cartId = cart.getCartId();
		}
		
		return cartId;
	}

	@Override
	public Set<CartProductDTO> getProductsFromCart(String customerEmailId) throws Shopease {
		Optional<CustomerCart> cartOptional = customerCartRepository.findByCustomerEmailId(customerEmailId);
		CustomerCart cart = cartOptional.orElseThrow(() -> new Shopease("CustomerCartService.NO_CART_FOUND"));
		
		Set<CartProductDTO> productsDTO = new HashSet<>();
		
		cart.getCartProducts().forEach(prod -> {
			CartProductDTO dto = new CartProductDTO();
			dto.setCartProductId(prod.getCartProductId());
			dto.setQuantity(prod.getQuantity());
			
			ProductDTO prodDTO = new  ProductDTO();
			prodDTO.setProductId(prod.getProductId());
			dto.setProduct(prodDTO);
			
			productsDTO.add(dto);
		});
		if(productsDTO.isEmpty())
			throw new Shopease("CustomerCartService.NO_PRODUCT_ADDED_TO_CART");
		
		return productsDTO;
		
	}
	
	@Override
	public void modifyQuantityOfProductInCart(String customerEmailId, Integer productId, Integer quantity)
			throws Shopease {
		Optional<CustomerCart> cartOptional = customerCartRepository.findByCustomerEmailId(customerEmailId);
		CustomerCart cart = cartOptional.orElseThrow(() -> new Shopease("CustomerCartService.NO_CART_FOUND"));
		
		if(cart.getCartProducts().isEmpty()) {
			throw new Shopease("CustomerCartService.NO_PRODUCT_ADDED_TO_CART");
		}
		CartProduct selectedProduct = null;
		for(CartProduct product : cart.getCartProducts()) {
			if(product.getProductId().equals(productId)) {
				selectedProduct = product;
			}
		}
		if(selectedProduct == null) {
			throw new Shopease("CustomerCartService.PRODUCT_ALREADY_NOT_AVAILABLE");
		}
		selectedProduct.setQuantity(quantity);
	}

	@Override
	public void deleteProductFromCart(String customerEmailId, Integer productId) throws Shopease {
		Optional<CustomerCart> cartOptional = customerCartRepository.findByCustomerEmailId(customerEmailId);
		CustomerCart cart = cartOptional.orElseThrow(() -> new Shopease("CustomerCartService.NO_CART_FOUND"));
		int cartProductId = -1;
		if(cart.getCartProducts().isEmpty())
			throw new Shopease("CustomerCartService.NO_PRODUCT_ADDED_TO_CART");
		
		Iterator<CartProduct> iterator = cart.getCartProducts().iterator();
		while (iterator.hasNext()) {
		    CartProduct cp = iterator.next();
		    if (productId.equals(cp.getProductId())) {
		        iterator.remove();
		        cartProductId = cp.getCartProductId();
		    }
		}
		cart.getCartProducts().stream().forEach(System.out::println);
		if(cartProductId != -1)
			cartProductRepository.deleteById(cartProductId);
		
	}

	@Override
	public void deleteAllProductsFromCart(String customerEmailId) throws Shopease {
		Optional<CustomerCart> cartOptional = customerCartRepository.findByCustomerEmailId(customerEmailId);
		CustomerCart cart = cartOptional.orElseThrow(() -> new Shopease("CustomerCartService.NO_CART_FOUND"));
		
		if(cart.getCartProducts().isEmpty()) {
			throw new Shopease("CustomerCartService.NO_PRODUCT_ADDED_TO_CART");
		}
		List<Integer> productIds = new ArrayList<>();
		cart.getCartProducts().parallelStream().forEach(cp -> {
			productIds.add(cp.getCartProductId());
			cart.getCartProducts().remove(cp);
		});
		
		productIds.forEach(pid -> {
			cartProductRepository.deleteById(pid);
		});
	}

}
