package de.loskutov.jpg;

public class Interface extends JavaElement {

	private String extend;

	Interface(String name, String packageName, String extend) {
		super(name, packageName);
		this.extend = extend;
	}

	@Override
	String generateCode() {
		String type = genTypes.next();
		String s = "package " + packageName + ";\n\n" +
				generateImports() +
				"@SuppressWarnings(\"all\")\n" +
				"public interface " + name + "<"+type+"> extends " + extend + "<"+type+"> {\n\n" +

					generateFields() +

					"\t String getName();\n\n" +

					"\t void setName(String s);\n\n" +

					"\t "+type+" get();\n\n" +

					"\t void set("+type+" e);\n\n" +
				"}\n";
		return s;
	}

}
