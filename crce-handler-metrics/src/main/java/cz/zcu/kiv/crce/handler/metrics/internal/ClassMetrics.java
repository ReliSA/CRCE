package cz.zcu.kiv.crce.handler.metrics.internal;

import java.lang.reflect.Modifier;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassMetrics {

	private static final String DEFAULT_PACKAGE_NAME = "<default>";
	
	private static final double COMPLEX_FIELD_WEIGHT = 1.0;
	private static final double COMPLEX_PARAMETER_WEIGHT = 1.0;
	
	private static final int[] COMPLEX_TYPES = new int[] { Type.ARRAY, Type.OBJECT };
	
	private String packageName;
	private String className;
	
	private boolean isPublic;
	private boolean isInterface;
	
	private double classComplexity;
	private double methodsComplexity;		
	
	public ClassMetrics(ClassNode byteCodeNode) {
		
		classComplexity = 0;
		methodsComplexity = 0;
		
		String fullClassName = byteCodeNode.name.replace('/','.');
		
		packageName = getPackageName(fullClassName);
		className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		
		isInterface = Modifier.isInterface(byteCodeNode.access);
		isPublic = Modifier.isPublic(byteCodeNode.access);
		
		int simpleTypeFieldCount = 0;
		int complexTypeFieldCount = 0;
		
		@SuppressWarnings("unchecked")
		List<FieldNode> fields = byteCodeNode.fields;
        for (FieldNode field : fields) {
        	// no simple type field has signature
        	if (field.signature != null && !isComplexType(Type.getType(field.desc))) {
    			// simple type field 
        		simpleTypeFieldCount++;
        	}
        	else {
        		complexTypeFieldCount++;
        	}
        }
        
        classComplexity = simpleTypeFieldCount + COMPLEX_FIELD_WEIGHT * complexTypeFieldCount;
		
        @SuppressWarnings("unchecked")
		List<MethodNode> methods = byteCodeNode.methods;
        for (MethodNode method : methods) {
        	parseMethod(method);
        }
	}
	
	private String getPackageName(String className)	{
		String packageName;
		
		int lastIndexOf = className.lastIndexOf(".");
		if (lastIndexOf == -1) { 
			// default package
			packageName = DEFAULT_PACKAGE_NAME;
		}
		else {
			packageName = className.substring(0, lastIndexOf);
		}
		
		return packageName;
	}
	
	private void parseMethod(MethodNode method) {
		boolean isPublicMethod = Modifier.isPublic(method.access);
		
		if (!isPublicMethod) {
			return;
		}
		
		int simpleParametersCount = 0;
		int complexParametersCount = 0;
		
		if (method.signature != null) {
			MetricsSignatureVisitor visitor = new MetricsSignatureVisitor();			
			new SignatureReader(method.signature).accept(visitor);
			
			simpleParametersCount = visitor.getNumberOfSimpleParametrs();
			complexParametersCount = visitor.getNumberOfComplexParametrs();
		}
		else {
			for (Type parameterType : Type.getArgumentTypes(method.desc)) {
				if (isComplexType(parameterType)) {
					complexParametersCount++;
				} 
				else {
					simpleParametersCount++;
				}
			}
		}
				
		methodsComplexity += simpleParametersCount + complexParametersCount * COMPLEX_PARAMETER_WEIGHT;
	}
	
	private boolean isComplexType(Type t) {
		int typeInt = t.getSort();
		
		for (int type : COMPLEX_TYPES) {
			if (type == typeInt) {
				return true;
			}
		}
		
		return false;
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
	
	public double getClassComplexity() {
		return classComplexity;
	}

	public double getMethodsComplexity() {
		return methodsComplexity;
	}
}
