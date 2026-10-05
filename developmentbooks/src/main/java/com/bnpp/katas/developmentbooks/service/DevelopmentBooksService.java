package com.bnpp.katas.developmentbooks.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bnpp.katas.developmentbooks.dto.Book;
import com.bnpp.katas.developmentbooks.store.DevelopmentBooksEnum;
import com.bnpp.katas.developmentbooks.store.DiscountProviderEnum;

@Service
public class DevelopmentBooksService {

	@Autowired
	private ModelMapper modelMapper;

	public List<Book> getBooks() {
		return Arrays.stream(DevelopmentBooksEnum.values()).map(bookEnum -> modelMapper.map(bookEnum, Book.class))
				.collect(Collectors.toList());
	}

	public Map<Integer, Integer> getDiscountDetails() {
		Map<Integer, Integer> discountGroups = new HashMap<>();
		for (DiscountProviderEnum discountGroup : DiscountProviderEnum.values()) {
			discountGroups.put(discountGroup.getNumberOfDistinctItems(), discountGroup.getDiscountPercentage());
		}
		return discountGroups;
	}

}
