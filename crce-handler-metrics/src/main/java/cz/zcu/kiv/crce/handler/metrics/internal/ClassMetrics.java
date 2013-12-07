package cz.zcu.kiv.crce.handler.metrics.internal;

import java.lang.reflect.Modifier;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassMetrics {

	private static final String DEFAULT_PACKAGE_NAME = "<default>";
	
	private String packageName;
	private String className;
	
	private boolean isPublic;
	private boolean isInterface;
	
	private int numPublicMethods;
	private int numAllMethods;
	
	private int parameterWeightedNumPublicMethods;
	private int parameterWeightedNumAllMethods;	
	
	public ClassMetrics(ClassNode byteCodeNode) {
		
		numPublicMethods = 0;
		numAllMethods = 0;
		
		parameterWeightedNumPublicMethods = 0;
		parameterWeightedNumAllMethods = 0;
		
		String fullClassName = byteCodeNode.name.replace('/','.');
		
		packageName = getPackageName(fullClassName);
		className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		
		isInterface = Modifier.isInterface(byteCodeNode.access);
		isPublic = Modifier.isPublic(byteCodeNode.access);
		
        @SuppressWarnings("unchecked")
		List<MethodNode> methods = byteCodeNode.methods;
        for (MethodNode method : methods) 
        {
        	parseMethod(method);
        }
	}
	
	private String getPackageName(String className)	{
		String packageName;
		
		int lastIndexOf = className.lastIndexOf(".");
		if (lastIndexOf == -1) // default package
		{
			packageName = DEFAULT_PACKAGE_NAME;
		}
		else
		{
			packageName = className.substring(0, lastIndexOf);
		}
		
		return packageName;
	}
	
	private void parseMethod(MethodNode method) {
		boolean isPublicMethod = Modifier.isPublic(method.access);
		
		numAllMethods++;
		if (isPublicMethod) {
			numPublicMethods++;
		}
		
		int parametersCount = 0;
		if (method.signature != null)
		{
			MetricsSignatureVisitor visitor = new MetricsSignatureVisitor();			
			new SignatureReader(method.signature).accept(visitor);
			parametersCount = visitor.getNumberOfMethodParametrs();
		}
		else
		{
			parametersCount = Type.getArgumentTypes(method.desc).length;
		}
		
		
		parameterWeightedNumAllMethods += parametersCount + 1;
		if (isPublicMethod) {
			parameterWeightedNumPublicMethods += parametersCount + 1;
		}
	}

	public String getPackageName() {
		return packageName;
	}

	public String getClassName() {
		return className;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public int getNumPublicMethods() {
		return numPublicMethods;
	}

	public int getNumAllMethods() {
		return numAllMethods;
	}

	public int getParameterWeightedNumPublicMethods() {
		return parameterWeightedNumPublicMethods;
	}

	public int getParameterWeightedNumAllMethods() {
		return parameterWeightedNumAllMethods;
	}
}
