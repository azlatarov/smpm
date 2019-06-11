package com.smpm.pricing;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * All discounts currently active. A discount currently has no status, if it's present here it is active.
 * Also, only 1 discount per purchase item is allowed, adding duplicates will be ignored.
 * @author azlatarov
 */
public class Discounts {
	private Set<Discount> discounts = new HashSet<>();
	
	private Discounts() {}
	
	public static Discounts getInstance() {
		return Discounts.Helper.INSTANCE;
	}
	
	private static class Helper {
		private static final Discounts INSTANCE = new Discounts();
	}
	
	public boolean add(Discount discountToAdd) {
		return discounts.add(discountToAdd);
	}
	
	public boolean remove(Discount discountToRemove) {
		return discounts.remove(discountToRemove);
	}
	
	public Stream<Discount> getAll() {
		return discounts.stream();
	}
}
