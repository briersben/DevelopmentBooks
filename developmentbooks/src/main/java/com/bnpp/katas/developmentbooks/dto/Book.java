package com.bnpp.katas.developmentbooks.dto;

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
	private double price;
	private String imageUrl;
}
