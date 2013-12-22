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
	
	private static final int[] COMPLEX_TYPES = new int[] { Type.ARRAY, Type.OBJECT };
	
	private String packageName;
	private String className;
	
	private boolean isPublic;
	private boolean isInterface;
	
	private double methodCount;
	
	private int simpleTypeFieldCount;
	private int complexTypeFieldCount;
	
	private int simpleParametersCount;
	private int complexParametersCount;
	
	public ClassMetrics(ClassNode byteCodeNode) {
		
		methodCount = 0;
		
		simpleTypeFieldCount = 0;
		complexTypeFieldCount = 0;
		
		simpleParametersCount = 0;
		complexParametersCount = 0;
		
		String fullClassName = byteCodeNode.name.replace('/','.');
		
		packageName = getPackageName(fullClassName);
		className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		
		isInterface = Modifier.isInterface(byteCodeNode.access);
		isPublic = Modifier.isPublic(byteCodeNode.access);
		
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
		
		if (method.signature != null) {
			MetricsSignatureVisitor visitor = new MetricsSignatureVisitor();			
			new SignatureReader(method.signature).accept(visitor);
			
			simpleParametersCount += visitor.getNumberOfSimpleParametrs();
			complexParametersCount += visitor.getNumberOfComplexParametrs();
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
				
		methodCount++;
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

	public double getMethodCount() {
		return methodCount;
	}

	public int getSimpleTypeFieldCount() {
		return simpleTypeFieldCount;
	}

	public int getComplexTypeFieldCount() {
		return complexTypeFieldCount;
	}

	public int getSimpleParametersCount() {
		return simpleParametersCount;
	}

	public int getComplexParametersCount() {
		return complexParametersCount;
	}
}
