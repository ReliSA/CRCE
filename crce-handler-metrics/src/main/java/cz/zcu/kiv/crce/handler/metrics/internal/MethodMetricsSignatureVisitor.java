package cz.zcu.kiv.crce.handler.metrics.internal;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

/**
 * Implementation of <code>SignatureVisitor</code> for collecting information base on 
 * method signature.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class MethodMetricsSignatureVisitor extends SignatureVisitor {

	private boolean visitingPrameter;
	
	private int simpleParametersCount;
	private int complexParametersCount;
	
	/**
	 * New instance.
	 */
	public MethodMetricsSignatureVisitor() {
		super(Opcodes.ASM4);

		visitingPrameter = false;
		
		simpleParametersCount = 0;
		complexParametersCount = 0;
	}

	@Override
	public SignatureVisitor visitParameterType() {
		visitingPrameter = true;
		
		return this;
	}
	
	@Override
	public void visitTypeVariable(String name)
	{
		// generic parameter type
		if (visitingPrameter) {
			complexParametersCount++;
			visitingPrameter = false;
		}
	}
	
	@Override
	public SignatureVisitor visitArrayType()
	{		
		if (visitingPrameter) {
			complexParametersCount++;
			visitingPrameter = false;
		}
		return this;
	}
	
	@Override
	public void visitClassType(String name) {
		if (visitingPrameter) {
			complexParametersCount++;
			visitingPrameter = false;
		}	
	}
	
	@Override
	public void visitBaseType(char descriptor) {
		if (visitingPrameter) {
			simpleParametersCount++;
			visitingPrameter = false;
		}
	}	
	
	/**
	 * Count of simple parameters eg. int, log, double...
	 * 
	 * @return Count of parameters.
	 */
	public int getSimpleParametersCount() {
		return simpleParametersCount;
	}
	
	/**
	 * Count of complex parameters eg. class, array...
	 * 
	 * @return Count of parameters.
	 */
	public int getComplexParametersCount() {
		return complexParametersCount;
	}
}
