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

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.bookstore.util.ConsoleOutMongoDBCommandListener;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;

/**
 * @author Christoph Strobl
 */
@SpringBootApplication
public class FantasyBookstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(FantasyBookstoreApplication.class, args);
	}

	/**
	 * The default configuration to apply no matter what.
	 */
	@Configuration
	class DefaultConfiguration {

		@Autowired MongoTemplate template;

		@Bean
		com.mongodb.client.MongoClient mongoClient() {

			MongoClientSettings settings = MongoClientSettings.builder() //
					.addCommandListener(ConsoleOutMongoDBCommandListener.INSTANCE) //
					.build();

			return MongoClients.create(settings);
		}

		/**
		 * {@link RouterFunction Routes} to interact with the MongoDB Bookstore Application delegating the processing of
		 * {@link org.springframework.web.reactive.function.server.ServerRequest requests} to the {@link BookstoreHandler}.
		 *
		 * @param handler
		 * @return
		 */
		@Bean
		RouterFunction<ServerResponse> routerFunction(BookstoreHandler handler) {

			return RouterFunctions.route(GET("/books"), handler::books) //
					.andRoute(GET("/book/{book}"), handler::book) //
					.andRoute(POST("/book/{book}/order"), handler::order);
		}

		/**
		 * Just make sure to have everything at hand so we do not run into issues when accessing (potentially non existing)
		 * collections in a transaction.
		 */
		@PostConstruct
		public void init() {

			if (!template.collectionExists(Order.class)) {
				template.createCollection(Order.class);
			}
			if (!template.collectionExists(Book.class)) {
				template.createCollection(Book.class);
			}
		}
	}

	/**
	 * Additional configuration for: synchronous atomic document update.
	 */
	@Configuration
	@Profile(AppProfiles.SYNC_ATOMIC)
	class SyncConfiguration {

	}

	/**
	 * Additional configuration for: synchronous transactions plainly using the {@link com.mongodb.client.MongoClient}.
	 */
	@Configuration
	@Profile(AppProfiles.NATIVE_SYNC_TRANSACTION)
	class SyncNativeMongoTransactionConfiguration {

	}

	/**
	 * Additional configuration for: synchronous Spring managed transactions.
	 */
	@Configuration
	@Profile(AppProfiles.SYNC_TRANSACTION)
	class SyncSpringTransactionConfiguration {

		@Bean
		MongoTransactionManager txManager(MongoDbFactory dbFactory) {
			return new MongoTransactionManager(dbFactory);
		}
	}

	/**
	 * Additional configuration for: reactive transactions.
	 */
	@Configuration
	@Profile(AppProfiles.REACTIVE_TRANSACTION)
	class ReactiveTransactionConfiguration {

		@Bean
		com.mongodb.reactivestreams.client.MongoClient reactiveMongoClient() {

			MongoClientSettings settings = MongoClientSettings.builder() //
					.addCommandListener(ConsoleOutMongoDBCommandListener.INSTANCE) //
					.build();

			return com.mongodb.reactivestreams.client.MongoClients.create(settings);
		}
	}

	/**
	 * Additional configuration for: Change Streams
	 */
	@Configuration
	@Profile(AppProfiles.REACTIVE_CHANGESTREAMS)
	class ReactiveChangeStreamConfiguration {

		@Value("${spring.data.mongodb.database}") String database;

		@PostConstruct
		public void init() {

			new ReactiveMongoTemplate(com.mongodb.reactivestreams.client.MongoClients.create(), database) //
					.changeStream("order", ChangeStreamOptions.empty(), Order.class) //
					.doOnNext(System.out::println) //
					.subscribe();
		}
	}

	/**
	 * Additional configuration for: Retry on write conflict
	 */
	@Configuration
	@Profile(AppProfiles.RETRYABLE_TRANSACTION)
	@EnableRetry
	class RetryableTransactionConfiguration {

	}
}
