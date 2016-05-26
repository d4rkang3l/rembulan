package net.sandius.rembulan.lib.impl;

import net.sandius.rembulan.core.ControlThrowable;
import net.sandius.rembulan.core.Conversions;
import net.sandius.rembulan.core.ExecutionContext;
import net.sandius.rembulan.core.Function;
import net.sandius.rembulan.core.IllegalOperationAttemptException;
import net.sandius.rembulan.core.RawOperators;
import net.sandius.rembulan.lib.StringLib;

public class DefaultStringLib extends StringLib {

	@Override
	public Function _byte() {
		return Byte.INSTANCE;
	}

	@Override
	public Function _char() {
		return Char.INSTANCE;
	}

	@Override
	public Function _dump() {
		return Dump.INSTANCE;
	}

	@Override
	public Function _find() {
		return Find.INSTANCE;
	}

	@Override
	public Function _format() {
		return null;  // TODO
	}

	@Override
	public Function _gmatch() {
		return null;  // TODO
	}

	@Override
	public Function _gsub() {
		return null;  // TODO
	}

	@Override
	public Function _len() {
		return Len.INSTANCE;
	}

	@Override
	public Function _lower() {
		return Lower.INSTANCE;
	}

	@Override
	public Function _match() {
		return null;  // TODO
	}

	@Override
	public Function _pack() {
		return null;  // TODO
	}

	@Override
	public Function _packsize() {
		return null;  // TODO
	}

	@Override
	public Function _rep() {
		return null;  // TODO
	}

	@Override
	public Function _reverse() {
		return Reverse.INSTANCE;
	}

	@Override
	public Function _sub() {
		return Sub.INSTANCE;
	}

	@Override
	public Function _unpack() {
		return null;  // TODO
	}

	@Override
	public Function _upper() {
		return Upper.INSTANCE;
	}

	private static int correctIndex(int i, int len) {
		if (i < 0) {
			i = len - i;
		}

		if (i < 1) {
			i = 1;
		}

		if (i > len) {
			i = len;
		}

		return i;
	}

	public static class Byte extends LibFunction {

		public static final Byte INSTANCE = new Byte();

		@Override
		protected String name() {
			return "byte";
		}

		@Override
		protected void invoke(ExecutionContext context, CallArguments args) throws ControlThrowable {
			String s = args.nextString();
			int i = args.optNextInt(1);
			int j = args.optNextInt(i);

			int len = RawOperators.stringLen(s);

			// correct indices
			i = correctIndex(i, len);
			j = correctIndex(j, len);

			context.getObjectSink().reset();

			for (int idx = i; idx <= j; idx++) {
				// FIXME: these are not bytes!
				char c = s.charAt(idx - 1);
				context.getObjectSink().push((long) c);
			}
		}

	}

	public static class Char extends LibFunction {

		public static final Char INSTANCE = new Char();

		@Override
		protected String name() {
			return "char";
		}

		@Override
		protected void invoke(ExecutionContext context, CallArguments args) throws ControlThrowable {
			char[] chars = new char[args.size()];

			for (int i = 0; i < chars.length; i++) {
				chars[i] = (char) args.nextInt();
			}

			String s = String.valueOf(chars);
			context.getObjectSink().setTo(s);
		}

	}

	public static class Dump extends LibFunction {

		public static final Dump INSTANCE = new Dump();

		@Override
		protected String name() {
			return "dump";
		}

		@Override
		protected void invoke(ExecutionContext context, CallArguments args) throws ControlThrowable {
			Function f = args.nextFunction();
			boolean strip = Conversions.objectToBoolean(args.optNextAny());

			throw new IllegalOperationAttemptException("unable to dump given function");
		}

	}

	public static class Find extends LibFunction {

		public static final Find INSTANCE = new Find();

		@Override
		protected String name() {
			return "find";
		}

		@Override
		protected void invoke(ExecutionContext context, CallArguments args) throws ControlThrowable {
			String s = args.nextString();
			String pattern = args.nextString();
			int init = args.optNextInt(1);
			boolean plain = args.optNextBoolean(false);

			init = correctIndex(init, s.length());

			if (plain) {
				// find a substring
				int at = s.indexOf(pattern, init - 1);
				if (at >= 0) {
					context.getObjectSink().setTo(at + 1, at + pattern.length());
				}
				else {
					context.getObjectSink().setTo(null);
				}
			}
			else {
				// find a pattern
				throw new UnsupportedOperationException("not implemented");  // TODO
			}
		}

	}


	public static class Len extends LibFunction {

		public static final Len INSTANCE = new Len();

		@Override
		protected String name() {
			return "len";
		}

		@Override
		protected void invoke(ExecutionContext context, CallArguments args) throws ControlThrowable {
			String s = args.nextString();
			context.getObjectSink().setTo((long) s.length());
		}

	}

	public static class Lower extends LibFunction {

		public static final Lower INSTANCE = new Lower();

		@Override
		protected String name() {
			return "lower";
		}

		@Override
		protected void invoke(ExecutionContext context, CallArguments args) throws ControlThrowable {
			String s = args.nextString();
			context.getObjectSink().setTo(s.toLowerCase());
		}

	}

	public static class Reverse extends LibFunction {

		public static final Reverse INSTANCE = new Reverse();

		@Override
		protected String name() {
			return "reverse";
		}

		@Override
		protected void invoke(ExecutionContext context, CallArguments args) throws ControlThrowable {
			String s = args.nextString();

			int len = s.length();
			char[] chars = new char[len];

			for (int i = 0; i < chars.length; i++) {
				chars[i] = s.charAt(len - 1 - i);
			}

			String result = String.valueOf(chars);

			context.getObjectSink().setTo(result);
		}

	}

	public static class Sub extends LibFunction {

		public static final Sub INSTANCE = new Sub();

		@Override
		protected String name() {
			return "sub";
		}

		@Override
		protected void invoke(ExecutionContext context, CallArguments args) throws ControlThrowable {
			String s = args.nextString();
			int i = args.nextInt();
			int j = args.optNextInt(-1);

			int len = s.length();
			i = correctIndex(i, len) - 1;
			j = correctIndex(j, len);

			String result = s.substring(i, j);

			context.getObjectSink().setTo(result);
		}

	}

	public static class Upper extends LibFunction {

		public static final Upper INSTANCE = new Upper();

		@Override
		protected String name() {
			return "upper";
		}

		@Override
		protected void invoke(ExecutionContext context, CallArguments args) throws ControlThrowable {
			String s = args.nextString();
			context.getObjectSink().setTo(s.toUpperCase());
		}

	}

}
