/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.bookstore;

import lombok.Value;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

/**
 * An order as simple as it can be.
 *
 * <pre>
 * <code>
 * {
 *     "by" : "cstrobl@pivotal.io",
 *     "date" : ISODate("2018-08-27T10:11:59.853Z"),
 *     "books" : [ { "$ref" : "books", "$id" : "f430cb49" } ]
 * }
 *
 * </code>
 * </pre>
 *
 * @author Christoph Strobl
 * @see <a href="https://docs.mongodb.com/manual/tutorial/model-data-for-atomic-operations/">MongoDB - Data Model
 *      Examples and Patterns</a>
 */
@Value
public class Order {

	@Field("by") //
	String customer;
	Date date;

	@Nullable //
	@DBRef List<Book> books;

	public Order(String customer, Date date) {
		this(customer, date, null);
	}

	public Order(String customer, Date date, @Nullable List<Book> books) {

		this.customer = customer;
		this.date = date;
		this.books = books;
	}
}
