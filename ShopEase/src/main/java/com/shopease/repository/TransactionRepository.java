package com.shopease.repository;

import org.springframework.data.repository.CrudRepository;

import com.shopease.entity.Transaction;

public interface TransactionRepository  extends CrudRepository<Transaction, Integer>{

}
