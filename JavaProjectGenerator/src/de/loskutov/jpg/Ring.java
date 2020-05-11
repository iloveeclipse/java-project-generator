package de.loskutov.jpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Ring<E> {
	private List<E> choices;
	int cursor = -1;
	private int limit;

	Ring(List<E> list){
		this(list, -1);
	}

	Ring(List<E> list, int limit){
		this.limit = limit;
		if(list instanceof ArrayList) {
			this.choices = list;
		} else {
			this.choices = new ArrayList<>(list);
		}
//		Collections.shuffle(choices);
	}

	E next() {
		cursor ++;
		if(cursor >= choices.size()) {
			cursor = 0;
		}
		return choices.get(cursor);
	}

	Stream<E> stream(){
		if(limit > 0) {
			return Stream.generate(() -> next()).limit(limit);
		}
		return Stream.generate(() -> next());
	}

	int originalDataSize() {
		return choices.size();
	}
}
