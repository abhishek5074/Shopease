package com.shopease.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopease.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{

}
