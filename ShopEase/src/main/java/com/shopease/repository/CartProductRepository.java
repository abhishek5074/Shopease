package com.shopease.repository;

import org.springframework.data.repository.CrudRepository;

import com.shopease.entity.CartProduct;

public interface CartProductRepository  extends CrudRepository<CartProduct, Integer>{

}
