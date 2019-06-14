package com.smpm;

import org.junit.BeforeClass;
import org.junit.Test;

import com.smpm.domain.Basket;
import com.smpm.domain.Discount;
import com.smpm.domain.Price;
import com.smpm.domain.PurchaseItem;
import com.smpm.domain.PurchaseItemType;
import com.smpm.domain.Receipt;
import com.smpm.pricing.Discounts;
import com.smpm.pricing.PriceList;

import static junit.framework.Assert.*;

public class PricingManagerTest {
	
    @BeforeClass
    public static void prepare() {
    	PricingManager.getInstance().load("€");
    	PriceList.getInstance().indatePurchaseItem("Beans", PurchaseItemType.BY_COUNT, 0.50D); // i.e. canned beans
    	PriceList.getInstance().indatePurchaseItem("Beans", PurchaseItemType.BY_WEIGHT, 1.50D);
    	PriceList.getInstance().indatePurchaseItem("Coke", PurchaseItemType.BY_COUNT, 0.70D);
    	PriceList.getInstance().indatePurchaseItem("Roobar w. choco chips", PurchaseItemType.BY_COUNT, 0.80D);
    	PriceList.getInstance().indatePurchaseItem("Avocado", PurchaseItemType.BY_COUNT, 0.90D);
    	PriceList.getInstance().indatePurchaseItem("Avocado", PurchaseItemType.BY_WEIGHT, 2.00D);
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
    
    @Test
    public void runReceipt() {
    	String beans = "Beans";
    	String coke = "Coke";
    	String avocado = "Avocado";
    	
    	PurchaseItem beansByCount = PurchaseItem.getPurchaseItem.apply(beans, PurchaseItemType.BY_COUNT).get();
    	PurchaseItem cokeByCount = PurchaseItem.getPurchaseItem.apply(coke, PurchaseItemType.BY_COUNT).get();
    	
    	Basket myBasket = new Basket();
    	myBasket.putInBasket(beansByCount, 5.00);
    	myBasket.putInBasket(cokeByCount, 7.00);
    	myBasket.putInBasket(PurchaseItem.getPurchaseItem.apply(avocado, PurchaseItemType.BY_WEIGHT).get(), 7.40);
    	
    	Discounts.getInstance().add(Discount.getInstance("Beans (canned) 3 for 2", beansByCount, 3.00, beansByCount.getUnitPrice()));
    	Discounts.getInstance().add(Discount.getInstance("Coke 2 for " + (Price.getInstance(1.00)).toString(),
    																	  cokeByCount, 2.00, Price.getInstance(-0.40)));
    	
    	Discounts.getInstance().getAll().forEach(d -> System.out.println(d));
    	
    	Receipt myReceipt = Receipt.getInstance(myBasket);
    	
    	assertEquals(22.20, myBasket.calculateSubtotal().getValue(), 0.00);
    	assertEquals(20.50, myReceipt.calculateTotal().getValue(), 0.00);
    }
}
