package com.bnpp.katas.developmentbooks.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Book {
	private int id;
	private String title;
	private String author;
	private int year;
	private BigDecimal price;
	private String imageUrl;
}
