package util;

import java.util.Comparator;

public class NumberComparator implements Comparator<Number>{

	@Override
	public int compare(Number o1, Number o2) {
		return (int) (o1.doubleValue() - o2.doubleValue());
	}

}
