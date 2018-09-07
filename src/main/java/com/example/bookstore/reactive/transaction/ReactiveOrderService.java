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
package com.example.bookstore.reactive.transaction;

import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.example.bookstore.AppProfiles;
import com.example.bookstore.Book;
import com.example.bookstore.BookSoldOutException;
import com.example.bookstore.Customer;
import com.example.bookstore.Order;

/**
 * Reactive OrderService using MongoDB 4.0 transactions.
 *
 * @author Christoph Strobl
 */
@Service
@Profile({ AppProfiles.REACTIVE_TRANSACTION })
@RequiredArgsConstructor
public class ReactiveOrderService {

	private final ReactiveMongoOperations mongoOperations;

	/**
	 * Place the order for a specific {@link Book}.
	 *
	 * @param customer
	 * @param book
	 * @return
	 */
	public Mono<Order> buy(Customer customer, Book book) {

		return mongoOperations.inTransaction().execute(action -> {

			return action.save(new Order(customer.getEmail(), new Date(), Arrays.asList(book)))

					.flatMap(order -> {

						return action.update(Book.class) //
								.matching(query(where("id").is(book.getId()).and("stock").gt(0))) //
								.apply(new Update().inc("stock", -1)) //
								.first() //
								.map(result -> {

									if (result.getModifiedCount() == 0) {
										throw new BookSoldOutException(book);
									}

									return order;
								});
					});
		}).next();
	}
}
