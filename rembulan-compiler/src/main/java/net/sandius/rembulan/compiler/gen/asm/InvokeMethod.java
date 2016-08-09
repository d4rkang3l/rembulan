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

package net.sandius.rembulan.compiler.gen.asm;

import net.sandius.rembulan.compiler.gen.asm.helpers.ASMUtils;
import net.sandius.rembulan.compiler.gen.asm.helpers.UtilMethods;
import net.sandius.rembulan.compiler.gen.asm.helpers.VariableMethods;
import net.sandius.rembulan.compiler.ir.Var;
import net.sandius.rembulan.core.ExecutionContext;
import net.sandius.rembulan.core.Variable;
import net.sandius.rembulan.util.Check;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.RETURN;

class InvokeMethod {

	private final ASMBytecodeEmitter context;
	private final RunMethod runMethod;

	public InvokeMethod(ASMBytecodeEmitter context, RunMethod runMethod) {
		this.context = Check.notNull(context);
		this.runMethod = Check.notNull(runMethod);
	}

	public MethodNode methodNode() {
		MethodNode node = new MethodNode(
				ACC_PUBLIC,
				"invoke",
				context.invokeMethodType().getDescriptor(),
				null,
				runMethod.throwsExceptions());

		InsnList il = node.instructions;
		List<LocalVariableNode> locals = node.localVariables;

		LabelNode begin = new LabelNode();
		LabelNode end = new LabelNode();

		int invokeKind = context.kind();

		il.add(begin);

		il.add(new VarInsnNode(ALOAD, 0));  // this
		il.add(new VarInsnNode(ALOAD, 1));  // context
		il.add(ASMUtils.loadInt(0));  // resumption point

		// a (slotIdx -> paramIdx) map
		int[] slotParamMap = new int[context.slots.numSlots()];
		Arrays.fill(slotParamMap, -1);

		for (int paramIdx = 0; paramIdx < context.fn.params().size(); paramIdx++) {
			int slotIdx = context.slots.slotOf(context.fn.params().get(paramIdx));
			assert (slotParamMap[slotIdx] == -1);
			slotParamMap[slotIdx] = paramIdx;
		}

		if (invokeKind > 0) {
			// we have (invokeKind - 1) standalone parameters, mapping them onto numSlots

			for (int paramIdx : slotParamMap) {
				if (paramIdx < 0) {
					// slot unused
					il.add(new InsnNode(ACONST_NULL));
				}
				else {
					// used by the parameter #paramIdx
					Var param = context.fn.params().get(paramIdx);
					boolean reified = context.types.isReified(param);

					if (reified) {
						il.add(new TypeInsnNode(NEW, Type.getInternalName(Variable.class)));
						il.add(new InsnNode(DUP));
					}

					il.add(new VarInsnNode(ALOAD, 2 + paramIdx));

					if (reified) {
						il.add(VariableMethods.constructor());
					}
				}
			}
		}
		else {
			// variable number of parameters, encoded in an array at position 2

			if (context.isVararg()) {
				il.add(new VarInsnNode(ALOAD, 2));
				il.add(UtilMethods.arrayFrom(context.numOfParameters()));
			}

			// load #numOfParameters, mapping them onto #numOfRegisters

			for (int paramIdx : slotParamMap) {
				if (paramIdx < 0) {
					// slot unused
					il.add(new InsnNode(ACONST_NULL));
				}
				else {
					// used by the parameter #paramIdx
					Var param = context.fn.params().get(paramIdx);
					boolean reified = context.types.isReified(param);

					if (reified) {
						il.add(new TypeInsnNode(NEW, Type.getInternalName(Variable.class)));
						il.add(new InsnNode(DUP));
					}

					il.add(new VarInsnNode(ALOAD, 2));  // TODO: use dup instead?
					il.add(UtilMethods.getArrayElementOrNull(paramIdx));

					if (reified) {
						il.add(VariableMethods.constructor());
					}
				}
			}

		}

		il.add(runMethod.methodInvokeInsn());

		il.add(new InsnNode(RETURN));
		il.add(end);

		locals.add(new LocalVariableNode("this", context.thisClassType().getDescriptor(), null, begin, end, 0));
		locals.add(new LocalVariableNode("context", Type.getDescriptor(ExecutionContext.class), null, begin, end, 1));
		if (invokeKind < 0) {
			locals.add(new LocalVariableNode("args", ASMUtils.arrayTypeFor(Object.class).getDescriptor(), null, begin, end, 2));

			// TODO: maxLocals, maxStack
		}
		else {
			for (int i = 0; i < invokeKind; i++) {
				locals.add(new LocalVariableNode("arg_" + i, Type.getDescriptor(Object.class), null, begin, end, 2 + i));
			}

			// TODO: maxLocals, maxStack
			node.maxLocals = 2 + invokeKind;
			node.maxStack = 4 + runMethod.numOfRegisters();
		}

		return node;
	}

}
