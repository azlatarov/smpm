package com.smpm.pricing;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.smpm.domain.PurchaseItem;
import com.smpm.domain.PurchaseItemStatus;
import com.smpm.domain.PurchaseItemType;
import com.smpm.domain.Price;



/**
 * This class simulates a data-set of purchase items to pick from.
 * @author azlatarov
 */
public class PriceList {
	
	private static BiFunction<String, PurchaseItemType, Optional<PurchaseItem>> itemExists = 
			(name, type) -> Helper.INSTANCE.items.parallelStream().filter(i -> PurchaseItem.isEqualTo.test(i, name, type)).findAny();
	
	private Set<PurchaseItem> items = new HashSet<>();
	private Set<PurchaseItem> deletedItems = new HashSet<>();
	
	private PriceList() {}
	
	public static PriceList getInstance() {
		return PriceList.Helper.INSTANCE;
	}
	
	private static class Helper {
		private static final PriceList INSTANCE = new PriceList();
	}
	
	public Optional<PurchaseItem> indatePurchaseItem(String name, PurchaseItemType type, double price) {
		
		Optional<PurchaseItem> item = itemExists.apply(name, type);
		
		try {
			if (item.isPresent())
				return Optional.of(item.get().setUnitPrice(Price.getInstance(price)));
			else {
				PurchaseItem newItem = PurchaseItem.getInstance(name, type, Price.getInstance(price), null);
				items.add(newItem);
				return Optional.of(newItem);
			}
		}
		catch (IllegalArgumentException iae) {
			// log event
		}
		
		return Optional.empty();
	}
	
	public Optional<PurchaseItem> removePurchaseItem(String name, PurchaseItemType type) {
		
		Optional<PurchaseItem> item = itemExists.apply(name, type);
		
		if(item.isPresent())
			try {
				items.remove(item.get());
				deletedItems.add(item.get());
				item.get().setStatus(PurchaseItemStatus.DELETED);
				return item;
			}
			catch (IllegalArgumentException iae) {
				// log event
			}
		
		return Optional.empty();
	}
	
	public Stream<PurchaseItem> getAllPurchaseItems() {
		return items.stream();
	}
	
	public Stream<PurchaseItem> getAllDeletedPurchaseItems() {
		return deletedItems.stream();
	}
}
