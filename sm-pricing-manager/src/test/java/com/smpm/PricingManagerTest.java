package com.smpm;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.smpm.domain.PurchaseItemType;
import com.smpm.pricing.PriceList;

import static junit.framework.Assert.*;

public class PricingManagerTest {
	
    @BeforeClass
    public static void prepare() {
    	PricingManager.getInstance().load("€");
    	PriceList.getInstance().indatePurchaseItem("Beans", PurchaseItemType.BY_COUNT, 0.50D);
    	PriceList.getInstance().indatePurchaseItem("Coke", PurchaseItemType.BY_COUNT, 0.70D);
    	PriceList.getInstance().indatePurchaseItem("Roobar w. choco chips", PurchaseItemType.BY_COUNT, 0.80D);
    	PriceList.getInstance().indatePurchaseItem("Avocado", PurchaseItemType.BY_COUNT, 2.10D);
    	PriceList.getInstance().indatePurchaseItem("Avocado", PurchaseItemType.BY_WEIGHT, 5.50D);
    	PriceList.getInstance().indatePurchaseItem("Oranges", PurchaseItemType.BY_WEIGHT, 1.99D);
    	PriceList.getInstance().indatePurchaseItem("Tomatoes", PurchaseItemType.BY_WEIGHT, 1.54D);
    	PriceList.getInstance().indatePurchaseItem("Cucumbers", PurchaseItemType.BY_WEIGHT, 0.89D);
    }
	
    @Test
    public void applicationStartup() {
    	assertNotNull(System.getProperty(PricingManager.CURRENCY_SYMBOL_PROPERTY));
    	assertNotNull(PriceList.getInstance().getAllPurchaseItems().count() > 0);
    }
    
    @Test
    public void addUniquePurchaseItems() {
    	long initialItemsSize = PriceList.getInstance().getAllPurchaseItems().count();
    	String fish = "Fish";
    	
    	assertTrue(PriceList.getInstance().indatePurchaseItem(fish, PurchaseItemType.BY_COUNT, 14.00).isPresent());
    	assertTrue(PriceList.getInstance().getAllPurchaseItems()
    			.filter(i -> i.getName().equals(fish) && i.getType().equals(PurchaseItemType.BY_COUNT)).findAny().isPresent());
    	assertTrue(PriceList.getInstance().getAllPurchaseItems().count() == initialItemsSize + 1);
    	
    	assertTrue(PriceList.getInstance().indatePurchaseItem("Fish", PurchaseItemType.BY_WEIGHT, 14.00).isPresent());
    	assertTrue(PriceList.getInstance().getAllPurchaseItems()
    			.filter(i -> i.getName().equals(fish) && i.getType().equals(PurchaseItemType.BY_WEIGHT)).findAny().isPresent());
    	assertTrue(PriceList.getInstance().getAllPurchaseItems().count() == initialItemsSize + 2);
    	
    	assertTrue(PriceList.getInstance().removePurchaseItem(fish, PurchaseItemType.BY_COUNT).isPresent());
    	assertTrue(PriceList.getInstance().removePurchaseItem(fish, PurchaseItemType.BY_WEIGHT).isPresent());
    	assertTrue(PriceList.getInstance().getAllPurchaseItems().count() == initialItemsSize);
    	assertTrue(PriceList.getInstance().getAllDeletedPurchaseItems()
    			.filter(i -> i.getName().equals(fish) && i.getType().equals(PurchaseItemType.BY_COUNT)).findAny().isPresent());
    	assertTrue(PriceList.getInstance().getAllDeletedPurchaseItems()
    			.filter(i -> i.getName().equals(fish) && i.getType().equals(PurchaseItemType.BY_WEIGHT)).findAny().isPresent());
    }
    
    @Test
    public void addExistingPurchaseItem() {
    	long initialItemsSize = PriceList.getInstance().getAllPurchaseItems().count();
    	String fish = "Fish";
    	double lastPrice = 10.00;
    	
    	assertTrue(PriceList.getInstance().indatePurchaseItem(fish, PurchaseItemType.BY_COUNT, 14.00).isPresent());
    	assertTrue(PriceList.getInstance().getAllPurchaseItems().count() == (initialItemsSize + 1));
    	assertTrue(PriceList.getInstance().indatePurchaseItem(fish, PurchaseItemType.BY_COUNT, lastPrice).isPresent());
    	assertTrue(PriceList.getInstance().getAllPurchaseItems().count() == (initialItemsSize + 1));
    	assertTrue(PriceList.getInstance().getAllPurchaseItems()
    				.filter(i -> i.getName().equals(fish) && i.getType().equals(PurchaseItemType.BY_COUNT)).count() == 1);
    	assertTrue(PriceList.getInstance().getAllPurchaseItems()
    				.filter(i -> i.getName().equals(fish) && i.getType().equals(PurchaseItemType.BY_COUNT)).findAny().get()
    				.getUnitPrice().getValue() == lastPrice);
    	
    	assertTrue(PriceList.getInstance().removePurchaseItem(fish, PurchaseItemType.BY_COUNT).isPresent());
    	assertTrue(PriceList.getInstance().getAllPurchaseItems().count() == initialItemsSize);
    }
}
