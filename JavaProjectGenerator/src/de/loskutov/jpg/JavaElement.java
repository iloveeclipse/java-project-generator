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
	
	static List<String> LETTERS = IntStream.rangeClosed('A', 'Z').mapToObj(x -> String.valueOf((char)x))
			.collect(Collectors.toList());
	
	String name;
	String packageName;
	static Ring<String> imports = new Ring<>(IMPORTS);
	static Ring<String> fields = new Ring<>(FIELDS); 
	static Ring<String> genTypes = new Ring<>(LETTERS);
	
	JavaElement(String name, String packageName){
		this.name = name;
		this.packageName = packageName;
	}
	
	abstract String generateCode();
	
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
