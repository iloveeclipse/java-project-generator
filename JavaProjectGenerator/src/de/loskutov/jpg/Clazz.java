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
			return generateFirstObject();
		}
		String type = genTypes.next();
		String s = "package " + packageName + ";\n\n" +
				generateImports() +
				generateComments() +
				generateTypeDefinition(type) +
				"{\n\n" +
					generateFields() +
					generateClassFields(type) +
					generateMethods(type) +
				"}\n";
		return s;
	}

	String generateTypeDefinition(String type) {
		String result = "";
		if (hideWarnings) {
			result = "@SuppressWarnings(\"all\")\n";
		}
		if(useExtend) {
			return result +	"public abstract class " + name + "<"+type+"> extends " + extend + "<"+type+"> implements " + implement + "<"+type+"> ";
		}
		return result +	"public abstract class " + name + "<"+type+"> ";
	}

	String generateClassFields(String type) {
		if(methodCounts == 0) {
			return "";
		}
		String result =
				"\t public "+type+" element;\n\n" +
				"\t public static " + name + " instance;\n\n";
		return result;
	}

	String generateMethods(String type) {
		if(methodCounts == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < methodCounts; i++) {
			String suffix = i == 0? "" : "" + i;
			String result =	"\t public static " + name + " getInstance" + suffix + "() {\n" +
				"\t \t return instance;\n" +
				"\t }\n\n" +
				"\t public static <T> T create" + suffix + "(java.util.List<T> input) {\n" +
				"\t \t return " + extend + ".create(input);\n" +
				"\t }\n\n" +
				"\t public String getName" + suffix + "() {\n" +
				"\t \t return " + extend + ".getInstance" + suffix + "().getName" + suffix + "();\n" +
				"\t }\n\n" +
				"\t public void setName" + suffix + "(String string) {\n" +
				"\t \t " + extend + ".getInstance" + suffix + "().setName" + suffix + "(getName" + suffix + "());\n" +
				"\t \t return;\n" +
				"\t }\n\n" +
				"\t public "+type+" get" + suffix + "() {\n" +
				"\t \t return ("+type+")" + extend + ".getInstance" + suffix + "().get" + suffix + "();\n" +
				"\t }\n\n" +
				"\t public void set" + suffix + "(Object element) {\n" +
				"\t \t this.element = ("+type+")element;\n" +
				"\t \t " + extend + ".getInstance" + suffix + "().set" + suffix + "(this.element);\n" +
				"\t }\n\n";
			sb.append(result);
		}
		if(methodCounts > 0) {
			for (int i = 0; i < runnablesAndCallablesCounts; i++) {
				String suffix = i == 0? "" : "" + i;
				String getInstanceIdx = i == 0 || methodCounts == 1? "" : "" + Math.min(i, methodCounts - 1);
				String result =	 "\t public void run" + suffix + "() {\n" +
				"\t \t try {\n" +
				"\t \t \t this.call" + suffix + "();\n" +
				"\t \t } catch (Exception e) {}\n" +
				"\t \t " + "Runnable r = () -> {\n" +
			 	"\t \t " + "    run" + suffix + "();\n" +
			 	"\t \t " + "    set(this);\n" +
			 	"\t \t " + "    get();\n" +
			 	"\t \t " + "};\n" +
			 	"\t \t " + "r.run();\n" +
			 	"\t \t " + "r = this::run;\n" +
			 	"\t \t " + "r.run();\n" +
			 	"\t \t " + "r = " + extend + ".instance::run;\n" +
			 	"\t \t " + "r.run();\n" +
				"\t \t " + extend + ".getInstance" + getInstanceIdx + "().run();\n" +
				"\t }\n\n";
				sb.append(result);
			}
			for (int i = 0; i < runnablesAndCallablesCounts; i++) {
				String suffix = i == 0? "" : "" + i;
				String getInstanceIdx = i == 0 || methodCounts == 1? "" : "" + Math.min(i, methodCounts - 1);
				String result =	"\t public "+type+" call" + suffix + "() throws Exception {\n" +
						"\t \t " + extend + ".getInstance" + getInstanceIdx + "().call();\n" +
						"\t \t " + "java.util.concurrent.Callable<?> c = () -> {\n" +
					 	"\t \t " + "    call" + suffix + "();\n" +
					 	"\t \t " + "    set(this);\n" +
					 	"\t \t " + "    return get();\n" +
					 	"\t \t " + "};\n" +
					 	"\t \t " + "c.call();\n" +
					 	"\t \t " + "c = " + extend + ".getInstance" + getInstanceIdx + "()::call;\n" +
					 	"\t \t " + "c.call();\n" +
					 	"\t \t return ("+type+")" + extend + ".getInstance" + getInstanceIdx + "().call" + getInstanceIdx + "();\n" +
						"\t }\n\n";
				sb.append(result);
			}
		}
		return sb.toString();
	}

	String generateFirstObject() {
		String type = genTypes.next();
		String s = "package " + packageName + ";\n\n" +
				generateImports() +
				generateComments() +
				"@SuppressWarnings(\"all\")\n" +
				"public abstract class " + name + "<"+type+"> implements " + implement + "<"+type+"> {\n\n" +
					generateFields() +
					"public "+type+" element;\n" +
					"public static " + name + " instance;\n\n" +

					generateObjectMethods(type) +
					"}\n";
		return s;
	}

	String generateObjectMethods(String type) {
		if(methodCounts == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < methodCounts; i++) {
			String suffix = i == 0? "" : "" + i;
			String result =
				"\t public static " + name + " getInstance" + suffix + "() {\n" +
				"\t \t return instance;\n" +
				"\t }\n\n" +
				"\t public static <T> T create" + suffix + "(java.util.List<T> input) {\n" +
				"\t \t return null;\n" +
				"\t }\n\n" +
				"\t public String getName" + suffix + "() {\n" +
				"\t \t return element.toString();\n" +
				"\t }\n\n" +
				"\t public void setName" + suffix + "(String string) {\n" +
				"\t \t return;\n" +
				"\t }\n\n" +
				"\t public "+type+" get" + suffix + "() {\n" +
				"\t \t return element;\n" +
				"\t }\n\n" +
				"\t public void set" + suffix + "(Object element) {\n" +
				"\t \t this.element = ("+type+")element;\n" +
				"\t }\n\n" +
				"\t public "+type+" call" + suffix + "() throws Exception {\n" +
				"\t \t return ("+type+")getInstance" + suffix + "().call" + suffix + "();\n" +
				"\t }\n";
			sb.append(result);
		}
		return sb.toString();
	}

}
