package com.smpm.domain;

import com.smpm.functional.Predicates;
import com.smpm.functional.TriPredicate;
import com.smpm.pricing.PriceList;

import static com.smpm.functional.Predicates.*;

import java.util.Optional;
import java.util.function.BiFunction;



public class PurchaseItem {
	private static final double DEFAULT_PRICE = 0.01D;
	public static final TriPredicate<PurchaseItem, String, PurchaseItemType> isEqualTo = 
			(item, name, type) -> item.getName().equalsIgnoreCase(name) && item.getType().equals(type);
	public static final BiFunction<String, PurchaseItemType, Optional<PurchaseItem>> getPurchaseItem = 
		    (name, type) -> PriceList.getInstance().getAllPurchaseItems()
		    				.filter(i -> i.getName().equals(name) && i.getType().equals(type)).findAny();
	
	private String name;
	private PurchaseItemType type;
	private Price unitPrice;
	private PurchaseItemStatus status;
	
	private PurchaseItem() {}
	
	public static PurchaseItem getInstance(String name, PurchaseItemType type, Price unitPrice, PurchaseItemStatus status) 
			throws IllegalArgumentException {
		if (Predicates.<String>isNull().or(isEmpty()).test(name) 
				|| isNull().test(type))
			throw new IllegalArgumentException("Name and type are required purchase item properties. ");
		
		PurchaseItem instance = new PurchaseItem();
		instance.setName(name)
						.setType(type)
						.setUnitPrice(isNull().test(unitPrice) ? Price.getInstance(DEFAULT_PRICE) : unitPrice)
						.setStatus(isNull().test(status) ? PurchaseItemStatus.ACTIVE : status);
		
		/* TODO - allow initial population of price list, but further handling of purchase item should be validated as below
		if(!getPurchaseItem.apply(name, type).isPresent())
			throw new IllegalStateException("The purchase item needs be added to the price list before it can be used.");
		*/
		
		return instance;
	}

	public String getName() {
		return name;
	}

	private PurchaseItem setName(String name) {
		this.name = name;
		return this;
	}

	public PurchaseItemType getType() {
		return type;
	}

	private PurchaseItem setType(PurchaseItemType type) {
		this.type = type;
		return this;
	}

	public Price getUnitPrice() {
		return unitPrice;
	}

	public PurchaseItem setUnitPrice(Price unitPrice) {
		this.unitPrice = unitPrice;
		return this;
	}

	public PurchaseItemStatus getStatus() {
		return status;
	}

	public PurchaseItem setStatus(PurchaseItemStatus status) {
		this.status = status;
		return this;
	}

	@Override
	public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PurchaseItem)) {
            return false;
        }
        
        PurchaseItem item = (PurchaseItem) o;
        
		return isEqualTo.test(item, name, type);
	}
	
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
