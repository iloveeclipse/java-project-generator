package de.loskutov.jpg;

public class Interface extends JavaElement {

	private String extend;

	Interface(String name, String packageName, String extend) {
		super(name, packageName);
		this.extend = extend;
	}

	@Override
	String generateCode() {
		String s = "package " + packageName + ";\n\n" + 
				"import " + imports.next() + ";\n\n" +
				"@SuppressWarnings(\"all\")\n" +
				"public interface " + name + "<E> extends " + extend + "<E> {\n\n" + 

				     "\t " + fields.next() + " ifield = null;\n\n" +
					
					"\t String getName();\n\n" +
					
					"\t void setName(String s);\n\n" +
					
					"\t E get();\n\n" +
					
					"\t void set(E e);\n\n" +
				"}\n";
		return s;
	}

}
