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
package com.example.bookstore.util;

import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.bson.Document;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.example.bookstore.AppProfiles;
import com.example.bookstore.Book;
import com.example.bookstore.Order;
import com.mongodb.client.MongoCollection;

/**
 * A simple component that resets the test data.
 *
 * @author Christoph Strobl
 */
@Component
@Profile(AppProfiles.RESET)
@RequiredArgsConstructor
class TestDataLoader {

	private final MongoTemplate template;

	void resetTestDataFor(Class type) throws Exception {
		resetTestDataFor(template.getCollectionName(type));
	}

	void resetTestDataFor(String collection) throws Exception {

		if (template.collectionExists(collection)) {
			template.getCollection(collection).deleteMany(new Document());
		} else {
			template.createCollection(collection);
		}

		Resource dataFile = new ClassPathResource(collection + ".json");

		if (dataFile.exists()) {

			List<Map> objects = new JacksonJsonParser() //
					.parseList(StreamUtils.copyToString(dataFile.getInputStream(), StandardCharsets.UTF_8)).stream()
					.map(Map.class::cast).collect(Collectors.toList());

			MongoCollection<Document> mongoCollection = template.getCollection(collection);

			objects.forEach(it -> mongoCollection.insertOne(new Document((Map) it)));
		}
	}

	@PostConstruct
	public void init() throws Exception {

		resetTestDataFor(Book.class);
		resetTestDataFor(Order.class);
	}
}
