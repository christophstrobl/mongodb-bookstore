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

import org.springframework.data.annotation.Id;

/**
 * @author Christoph Strobl
 */
@Value
public class Customer {

	@Id String email;

	public static Customer of(String alias) {

		switch (alias) {

			case "christoph":
				return Customer.christoph();
			case "jeff":
				return Customer.jeff();
			case "oliver":
				return Customer.oliver();
			default:
				return new Customer(alias);
		}
	}

	public static Customer christoph() {
		return new Customer("cstrobl@pivotal.io");
	}

	public static Customer jeff() {
		return new Customer("jeff.yemin@mongodb.com");
	}

	public static Customer oliver() {
		return new Customer("ogierke@pivotal.io");
	}

	public static Customer guest() {
		return new Customer("guest@fantasy-bookstore.io");
	}

}
