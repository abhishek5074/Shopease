package com.shopease.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopease.entity.Card;

public interface CardRepository extends CrudRepository<Card, Integer>{

	List<Card> findByCustomerEmailId(String customerEmailId);
	
	List<Card> findByCustomerEmailIdAndCardType(String cutomerEmailId, String cardType);
}
