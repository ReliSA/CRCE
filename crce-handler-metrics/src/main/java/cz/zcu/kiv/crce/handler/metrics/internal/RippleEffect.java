package cz.zcu.kiv.crce.handler.metrics.internal;

public class RippleEffect {

	private final int internalMethodsCount;
	private final int externalMethodsCount;
	
	public RippleEffect(int internalMethodsCount, int externalMethodsCount) {
		
		this.internalMethodsCount = internalMethodsCount;
		this.externalMethodsCount = externalMethodsCount;
	}

	public int getInternalMethodsCount() {
		return internalMethodsCount;
	}

	public int getExternalMethodsCount() {
		return externalMethodsCount;
	}
}
