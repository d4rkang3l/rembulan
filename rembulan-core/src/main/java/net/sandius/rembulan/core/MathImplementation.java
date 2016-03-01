package net.sandius.rembulan.core;

import static net.sandius.rembulan.core.RawOperators.*;

public abstract class MathImplementation {

	public static final IntegerMathImplementation INTEGER_MATH = new IntegerMathImplementation();
	public static final FloatMathImplementation FLOAT_MATH = new FloatMathImplementation();

	public abstract Number do_add(Number a, Number b);
	public abstract Number do_sub(Number a, Number b);

	public static class IntegerMathImplementation extends MathImplementation {

		@Override
		public Long do_add(Number a, Number b) {
			return rawadd(a.longValue(), b.longValue());
		}

		@Override
		public Long do_sub(Number a, Number b) {
			return rawsub(a.longValue(), b.longValue());
		}

	}

	public static class FloatMathImplementation extends MathImplementation {

		@Override
		public Double do_add(Number a, Number b) {
			return rawadd(a.doubleValue(), b.doubleValue());
		}

		@Override
		public Double do_sub(Number a, Number b) {
			return rawsub(a.doubleValue(), b.doubleValue());
		}

	}

	public static MathImplementation arithmetic(Object a, Object b) {
		if (a instanceof Number && b instanceof Number) {
			if ((a instanceof Double || a instanceof Float)
					|| (b instanceof Double || b instanceof Float)) {
				return FLOAT_MATH;
			}
			else {
				return INTEGER_MATH;
			}
		}
		else if (a instanceof String || b instanceof String) {
			return arithmetic(Conversions.objectAsNumber(a), Conversions.objectAsNumber(b));
		}
		else {
			return null;
		}
	}

}
