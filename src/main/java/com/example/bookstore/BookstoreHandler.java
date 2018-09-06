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

import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Web request handler for webflux.
 *
 * @author Christoph Strobl
 */
public interface BookstoreHandler {

	/**
	 * Process a {@link ServerRequest} for a list of {@link Book books}.
	 *
	 * @param request
	 * @return
	 */
	Mono<ServerResponse> books(ServerRequest request);

	/**
	 * Process a {@link ServerRequest} for a single {@link Book}.
	 *
	 * @param request
	 * @return
	 */
	Mono<ServerResponse> book(ServerRequest request);

	/**
	 * Process a {@link ServerRequest} to {@link Order order} a specific {@link Book}.
	 *
	 * @param request
	 * @return
	 */
	Mono<ServerResponse> order(ServerRequest request);
}
