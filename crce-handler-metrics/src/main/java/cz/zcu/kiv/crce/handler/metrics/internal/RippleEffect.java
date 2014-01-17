package cz.zcu.kiv.crce.handler.metrics.internal;

public class RippleEffect {

	private final int internalNonAbstractMethodsCount;
	private final int internalAbstractMethodsCount;
	private final int externalMethodsCount;
	
	public RippleEffect(int internalNonAbstractMethodsCount, int internalAbstractMethodsCount,
			int externalMethodsCount) {
		
		this.internalNonAbstractMethodsCount = internalNonAbstractMethodsCount;
		this.internalAbstractMethodsCount = internalAbstractMethodsCount;
		this.externalMethodsCount = externalMethodsCount;
	}

	public int getInternalNonAbstractMethodsCount() {
		return internalNonAbstractMethodsCount;
	}

	public int getInternalAbstractMethodsCount() {
		return internalAbstractMethodsCount;
	}

	public int getExternalMethodsCount() {
		return externalMethodsCount;
	}
}
