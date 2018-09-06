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

import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;

/**
 * @author Christoph Strobl
 */
public enum ConsoleOutMongoDBCommandListener implements CommandListener {

	INSTANCE;

	@Override
	public void commandStarted(CommandStartedEvent event) {

		System.out.println("\nSending Command: " + event.getCommandName());
		System.out.println(bgColored(COLOR.CYAN, "-->") + " "
				+ event.getCommand().toJson(JsonWriterSettings.builder().indent(true).outputMode(JsonMode.RELAXED).build()));
	}

	@Override
	public void commandSucceeded(CommandSucceededEvent event) {
		System.out.println(bgColored(COLOR.GREEN, "<--") + " " + event.getResponse());
	}

	@Override
	public void commandFailed(CommandFailedEvent event) {
		System.out.println(bgColored(COLOR.RED, "-X-") + " " + event.getThrowable().getMessage());
	}

	static String bgColored(COLOR bgColor, String text) {
		return colored(COLOR.DEFAULT, bgColor, text);
	}

	static String colored(COLOR fg, COLOR bg, String text) {
		return (char) 27 + "[" + fg.getFgColorCode() + ";" + bg.getBgColorCode() + "m" + text + (char) 27 + "["
				+ COLOR.DEFAULT.getFgColorCode() + ";" + COLOR.DEFAULT.getBgColorCode() + "m";
	}

	enum COLOR {

		BLACK(30), RED(31), GREEN(32), YELLOW(33), BLUE(34), MAGENTA(35), CYAN(36), WHITE(37), DEFAULT(39);

		int fgColorCode;

		COLOR(int fgColorCode) {
			this.fgColorCode = fgColorCode;
		}

		public int getFgColorCode() {
			return fgColorCode;
		}

		public int getBgColorCode() {
			return getFgColorCode() + 10;
		}
	}

}
