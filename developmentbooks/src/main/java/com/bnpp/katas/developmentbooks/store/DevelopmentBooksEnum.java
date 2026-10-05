package com.bnpp.katas.developmentbooks.store;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DevelopmentBooksEnum {

	CLEAN_CODE(1, "Clean Code", "Robert Martin", 2008, 50.00,
			"https://github.com/stephane-genicot/katas/raw/master/images/Kata_DevelopmentBooks_CleanCode.png"),
	THE_CLEAN_CODER(2, "The Clean Coder", "Robert Martin", 2011, 50.00,
			"https://github.com/stephane-genicot/katas/raw/master/images/Kata_DevelopmentBooks_CleanCoder.png"),
	CLEAN_ARCHITECTURE(3, "Clean Architecture", "Robert Martin", 2017, 50.00,
			"https://github.com/stephane-genicot/katas/raw/master/images/Kata_DevelopmentBooks_CleanArchitecture.jpeg"),
	TEST_DRIVEN_DEVELOPMENT(4, "Test-Driven Development By Example", "Kent Beck", 2003, 50.00,
			"https://github.com/stephane-genicot/katas/raw/master/images/Kata_DevelopmentBooks_TDD.jpeg"),
	WORKING_WITH_LEGACY_CODE(5, "Working Effectively With Legacy Code", "Michael C. Feathers", 2004, 50.00,
			"https://github.com/stephane-genicot/katas/raw/master/images/Kata_DevelopmentBooks_Refactoring.jpeg");

	private int id;
	private String title;
	private String author;
	private int year;
	private double price;
	private String imageUrl;

}