package net.sandius.rembulan.core;

import net.sandius.rembulan.util.Check;

public class Preempted extends ControlThrowable {

//	public static final Preempted INSTANCE = new Preempted();

	private CallInfo[] callStack;
	private int top;

	public Preempted() {
		callStack = new CallInfo[10];
		top = 0;
	}

	public static Preempted newInstance() {
		return new Preempted();
	}

	@Override
	public void push(CallInfo ci) {
		Check.notNull(ci);

		if (top >= callStack.length) {
			CallInfo[] newCallStack = new CallInfo[callStack.length * 2];
			System.arraycopy(callStack, 0, newCallStack, 0, top);
			callStack = newCallStack;
		}

		callStack[top++] = ci;
	}

}
