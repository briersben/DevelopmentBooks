package com.bnpp.katas.developmentbooks.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookGroup {
	private List<Integer> listOfbooks;
	private int discountPercentage;
	private double actualPrice;
	private double discount;
	private int numberOfBooks;
}
