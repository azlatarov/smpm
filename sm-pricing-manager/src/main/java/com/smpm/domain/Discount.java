package com.smpm.domain;

import static com.smpm.functional.Predicates.isEmpty;
import static com.smpm.functional.Predicates.isNull;

import com.smpm.functional.Predicates;

public class Discount {
	private String discountName;
	private PurchaseItem discountItem;
	private Double tresholdQuantity;
	private Price discountPrice;
	
	private Discount() {}
	
	public static Discount getInstance(String name, PurchaseItem item, Double tresholdQuantity, Price priceOff) 
			throws IllegalArgumentException {
		
		if (Predicates.<String>isNull().or(isEmpty()).test(name) 
				|| isNull().test(item)
				|| isNull().test(tresholdQuantity)
				|| isNull().test(priceOff))
			throw new IllegalArgumentException("All discount properties are required. ");
		
		return new Discount()
				.setDiscountName(name)
				.setDiscountItem(item)
				.setTresholdQuantity(tresholdQuantity)
				.setDiscountPrice(Price.getInstance(Math.abs(priceOff.getValue()) * -1));
	}
	
	public String getDiscountName() {
		return discountName;
	}
	private Discount setDiscountName(String discountName) {
		this.discountName = discountName;
		return this;
	}
	public PurchaseItem getDiscountItem() {
		return discountItem;
	}
	private Discount setDiscountItem(PurchaseItem discountItem) {
		this.discountItem = discountItem;
		return this;
	}
	public Double getTresholdQuantity() {
		return tresholdQuantity;
	}
	private Discount setTresholdQuantity(Double tresholdQuantity) {
		this.tresholdQuantity = tresholdQuantity;
		return this;
	}
	public Price getDiscountPrice() {
		return discountPrice;
	}
	private Discount setDiscountPrice(Price discountPrice) {
		this.discountPrice = discountPrice;
		return this;
	}
    
	@Override
	public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Discount)) {
            return false;
        }
        
        Discount item = (Discount) o;
        
		return discountItem.equals(item.getDiscountItem());
	}
	
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + discountItem.hashCode();
        return result;
    }
	
    @Override
    public String toString() {
    	return ("Discount name : "+discountName+" | Discount Item {name, type} : {"+discountItem.getName()+", "+discountItem.getType()
    			+"} | Discount tresholdQuantity : "+tresholdQuantity+" | discountPrice : "+discountPrice);
    }
}
