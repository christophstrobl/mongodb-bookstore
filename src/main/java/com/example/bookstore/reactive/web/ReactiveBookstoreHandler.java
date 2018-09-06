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
package com.example.bookstore.reactive.web;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

import lombok.RequiredArgsConstructor;
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
import com.example.bookstore.reactive.transaction.ReactiveBookRepository;
import com.example.bookstore.reactive.transaction.ReactiveOrderService;

/**
 * Reactive {@link BookstoreHandler} implementation.
 *
 * @author Christoph Strobl
 */
@Component
@Profile({ AppProfiles.REACTIVE_TRANSACTION })
@RequiredArgsConstructor
public class ReactiveBookstoreHandler implements BookstoreHandler {

	private final ReactiveBookRepository bookRepository;
	private final ReactiveOrderService orderService;

	@Override
	public Mono<ServerResponse> books(ServerRequest request) {

		return ok().body(bookRepository.findAll(), Book.class);
	}

	@Override
	public Mono<ServerResponse> book(ServerRequest request) {
		return ok().body(bookRepository.findById(request.pathVariable("book")), Book.class);
	}

	@Override
	public Mono<ServerResponse> order(ServerRequest request) {

		Customer customer = Customer.of(request.queryParam("customer").orElse(Customer.guest().getEmail()));

		return ok() //
				.body(bookRepository.findById(request.pathVariable("book")) //
						.flatMap(book -> orderService.buy(customer, book)), Order.class);
	}
}
