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
package com.example.bookstore.sync.transaction;

import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.bookstore.AppProfiles;
import com.example.bookstore.Book;
import com.example.bookstore.BookSoldOutException;
import com.example.bookstore.Customer;
import com.example.bookstore.Order;
import com.example.bookstore.sync.OrderRepository;
import com.example.bookstore.sync.OrderService;
import com.mongodb.client.result.UpdateResult;

/**
 * Synchronous {@link OrderService} implementation using transactions.
 *
 * @author Christoph Strobl
 */
@Service
@Qualifier("transactional-order-service")
@Profile({ AppProfiles.SYNC_TRANSACTION })
@RequiredArgsConstructor
public class TransactionalOrderService implements OrderService {

	private final OrderRepository orderRepository;
	private final MongoOperations mongoOps;
	private final MongoTransactionManager txManager;

	@Override
	public Order buy(Customer customer, Book book) {

		TransactionTemplate tt = new TransactionTemplate(txManager);

		return tt.execute(action -> {

			Order order = new Order(customer.getEmail(), new Date(), Arrays.asList(book));
			orderRepository.save(order);

			UpdateResult result = mongoOps.update(Book.class) //
					.matching(query(where("id").is(book.getId()).and("stock").gt(0))) //
					.apply(new Update().inc("stock", -1)) //
					.first();

			if (result.getModifiedCount() != 1) {
				throw new BookSoldOutException(book);
			}

			return order;
		});
	}
}
