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
				generateComments() +
				generateTypeDefinition(type) +
				"{\n\n" +
					generateFields() +
					generateMethods(type) +
				"}\n";
		return s;
	}

	String generateTypeDefinition(String type) {
		String type2 = genTypes.next();
		String types = type + ", " + type2;
		if(useExtend) {
			return "@SuppressWarnings(\"all\")\n" +
					"public interface " + name + "<"+ types + "> extends " + extend + "<"+types+">, Runnable ";
		}
		return "@SuppressWarnings(\"all\")\n" +
				"public interface " + name + "<"+ type + "> ";
	}


	String generateMethods(String type) {
		if(methodCounts == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < methodCounts; i++) {
			String suffix = i == 0? "" : "" + i;
			sb.append("\t String getName" + suffix + "();\n\n");
			sb.append("\t void setName" + suffix + "(String s);\n\n");
			sb.append("\t " + type + " get" + suffix + "();\n\n");
			sb.append("\t void set" + suffix + "(" + type + " e);\n\n");
		}
		return sb.toString();
	}

}
