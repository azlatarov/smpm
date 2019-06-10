package com.smpm.pricing;

import java.util.HashSet;
import java.util.Set;

import com.smpm.domain.PurchaseItem;
import com.smpm.domain.PurchaseItemType;
import com.smpm.domain.UnitPrice;



/**
 * This class simulates a data-set of purchase items to pick from.
 * @author azlatarov
 */
public class PriceList {
	public static Set<PurchaseItem> items = new HashSet<>();
	
	private PriceList() {}
	
	public static PriceList getInstance() {
		return PriceList.Helper.INSTANCE;
	}
	
	private static class Helper {
		private static final PriceList INSTANCE = new PriceList();
	}
	
	static {
		preloadPurchaseItems();
	}
	
	/**
	 * Method simulates preloading of data from persistent data store.
	 */
	private static final void preloadPurchaseItems() {
		addOrUpdateItem("Beans", PurchaseItemType.BY_COUNT, 0.50D);
		addOrUpdateItem("Coke", PurchaseItemType.BY_COUNT, 0.70D);
		addOrUpdateItem("Roobar w. choco chips", PurchaseItemType.BY_COUNT, 0.80D);
		addOrUpdateItem("Avocado", PurchaseItemType.BY_COUNT, 2.10D);
		addOrUpdateItem("Avocado", PurchaseItemType.BY_WEIGHT, 5.50D);
		addOrUpdateItem("Oranges", PurchaseItemType.BY_WEIGHT, 1.99D);
		addOrUpdateItem("Tomatoes", PurchaseItemType.BY_WEIGHT, 1.54D);
		addOrUpdateItem("Cucumbers", PurchaseItemType.BY_WEIGHT, 0.89D);
	}
	
	public static final boolean addOrUpdateItem(String itemName, PurchaseItemType itemType, double itemSinglePrice) {
		boolean result = false;
		
		try {
			PurchaseItem item = PurchaseItem.getInstance(itemName, itemType, UnitPrice.getInstance(itemSinglePrice), null);
			
			if (items.contains(item))
				items.remove(item);
				
			result = items.add(item);
		} catch (IllegalArgumentException iae) {
			// log event
		}
		
		return result;
	}
	
	public static final boolean removeItem(String itemName, PurchaseItemType itemType) {
		boolean result = false;
		
		try {
			PurchaseItem item = PurchaseItem.getInstance(itemName, itemType, null, null);
			result = items.contains(item) ? items.remove(item) : result;
		} catch (IllegalArgumentException iae) {
			// log event
		}
		
		return result;
	}
	
}
