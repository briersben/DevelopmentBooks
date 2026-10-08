package com.bnpp.katas.developmentbooks.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
	@Min(1)
	private int id;
	@Min(1)
	private int quantity;
}
