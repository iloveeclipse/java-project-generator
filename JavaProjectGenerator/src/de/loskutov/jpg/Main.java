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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
	
	public static void main(String[] args) throws IOException {
		File rootDir = new File("./target/generated/");
        System.out.println("Writing to "  + rootDir.getAbsolutePath());
        Path root = rootDir.toPath();
		
		
		int depth = 10;
		int roots = 10;
		int classes = 100;

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
		
		List<Package> rootPackages = new ArrayList<>();
		for (int i = 0; i < roots; i++) {
			Package p = createPackage(pnames.next(), null);
			rootPackages.add(p);
		}
		
		for (Package r : rootPackages) {
			Stream<Package> stream = Stream.iterate(r, x -> new Package(pnames.next(), x)).limit(depth);
			stream.forEach(p -> {
				createInterfaces(p);
				createClasses(p);
			});
		}
		List<JavaElement> elements = new ArrayList<>(interfaces);
		elements.addAll(classes);
		elements.forEach(this::generateFile);
		System.out.println("Generated " + elements.size() + " classes");
	}

	private void generateFile(JavaElement e) {
		try {
			e.persist(root);
		} catch (IOException e1) {
			e1.printStackTrace();
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
			classes.add(new Clazz("Element0", p.getFqn(), implement.next().fqn(), "java.lang.Object"));
		}
		toExtend.stream().forEach(x -> {
			classes.add(new Clazz("Element" + classes.size(), p.getFqn(), implement.next().fqn(), toExtend.next().fqn()));
		});
	}
	
	Package createPackage(String name, Package parent) {
		return new Package(name, parent);
	}
	
}
