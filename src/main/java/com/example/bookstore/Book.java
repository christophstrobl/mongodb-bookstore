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

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A book as simple as it can be.
 *
 * <pre>
 * <code>
 * {
 *     _id: 123456789,
 *     title: "MongoDB: The Definitive Guide",
 *     author: [ "Kristina Chodorow", "Mike Dirolf" ],
 *     published_date: ISODate("2010-09-24"),
 *     pages: 216,
 *     language: "English",
 *     publisher_id: "oreilly",
 *     available: 3
 * }
 *
 * </code>
 * </pre>
 * 
 * @author Christoph Strobl
 * @see <a href="https://docs.mongodb.com/manual/tutorial/model-data-for-atomic-operations/">MongoDB - Data Model
 *      Examples and Patterns</a>
 */
@Data
@Document("books")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {

	@Id String id;
	String title;

	@Field("author") List<String> authors;

	@Field("published_date") //
	String publishDate;

	int pages;
	String language;

	@Field("publisher_id") //
	String publisherId;

	@Field("available") //
	int stock;
}
