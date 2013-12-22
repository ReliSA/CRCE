package cz.zcu.kiv.crce.handler.metrics.internal;

import java.lang.reflect.Modifier;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Class computing information from ClassNode for future using in metrics computing.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class ClassMetrics {

	private static final String DEFAULT_PACKAGE_NAME = "<default>";
	
	// definition of complex types
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
	
	/**
	 * New instance.
	 * 
	 * @param byteCodeNode Asm ClassNode to parse information.
	 */
	public ClassMetrics(ClassNode byteCodeNode) {
		
		methodCount = 0;
		
		simpleTypeFieldCount = 0;
		complexTypeFieldCount = 0;
		
		simpleParametersCount = 0;
		complexParametersCount = 0;
		
		String fullClassName = byteCodeNode.name.replace('/','.');
		
		// class and package name
		className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		packageName = getPackageName(fullClassName);
	
		// access and isInterface information 
		isPublic = Modifier.isPublic(byteCodeNode.access);
		isInterface = Modifier.isInterface(byteCodeNode.access);
		
		// filed informations
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
        	
        // methods informations
        @SuppressWarnings("unchecked")
		List<MethodNode> methods = byteCodeNode.methods;
        for (MethodNode method : methods) {
        	parseMethod(method);
        }
	}
	
	/**
	 * Parse package name.
	 * 
	 * @param className Full name of class (containing package name).
	 * @return Package name.
	 */
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
	
	/**
	 * Parsing single method.
	 * 
	 * @param method Asm MethodNode to parse.
	 */
	private void parseMethod(MethodNode method) {
		// parsing method access
		boolean isPublicMethod = Modifier.isPublic(method.access);
		
		// continue only for public methods
		if (!isPublicMethod) {
			return;
		}
		
		// information parsed from signature
		if (method.signature != null) {
			MethodMetricsSignatureVisitor visitor = new MethodMetricsSignatureVisitor();			
			new SignatureReader(method.signature).accept(visitor);
			
			simpleParametersCount += visitor.getSimpleParametersCount();
			complexParametersCount += visitor.getComplexParametersCount();
		}
		else {
			// non signature method 
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
	
	/**
	 * Testing, if parametr type is complex.
	 * 
	 * @param t Type to test.
	 * @return True, if parametr type is complex.
	 */
	private boolean isComplexType(Type t) {
		int typeInt = t.getSort();
		
		for (int type : COMPLEX_TYPES) {
			if (type == typeInt) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Name of class (short).
	 * 
	 * @return Name of class.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Name of classes package.
	 * 
	 * @return Name of classes package.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Indicator, if class (or interface) is public. 
	 * 
	 * @return True, if class(interface) is public.
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * Indicator, if this is interface instead of class.
	 * 
	 * @return True, if interface.
	 */
	public boolean isInterface() {
		return isInterface;
	}

	/**
	 * Count of all public methods.
	 * 
	 * @return Count of public methods.
	 */
	public double getMethodCount() {
		return methodCount;
	}

	/**
	 * Count of fields of simple type. 
	 * 
	 * @return Count of fields of simple type. 
	 */
	public int getSimpleTypeFieldCount() {
		return simpleTypeFieldCount;
	}

	/**
	 * Count of fields of complex type. 
	 * 
	 * @return Count of fields of complex type. 
	 */
	public int getComplexTypeFieldCount() {
		return complexTypeFieldCount;
	}

	/**
	 * Count of methods parametrs of simple type.
	 * 
	 * @return Count of methods parametrs of simple type.
	 */
	public int getSimpleParametersCount() {
		return simpleParametersCount;
	}

	/**
	 * Count of methods parametrs of complex type.
	 * 
	 * @return Count of methods parametrs of complex type.
	 */
	public int getComplexParametersCount() {
		return complexParametersCount;
	}
}
