package net.sandius.rembulan.lbc.recompiler.gen;

import net.sandius.rembulan.lbc.Prototype;
import net.sandius.rembulan.lbc.recompiler.gen.block.AccountingNode;
import net.sandius.rembulan.lbc.recompiler.gen.block.Branch;
import net.sandius.rembulan.lbc.recompiler.gen.block.Exit;
import net.sandius.rembulan.lbc.recompiler.gen.block.LineInfo;
import net.sandius.rembulan.lbc.recompiler.gen.block.Linear;
import net.sandius.rembulan.lbc.recompiler.gen.block.NodeAppender;
import net.sandius.rembulan.lbc.recompiler.gen.block.Target;
import net.sandius.rembulan.util.Check;
import net.sandius.rembulan.util.ReadOnlyArray;

public class LuaInstructionToNodeTranslator {

	private final Prototype prototype;
	private final ReadOnlyArray<Target> pcToLabel;

	private final PrototypeContext ctx;
	
	public LuaInstructionToNodeTranslator(Prototype prototype, ReadOnlyArray<Target> pcToLabel, PrototypeContext ctx) {
		this.prototype = Check.notNull(prototype);
		this.pcToLabel = Check.notNull(pcToLabel);
		this.ctx = Check.notNull(ctx);
	}
	
	public class MyNodeAppender {
		private final int pc;
		private final NodeAppender appender;

		public MyNodeAppender(int pc) {
			this.appender = new NodeAppender(pcToLabel.get(pc));
			this.pc = pc;
		}

		public MyNodeAppender append(Linear lin) {
			appender.append(lin);
			return this;
		}

		public PrototypeContext context() {
			return ctx;
		}

		public void branch(Branch branch) {
			appender.branch(branch);
		}

		public void term(Exit term) {
			appender.append(new AccountingNode.End()).term(term);
		}

//		public void jumpTo(int dest) {
//			appender.jumpTo(target(pc, dest));
//		}

		public Target target(int offset) {
			// offset == 0 for empty for-loops
//			if (offset == 0) {
//				throw new IllegalArgumentException();
//			}

			Target jmpTarget = pcToLabel.get(pc + offset);

			if (offset < 0) {
				// this is a backward jump

				Target tgt = new Target();
				NodeAppender appender = new NodeAppender(tgt);
				appender.append(new AccountingNode.Flush())
						.jumpTo(jmpTarget);

				return tgt;
			}
			else {
				return jmpTarget;
			}
		}

		public void jumpToOffset(int offset) {
			appender.jumpTo(target(offset));
		}

		public void toNext() {
			jumpToOffset(1);
		}

	}

	public void translate(int pc) {
		MyNodeAppender appender = new MyNodeAppender(pc);

		int line = prototype.getLineAtPC(pc);

		if (line > 0) {
			appender.append(new LineInfo(line));
		}

		appender.append(new AccountingNode.TickBefore());

		int insn = prototype.getCode().get(pc);
		new LuaInstructionDispatcher(new AppenderEmitter(prototype, appender)).dispatch(insn);
	}

}