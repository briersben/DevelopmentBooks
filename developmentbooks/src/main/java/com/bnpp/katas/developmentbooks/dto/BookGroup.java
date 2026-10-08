package com.bnpp.katas.developmentbooks.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookGroup {
	private List<Integer> listOfbooks;
	private int discountPercentage;
	private BigDecimal actualPrice;
	private BigDecimal discount;
	private int numberOfBooks;
}
