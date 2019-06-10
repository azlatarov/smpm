package com.smpm;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.smpm.domain.PurchaseItem;
import com.smpm.domain.PurchaseItemType;
import com.smpm.pricing.PriceList;

import static junit.framework.Assert.*;

public class PricingManagerTest {
	
    @BeforeClass
    public static void prepare() {
    	PricingManager.start();
    }

    // Run once, e.g close connection, cleanup
    @AfterClass
    public static void cleanup() {
        // TODO
    }
	
    @Test
    public void applicationStartup() {
    	assertNotNull(System.getProperty(PricingManager.CURRENCY_SYMBOL_PROPERTY));
    	assertNotNull(PriceList.items);
    	assertFalse(PriceList.items.isEmpty());
    }
    
    @Test
    public void addUniquePurchaseItems() {
    	int initialItemsSize = PriceList.items.size();
    	
    	assertTrue(PriceList.addOrUpdateItem("Fish", PurchaseItemType.BY_COUNT, 14.00));
    	assertTrue(PriceList.items.contains(PurchaseItem.getInstance("Fish", PurchaseItemType.BY_COUNT, null, null)));
    	assertTrue(PriceList.items.size() == initialItemsSize + 1);
    	
    	assertTrue(PriceList.addOrUpdateItem("Fish", PurchaseItemType.BY_WEIGHT, 14.00));
    	assertTrue(PriceList.items.contains(PurchaseItem.getInstance("Fish", PurchaseItemType.BY_WEIGHT, null, null)));
    	assertTrue(PriceList.items.size() == initialItemsSize + 2);
    	
    	assertTrue(PriceList.removeItem("Fish", PurchaseItemType.BY_COUNT));
    	assertTrue(PriceList.removeItem("Fish", PurchaseItemType.BY_WEIGHT));
    	assertTrue(PriceList.items.size() == initialItemsSize);
    }
    
    @Test
    public void addExistingPurchaseItem() {
    	int initialItemsSize = PriceList.items.size();
    	assertTrue(PriceList.addOrUpdateItem("Fish", PurchaseItemType.BY_COUNT, 14.00));
    	assertTrue(PriceList.items.size() == (initialItemsSize + 1));
    	assertTrue(PriceList.addOrUpdateItem("Fish", PurchaseItemType.BY_COUNT, 10.00));
    	assertTrue(PriceList.items.size() == (initialItemsSize + 1));
    	assertTrue(PriceList.items.contains(PurchaseItem.getInstance("Fish", PurchaseItemType.BY_COUNT, null, null)));
    	
    	assertTrue(PriceList.removeItem("Fish", PurchaseItemType.BY_COUNT));
    	assertTrue(PriceList.items.size() == initialItemsSize);
    }
}
