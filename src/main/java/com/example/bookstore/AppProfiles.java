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

/**
 * Just a simple collection of supported {@link org.springframework.context.annotation.Profile} values used for
 * configuration.
 *
 * @author Christoph Strobl
 */
public final class AppProfiles {

	/**
	 * Spring {@link org.springframework.context.annotation.Profile} value to reset the application to its initial state.
	 */
	public static final String RESET = "reset";

	/**
	 * Spring {@link org.springframework.context.annotation.Profile} to be used for the synchronous atomic
	 * {@link org.bson.Document} update sample.
	 */
	public static final String SYNC_ATOMIC = "sa";

	/**
	 * Spring {@link org.springframework.context.annotation.Profile} to be used for the
	 * {@link com.mongodb.client.MongoClient} synchronous transactional sample.
	 */
	public static final String NATIVE_SYNC_TRANSACTION = "stxn";

	/**
	 * Spring {@link org.springframework.context.annotation.Profile} to be used for the synchronous transactional sample.
	 */
	public static final String SYNC_TRANSACTION = "stx";

	/**
	 * Spring {@link org.springframework.context.annotation.Profile} to be used for the reactive transactional sample.
	 */
	public static final String REACTIVE_TRANSACTION = "rtx";

	/**
	 * Spring {@link org.springframework.context.annotation.Profile} to activate retry.
	 */
	public static final String RETRYABLE_TRANSACTION = "retry";

	/**
	 * Spring {@link org.springframework.context.annotation.Profile} to be used to demonstrate change streams.
	 */
	public static final String REACTIVE_CHANGESTREAMS = "rcs";

	private AppProfiles() { /* u can't touch this */}

}
