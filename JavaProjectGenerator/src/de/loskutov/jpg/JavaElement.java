package de.loskutov.jpg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
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
	static int commentsCount = 3;
	static int seeCount = 3;
	static int methodCounts = 1;
	static int runnablesAndCallablesCounts = 1;
	static boolean useExtend = true;

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

	static final List<String> LOREM = Arrays.asList(
			"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut ",
			"labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. ",
			"Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. ",
			"Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum ",
			"dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent ",
			"luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet,   ",
			"consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. ",
			"Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ",
			"ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, ",
			"vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit   ",
			"praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. ",
			"Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat ",
			"facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh ",
			"euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis ",
			"nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. ",
			"Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum ",
			"dolore eu feugiat nulla facilisis. ",
			"At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata ",
			"sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, ",
			"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. ",
			"At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata ",
			"sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, ",
			"At accusam aliquyam diam diam dolore dolores duo eirmod eos erat, et nonumy sed tempor et et invidunt ",
			"justo labore Stet clita ea et gubergren, kasd magna no rebum. sanctus sea sed takimata ut vero voluptua. "
			);

	static final List<String> LETTERS = IntStream.rangeClosed('A', 'Z').mapToObj(x -> String.valueOf((char)x))
			.collect(Collectors.toList());



	String name;
	String packageName;
	static final Ring<String> imports = new Ring<>(IMPORTS);
	static final Ring<String> fields = new Ring<>(FIELDS);
	static final Ring<String> genTypes = new Ring<>(LETTERS);
	final Ring<String> loremIpsum = new Ring<>(LOREM);

	JavaElement(String name, String packageName){
		this.name = name;
		this.packageName = packageName;
	}

	abstract String generateCode();

	String generateImports() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < importsCount; i++) {
			sb.append("import ").append(imports.next()).append(";\n");
		}
		sb.append("\n");
		return sb.toString();
	}

	String generateComments() {
		StringBuilder sb = new StringBuilder();
		if(commentsCount > 0) {
			sb.append("/**\n");
			for (int i = 0; i < commentsCount; i++) {
				sb.append(" * ").append(loremIpsum.next()).append("\n");
			}
			sb.append(" *\n");
			for (int i = 0; i < seeCount; i++) {
				sb.append(" * @see ").append(fields.next()).append("\n");
			}
			sb.append(" */\n");
		}
		return sb.toString();
	}

	String generateFields() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fieldsCount; i++) {
			sb.append("\t ").append(fields.next()).append(" f").append(i).append(" = null;\n");
		}
		sb.append("\n");
		return sb.toString();
	}

	int persist(Path root) throws IOException {
		int lines = 0;
		try(BufferedWriter writer = createWriter(root)){
			String code = generateCode();
			LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(code));
		    lineNumberReader.skip(Long.MAX_VALUE);
		    lines = lineNumberReader.getLineNumber();
			writer.append(code);
			writer.flush();
		}
		return lines;
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
