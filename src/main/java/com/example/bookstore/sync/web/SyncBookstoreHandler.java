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
package com.example.bookstore.sync.web;

import static org.springframework.web.reactive.function.server.ServerResponse.*;
import static reactor.core.publisher.Mono.*;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.bookstore.AppProfiles;
import com.example.bookstore.Book;
import com.example.bookstore.BookstoreHandler;
import com.example.bookstore.Customer;
import com.example.bookstore.Order;
import com.example.bookstore.sync.BookRepository;
import com.example.bookstore.sync.OrderService;

/**
 * Synchronous {@link BookstoreHandler} implementation.
 *
 * @author Christoph Strobl
 */
@Component
@Profile({ AppProfiles.SYNC_ATOMIC, AppProfiles.SYNC_TRANSACTION, AppProfiles.NATIVE_SYNC_TRANSACTION })
@RequiredArgsConstructor
public class SyncBookstoreHandler implements BookstoreHandler {

	private final BookRepository bookRepository;
	private final OrderService orderService;

	@Override
	public Mono<ServerResponse> books(ServerRequest request) {
		return ok().body(Flux.fromIterable(bookRepository.findAll()), Book.class);
	}

	@Override
	public Mono<ServerResponse> book(ServerRequest request) {
		return ok().body(just(bookById(request)), Book.class);
	}

	@Override
	public Mono<ServerResponse> order(ServerRequest request) {

		Customer customer = Customer.of(request.queryParam("customer").orElse(Customer.guest().getEmail()));

		return ok().body(just(orderService.buy(customer, bookById(request))), Order.class);
	}

	private Book bookById(ServerRequest request) {

		return bookRepository.findById(request.pathVariable("book")).orElseThrow(
				() -> new RuntimeException(String.format("No book found for id %s", request.pathVariable("book"))));
	}
}
