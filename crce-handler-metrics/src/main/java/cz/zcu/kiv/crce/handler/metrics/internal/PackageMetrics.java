package cz.zcu.kiv.crce.handler.metrics.internal;

public interface PackageMetrics {

	Object computeValueForPackage(String packageName);
	
	String getName();
}
