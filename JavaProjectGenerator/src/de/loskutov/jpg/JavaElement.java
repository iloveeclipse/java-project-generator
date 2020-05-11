package de.loskutov.jpg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class JavaElement {

	static int fieldsCount = 3;
	static int importsCount = 3;

	static final List<String> IMPORTS = Arrays.asList(
			"java.awt.datatransfer.*",
			"java.beans.beancontext.*",
			"java.io.*",
			"java.rmi.*",
			"java.nio.file.*",
			"java.sql.*",
			"java.util.logging.*",
			"java.util.zip.*",
			"javax.annotation.processing.*",
			"javax.lang.model.*",
			"javax.management.*",
			"javax.naming.directory.*",
			"javax.net.ssl.*",
			"javax.rmi.ssl.*"
			);

	static final List<String> FIELDS = Arrays.asList(
			"java.awt.datatransfer.DataFlavor",
			"java.beans.beancontext.BeanContext",
			"java.io.File",
			"java.rmi.Remote",
			"java.nio.file.FileStore",
			"java.sql.Array",
			"java.util.logging.Filter",
			"java.util.zip.Deflater",
			"javax.annotation.processing.Completion",
			"javax.lang.model.AnnotatedConstruct",
			"javax.management.Attribute",
			"javax.naming.directory.DirContext",
			"javax.net.ssl.ExtendedSSLSession",
			"javax.rmi.ssl.SslRMIClientSocketFactory"
			);

	static final List<String> LETTERS = IntStream.rangeClosed('A', 'Z').mapToObj(x -> String.valueOf((char)x))
			.collect(Collectors.toList());

	String name;
	String packageName;
	static final Ring<String> imports = new Ring<>(IMPORTS);
	static final Ring<String> fields = new Ring<>(FIELDS);
	static final Ring<String> genTypes = new Ring<>(LETTERS);

	JavaElement(String name, String packageName){
		this.name = name;
		this.packageName = packageName;
	}

	abstract String generateCode();

	String generateImports() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < importsCount; i++) {
			sb.append("import ").append(imports.next()).append(";\n\n");
		}
		return sb.toString();
	}

	String generateFields() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fieldsCount; i++) {
			sb.append("\t ").append(fields.next()).append(" f").append(i).append(" = null;\n\n");
		}
		return sb.toString();
	}

	void persist(Path root) throws IOException {
		try(BufferedWriter writer = createWriter(root)){
			String code = generateCode();
			writer.append(code);
			writer.flush();
		}
	}

	String fqn() {
		return packageName + "." + name;
	}

	BufferedWriter createWriter(Path root) throws IOException {
		Path path = root.resolve(packageName.replace('.', File.separatorChar)).resolve(name + ".java");
		Files.createDirectories(path.getParent());
		return Files.newBufferedWriter(path, StandardCharsets.UTF_8);
	}
}
