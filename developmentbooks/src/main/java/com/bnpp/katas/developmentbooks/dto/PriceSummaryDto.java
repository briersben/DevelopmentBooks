package com.bnpp.katas.developmentbooks.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceSummaryDto {
	private List<BookGroup> listOfBookGroups;
	private double actualPrice;
	private double totalDiscount;
	private double finalPrice;
}
