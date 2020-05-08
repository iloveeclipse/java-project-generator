package de.loskutov.jpg;

public class Clazz extends JavaElement {

	String implement;
	String extend;
	
	Clazz(String name, String packageName, String implement, String extend) {
		super(name, packageName);
		this.implement = implement;
		this.extend = extend;
	}

	@Override
	String generateCode() {
		boolean object = "java.lang.Object".equals(extend);
		if(object) {
			return generateObject();
		}
		String type = genTypes.next();
		String s = "package " + packageName + ";\n\n" + 
				"import " + imports.next() + ";\n\n" +
				"@SuppressWarnings(\"all\")\n" +
				"public abstract class " + name + "<"+type+"> extends " + extend + "<"+type+"> implements " + implement + "<"+type+"> {\n\n" + 
					"\t public "+type+" element;\n\n" +
					"\t public " + fields.next() + " field1;\n\n" +
					"\t public static " + name + " instance;\n\n" +
					"\t public static " + name + " getInstance() {\n" +
					"\t \t return instance;\n" +
					"\t }\n\n" +
					"\t public static <T> T create(java.util.List<T> input) {\n" +
					"\t \t return " + extend + ".create(input);\n" +
					"\t }\n\n" +
					"\t public String getName() {\n" +
					"\t \t return " + extend + ".getInstance().getName();\n" +
					"\t }\n\n" +
					"\t public void setName(String string) {\n" +
					"\t \t " + extend + ".getInstance().setName(getName());\n" +
					"\t \t return;\n" +
					"\t }\n\n" +
					"\t public "+type+" get() {\n" +
					"\t \t return ("+type+")" + extend + ".getInstance().get();\n" +
					"\t }\n\n" +
					"\t public void set(Object element) {\n" +
					"\t \t this.element = ("+type+")element;\n" +
					"\t \t " + extend + ".getInstance().set(this.element);\n" +
					"\t }\n\n" +
					"\t public "+type+" call() throws Exception {\n" +
					"\t \t return ("+type+")" + extend + ".getInstance().call();\n" +
					"\t }\n" +
				"}\n";
		return s;
	}
	
	String generateObject() {
		String type = genTypes.next();
		String s = "package " + packageName + ";\n" + 
				"import " + imports.next() + ";\n" +
				"@SuppressWarnings(\"all\")\n" +
				"public abstract class " + name + "<"+type+"> implements " + implement + "<"+type+"> {\n" + 
					"public "+type+" element;\n" +
					"public " + fields.next() + " field1;\n" +
					"public static " + name + " instance;\n" +
					"public static " + name + " getInstance() {\n" +
					"\t return instance;\n" +
					"}\n" +
				
					"public static <T> T create(java.util.List<T> input) {\n" +
					"\t return null;\n" +
					"}\n" +
					
					"public String getName() {\n" +
					"\t return element.toString();\n" +
					"}\n" +
					
					"public void setName(String string) {\n" +
					"\t return;\n" +
					"}\n" +
					
					"public "+type+" get() {\n" +
					"\t return element;\n" +
					"}\n" +
					
					"public void set(Object element) {\n" +
					"\t this.element = ("+type+")element;\n" +
					"}\n" +
					
					"public "+type+" call() throws Exception {\n" +
					"\t return ("+type+")getInstance().call();\n" +
					"}\n" +
					"}\n";
		return s;
	}

}
