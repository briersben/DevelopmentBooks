package com.bnpp.katas.developmentbooks.store;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscountProviderEnum {
	TWO_DISTINCT_BOOKS(2, 5), THREE_DISTINCT_BOOKS(3, 10), FOUR_DISTINCT_BOOKS(4, 20), FIVE_DISTINCT_BOOKS(5, 25);

	private int numberOfDistinctItems;
	private int discountPercentage;
}