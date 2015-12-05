package net.sandius.rembulan.gen;

import net.sandius.rembulan.core.OpCode;
import net.sandius.rembulan.core.PrototypePrinter;

public abstract class Instruction {

	protected final int insn;

	public Instruction(int insn) {
		this.insn = insn;
	}

	public int getOpCode() {
		return OpCode.opCode(insn);
	}

	@Override
	public String toString() {
		return PrototypePrinter.instructionInfo(insn);
	}

	public static Instruction valueOf(int insn) {
		int oc = OpCode.opCode(insn);

		Instruction i = UnconditionalInstruction.fromInstruction(insn);
		if (i == null) {
			i = BranchInstruction.fromInstruction(insn);
		}
		if (i == null) {
			i = TerminalInstruction.fromInstruction(insn);
		}
		if (i == null) {
			throw new IllegalArgumentException("Unsupported instruction: " + insn + " (opcode = " + oc + ")");
		}

		return i;
	}

}