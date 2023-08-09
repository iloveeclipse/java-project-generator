package de.loskutov.jpg;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

	public static void main(String[] args) throws IOException {
		String pathname = "./target/generated/";

        int roots = 1;
		int depth = 5;
		int classes = 1100;

		int fields = 3;
		int imports = 3;
		int comments = 3;
		int see = 3;
		int methods = 1; // result method count will be (methods x 6 + runnablesAndCallables x 2)
		int runnablesAndCallables = 1;
		boolean extend = true;
		boolean hideWarnings = false;

		if(args.length == 0) {
			System.out.println("No arguments given, using defaults");
		} else {
			try {
				int argc = 0;
				pathname = args[argc++];
				roots = Integer.parseUnsignedInt(args[argc++]);
				depth = Integer.parseUnsignedInt(args[argc++]);
				classes = Integer.parseUnsignedInt(args[argc++]);
				fields = Integer.parseUnsignedInt(args[argc++]);
				imports = Integer.parseUnsignedInt(args[argc++]);
				comments = Integer.parseUnsignedInt(args[argc++]);
				see = Integer.parseUnsignedInt(args[argc++]);
				methods = Integer.parseUnsignedInt(args[argc++]);
				runnablesAndCallables = Integer.parseUnsignedInt(args[argc++]);
			} catch(Exception e) {
				//
			}
		}
		File rootDir = new File(pathname);
		Path root = rootDir.toPath();
		System.out.println("Writing to "  + rootDir.getAbsolutePath());
		System.out.println("Roots: " + roots + ", depth: " + depth +
				", classes & interfaces per package: " + (classes*2) +
				", \n imports + fields + comments + methodsx4 per class: " + imports + " + " + fields + " + " + comments + " + " + (methods*4));
		System.out.println("Will generate " + roots + "x" + depth + "x" + classes + "x2 + 2 = " + (depth * roots* classes * 2 + 2) + " files");

		JavaElement.fieldsCount = fields;
		JavaElement.importsCount = imports;
		JavaElement.commentsCount = comments;
		JavaElement.seeCount = see;
		JavaElement.methodCounts = methods;
		JavaElement.runnablesAndCallablesCounts = runnablesAndCallables;
		JavaElement.useExtend = extend;
		JavaElement.hideWarnings = hideWarnings;

		new JavaBuilder(depth, roots, classes, root).build();
	}
}

class JavaBuilder {

	static List<String> namesList = IntStream.rangeClosed('a', 'z').mapToObj(x -> String.valueOf((char)x))
			.collect(Collectors.toList());

	int depth;
	int roots;
	private Ring<String> pnames;
	private Path root;

	private List<Clazz> classes;

	private List<Interface> interfaces;

	private int countClasses;

	public JavaBuilder(int depth, int roots, int countClasses, Path root) {
		this.depth = depth;
		this.roots = roots;
		this.countClasses = countClasses;
		this.root = root;
		pnames = new Ring<>(namesList);
		classes = new ArrayList<>();
		interfaces = new ArrayList<>();
	}

	void build() throws IOException {
		Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
			   @Override
			   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			       Files.delete(file);
			       return FileVisitResult.CONTINUE;
			   }

			   @Override
			   public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			       Files.delete(dir);
			       return FileVisitResult.CONTINUE;
			   }
			});
		Files.createDirectories(root);
		Files.createFile(root.resolve(".empty_dir_placeholder"));

		List<Package> rootPackages = new ArrayList<>();

		for (int i = 0; i < roots; i++) {
			String name = pnames.next();
			if(i > pnames.originalDataSize()) {
				name = name + (i - pnames.originalDataSize());
			}
			Package p = createPackage(name, null);
			rootPackages.add(p);
		}

		for (Package r : rootPackages) {
			Stream<Package> stream = Stream.iterate(r, x -> new Package(pnames.next(), x)).limit(depth);
			stream.forEach(p -> {
				createInterfaces(p);
				createClasses(p);
			});
		}
		AtomicLong lines = new AtomicLong();
		List<JavaElement> elements = new ArrayList<>(interfaces);
		elements.addAll(classes);
		elements.forEach(t -> lines.addAndGet(generateFile(t)));
		System.out.println("Generated " + elements.size() + " classes with " + lines + " lines of code");
	}

	private int generateFile(JavaElement e) {
		try {
			return e.persist(root);
		} catch (IOException e1) {
			e1.printStackTrace();
			return 0;
		}
	}

	void createInterfaces(Package p) {
		Ring<Interface> toImplement = new Ring<>(interfaces, countClasses);
		if(interfaces.isEmpty()) {
			interfaces.add(new Interface("IFoo0", p.getFqn(), "java.util.concurrent.Callable"));
		}
		toImplement.stream().forEach(i -> {
			interfaces.add(new Interface("IFoo" + interfaces.size(), p.getFqn(), toImplement.next().fqn()));
		});
	}

	void createClasses(Package p) {
		Ring<Interface> implement = new Ring<>(interfaces, countClasses);
		Ring<Clazz> toExtend = new Ring<>(classes, countClasses);
		if (classes.isEmpty()) {
			classes.add(new Clazz("Foo0", p.getFqn(), implement.next().fqn(), "java.lang.Object"));
		}
		toExtend.stream().forEach(x -> {
			classes.add(new Clazz("Foo" + classes.size(), p.getFqn(), implement.next().fqn(), toExtend.next().fqn()));
		});
	}

	Package createPackage(String name, Package parent) {
		return new Package(name, parent);
	}

}
