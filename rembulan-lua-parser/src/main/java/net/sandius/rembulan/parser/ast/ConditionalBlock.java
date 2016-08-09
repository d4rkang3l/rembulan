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

package net.sandius.rembulan.parser.ast;

import net.sandius.rembulan.util.Check;

public class ConditionalBlock {

	private final Expr condition;
	private final Block block;

	public ConditionalBlock(Expr condition, Block block) {
		this.condition = Check.notNull(condition);
		this.block = Check.notNull(block);
	}

	public Expr condition() {
		return condition;
	}

	public Block block() {
		return block;
	}

	public ConditionalBlock update(Expr condition, Block block) {
		if (this.condition.equals(condition) && this.block.equals(block)) {
			return this;
		}
		else {
			return new ConditionalBlock(condition, block);
		}
	}

}
