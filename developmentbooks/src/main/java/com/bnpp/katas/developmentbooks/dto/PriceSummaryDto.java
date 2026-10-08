package com.bnpp.katas.developmentbooks.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceSummaryDto {
	private List<BookGroup> listOfBookGroups;
	private BigDecimal actualPrice = new BigDecimal("0.00");
	private BigDecimal totalDiscount = new BigDecimal("0.00");
	private BigDecimal finalPrice = new BigDecimal("0.00");
}
