package com.smpm.pricing;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Discounts {
	private Set<Discount> discountSet = new HashSet<>();
	
	private Discounts() {}
	
	public static Discounts getInstance() {
		return Discounts.Helper.INSTANCE;
	}
	
	private static class Helper {
		private static final Discounts INSTANCE = new Discounts();
	}
	
	public boolean add(Discount discountToAdd) {
		return discountSet.add(discountToAdd);
	}
	
	public boolean remove(Discount discountToRemove) {
		return discountSet.remove(discountToRemove);
	}
	
	public Stream<Discount> getAll() {
		return discountSet.stream();
	}
}
