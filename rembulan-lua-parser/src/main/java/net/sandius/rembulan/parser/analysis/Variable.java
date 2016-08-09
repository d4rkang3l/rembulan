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

package net.sandius.rembulan.parser.analysis;

import net.sandius.rembulan.parser.ast.Name;
import net.sandius.rembulan.util.Check;

public class Variable {

	public static final Variable ENV = new Variable(Name.fromString("_ENV"));

	private final Name name;
	private final Ref ref;

	public Variable(Name name) {
		this.name = Check.notNull(name);
		this.ref = new Ref(this);
	}

	public Name name() {
		return name;
	}

	public Ref ref() {
		return ref;
	}

	public static class Ref {

		private final Variable var;

		public Ref(Variable var) {
			this.var = Check.notNull(var);
		}

		public Variable var() {
			return var;
		}

	}

}
