package com.smpm.pricing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.smpm.domain.PurchaseItem;
import com.smpm.domain.PurchaseItemType;
import com.smpm.domain.UnitPrice;

/**
 * This class simulates a data-set of purchase items.
 * It can be loaded lazy or proactively.
 * @author azlatarov
 */
public class PriceList {
	public static List<PurchaseItem> priceList = new ArrayList<>();
	
	static {
		initialize();
	}
	
	public static final void initialize() {
		addItemToPriceList("Beans", PurchaseItemType.BY_COUNT, 0.50D);
		addItemToPriceList("Coke", PurchaseItemType.BY_COUNT, 0.70D);
		addItemToPriceList("Roobar w. choco chips", PurchaseItemType.BY_COUNT, 0.80D);
		addItemToPriceList("Oranges", PurchaseItemType.BY_WEIGHT, 1.99D);
		addItemToPriceList("Tomatoes", PurchaseItemType.BY_WEIGHT, 1.54D);
		addItemToPriceList("Cucumbers", PurchaseItemType.BY_WEIGHT, 0.89D);
	}
	
	public static final boolean addItemToPriceList (String itemName, PurchaseItemType itemType, double itemSinglePrice) {
		Optional<PurchaseItem> existingItem = priceList.parallelStream().filter(item -> item.getName().equalsIgnoreCase(itemName))
												.findAny();
		
		if (existingItem.isPresent()) {
			// TODO - handle potential duplicate i.e. log warning/error, display warning/error in AdminUI, etc.;
			//			use existingItem data to be specific;
			return false;
		}
		
		return priceList.add(PurchaseItem.getInstance(itemName, itemType, UnitPrice.getInstance(itemSinglePrice), null));
	}
	
	// TODO - add functionality to alter list besides adding
	
}
