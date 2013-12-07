package cz.zcu.kiv.crce.handler.metrics.internal;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

public class MetricsSignatureVisitor extends SignatureVisitor {

	private int numberOfMethodParametrs;
	
	public MetricsSignatureVisitor() {
		super(Opcodes.ASM4);

		numberOfMethodParametrs = 0;
	}

	@Override
	public void visitFormalTypeParameter(String name) {
		numberOfMethodParametrs++;	
	}
	
	public int getNumberOfMethodParametrs() {
		return numberOfMethodParametrs;
	}
}
