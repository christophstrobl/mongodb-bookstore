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
package com.example.bookstore.sync.mongonative;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.example.bookstore.AppProfiles;
import com.example.bookstore.Book;
import com.example.bookstore.BookSoldOutException;
import com.example.bookstore.Customer;
import com.example.bookstore.Order;
import com.example.bookstore.sync.OrderService;
import com.mongodb.DBRef;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

/**
 * Synchronous {@link OrderService} implementation using transactions directly via {@link MongoClient}.
 *
 * @author Christoph Strobl
 */
@Service
@Qualifier("native-transactional-order-service")
@Profile({ AppProfiles.NATIVE_SYNC_TRANSACTION })
@RequiredArgsConstructor
public class NativeMongoTransactionalOrderService implements OrderService {

	private final MongoClient client;
	private @Value("${spring.data.mongodb.database}") String databaseName;

	@Override
	public Order buy(Customer customer, Book book) {

		Order order = new Order(customer.getEmail(), new Date(), Arrays.asList(book));

		MongoDatabase database = client.getDatabase(databaseName);
		try (ClientSession session = client.startSession()) {

			session.startTransaction();

			database.getCollection("order").insertOne(session, toDocument(order));

			UpdateResult result = database.getCollection("books").updateOne(session, //
					and(eq("_id", book.getId()), gt("available", 0)), //
					inc("available", -1)); //

			if (result.getModifiedCount() != 1) {
				throw new BookSoldOutException(book);
			}

			session.commitTransaction();
		}

		return order;
	}

	private static Document toDocument(Order order) {

		return new Document("by", order.getCustomer()) //
				.append("date", order.getDate()) //
				.append("books",
						order.getBooks().stream().map(it -> new DBRef("books", it.getId())).collect(Collectors.toList()));
	}
}
