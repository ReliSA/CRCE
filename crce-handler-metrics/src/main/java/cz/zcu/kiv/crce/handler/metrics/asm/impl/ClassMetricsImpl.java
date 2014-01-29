package cz.zcu.kiv.crce.handler.metrics.asm.impl;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import cz.zcu.kiv.crce.handler.metrics.asm.ClassMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.MethodMetrics;

/**
 * Class computing information from ClassNode for future using in metrics computing.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class ClassMetricsImpl implements ClassMetrics {

	private static final String DEFAULT_PACKAGE_NAME = "<default>";
	
	private static final int[] COMPLEX_TYPES = new int[] { Type.ARRAY, Type.OBJECT };
	
	private String fullClassName;
	private String packageName;
	private String className;
	
	private boolean isPublic;
	private boolean isInterface;
	
	private double methodCount;
	
	private int simpleTypeFieldCount;
	private int complexTypeFieldCount;
	
	private int simpleParametersCount;
	private int complexParametersCount;
	
	private List<MethodMetrics> methods;
	
	/**
	 * New instance.
	 * 
	 * @param byteCodeNode ASM ClassNode to parse information.
	 */
	public ClassMetricsImpl(@Nonnull ClassNode byteCodeNode) {
		
		methods = new ArrayList<MethodMetrics>();
		
		methodCount = 0;
		
		simpleTypeFieldCount = 0;
		complexTypeFieldCount = 0;
		
		simpleParametersCount = 0;
		complexParametersCount = 0;
		
		fullClassName = byteCodeNode.name.replace('/','.');
		
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
        	parseMethod(fullClassName, method);
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
	private void parseMethod(String fullClassName, MethodNode method) {

		boolean isPublicMethod = Modifier.isPublic(method.access);
		
		methods.add(new MethodMetricsImpl(fullClassName, isPublicMethod, method));
		
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
	 * Testing, if parameter type is complex.
	 * 
	 * @param t Type to test.
	 * @return True, if parameter type is complex.
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
	
	@Override
	@Nonnull
	public String getFullClassName() {
		return fullClassName;
	}

	@Override
	@Nonnull
	public String getClassName() {
		return className;
	}

	@Override
	@Nonnull
	public String getPackageName() {
		return packageName;
	}

	@Override
	public boolean isPublic() {
		return isPublic;
	}

	@Override
	public boolean isInterface() {
		return isInterface;
	}

	@Override
	public double getMethodCount() {
		return methodCount;
	}

	@Override
	public int getSimpleTypeFieldCount() {
		return simpleTypeFieldCount;
	}

	@Override
	public int getComplexTypeFieldCount() {
		return complexTypeFieldCount;
	}

	@Override
	public int getSimpleParametersCount() {
		return simpleParametersCount;
	}

	@Override
	public int getComplexParametersCount() {
		return complexParametersCount;
	}
	
	@Override
	@Nonnull
	public List<MethodMetrics> getMethods() {
		return methods;
	}
}
