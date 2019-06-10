package com.smpm.pricing;

import static com.smpm.functional.Predicates.isEmpty;
import static com.smpm.functional.Predicates.isNull;
import static com.smpm.functional.Predicates.isPositive;

import com.smpm.domain.Price;
import com.smpm.domain.PurchaseItem;
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
		
		return Discount.Helper.INSTANCE
				.setDiscountName(name).setDiscountItem(item).setTresholdQuantity(tresholdQuantity)
				.setDiscountPrice(isPositive().test(priceOff.getValue()) ? Price.getInstance(priceOff.getValue() * -1) : priceOff);
	}
	
	private static class Helper {
		private static final Discount INSTANCE = new Discount();
	}
	
	private String getDiscountName() {
		return discountName;
	}
	private Discount setDiscountName(String discountName) {
		this.discountName = discountName;
		return this;
	}
	private PurchaseItem getDiscountItem() {
		return discountItem;
	}
	private Discount setDiscountItem(PurchaseItem discountItem) {
		this.discountItem = discountItem;
		return this;
	}
	private Double getTresholdQuantity() {
		return tresholdQuantity;
	}
	private Discount setTresholdQuantity(Double tresholdQuantity) {
		this.tresholdQuantity = tresholdQuantity;
		return this;
	}
	private Price getDiscountPrice() {
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
        
        Discount discount = (Discount) o;
        
		return discountItem.equals(discount.getDiscountItem());
	}
	
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + discountItem.hashCode();
        return result;
    }
}
