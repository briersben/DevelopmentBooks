package com.bnpp.katas.developmentbooks.exceptions;

import java.util.List;

public class BookNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -5764209847372890849L;

	public BookNotFoundException(List<Integer> missingBookIds) {
        super("Book id's not found : " + missingBookIds);
    }

}
