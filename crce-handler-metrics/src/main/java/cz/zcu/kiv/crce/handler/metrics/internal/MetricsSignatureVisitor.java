package cz.zcu.kiv.crce.handler.metrics.internal;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

public class MetricsSignatureVisitor extends SignatureVisitor {

	private boolean visitingPrameter;
	
	private int numberOfSimpleParametrs;
	private int numberOfComplexParametrs;
	
	public MetricsSignatureVisitor() {
		super(Opcodes.ASM4);

		visitingPrameter = false;
		
		numberOfSimpleParametrs = 0;
		numberOfComplexParametrs = 0;
	}

	@Override
	public SignatureVisitor visitParameterType() {
		visitingPrameter = true;
		
		return this;
	}
	
	@Override
	public void visitTypeVariable(String name)
	{
		// generic parameter type - is this complex ?
		if (visitingPrameter) {
			numberOfComplexParametrs++;
			visitingPrameter = false;
		}
	}
	
	@Override
	public SignatureVisitor visitArrayType()
	{		
		if (visitingPrameter) {
			numberOfComplexParametrs++;
			visitingPrameter = false;
		}
		return this;
	}
	
	@Override
	public void visitClassType(String name) {
		if (visitingPrameter) {
			numberOfComplexParametrs++;
			visitingPrameter = false;
		}	
	}
	
	@Override
	public void visitBaseType(char descriptor) {
		if (visitingPrameter) {
			numberOfSimpleParametrs++;
			visitingPrameter = false;
		}
	}	
	
	public int getNumberOfSimpleParametrs() {
		return numberOfSimpleParametrs;
	}
	
	public int getNumberOfComplexParametrs() {
		return numberOfComplexParametrs;
	}
}
