/*
 * Copyright 2016 Miroslav Janíček
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sandius.rembulan.util;

public class Ptr<T> {

	private T value;

	public Ptr(T value) {
		this.value = value;
	}

	public Ptr() {
		this(null);
	}

	public boolean isNull() {
		return value == null;
	}

	public T get() {
		return value;
	}

	public T getAndClear() {
		T v = value;
		value = null;
		return v;
	}

	public void set(T value) {
		this.value = value;
	}

	public void clear() {
		value = null;
	}

}
