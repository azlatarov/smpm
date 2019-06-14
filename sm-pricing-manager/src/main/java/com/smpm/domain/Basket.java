package com.smpm.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleUnaryOperator;

import com.smpm.pricing.Discounts;
import com.smpm.pricing.PriceList;

public class Basket {
	private static DoubleUnaryOperator roundOffTo3 = (double d) -> Math.round(d * 1000.00) / 1000.00;
	
	private Map<PurchaseItem, Double> content = new ConcurrentHashMap<>();

	/**
	 * 
	 * @param item
	 * @param amountToAdd - amount can be a weight representation, thus round off to 3
	 * @return
	 */
	public synchronized boolean putInBasket(PurchaseItem item, double amountToAdd) {
		if(content.containsKey(item))
			content.put(item, content.get(item) + roundOffTo3.applyAsDouble(amountToAdd));
		else
			content.put(item, roundOffTo3.applyAsDouble(amountToAdd));
		
		return true;
	}
	
	public synchronized Optional<Double> removeFromBasket(PurchaseItem item, double amountToRemove) {
		if(content.containsKey(item))
			if(content.get(item).doubleValue() > roundOffTo3.applyAsDouble(amountToRemove))
				return Optional.of(content.put(item, content.get(item) - roundOffTo3.applyAsDouble(amountToRemove)));
			else
				return Optional.of(content.remove(item));
		
		return Optional.empty();	
	}
	
	public synchronized Price calculateSubtotal() {
		Double subtotal = content.entrySet().parallelStream()
				.map(e -> {
					PurchaseItem priceListItem = PriceList.itemExists.apply(e.getKey().getName(), e.getKey().getType()).get();
					// TODO - print on receipt
					return new Double(priceListItem.getUnitPrice().getValue()) * e.getValue();
				})
				.reduce(0D, Double::sum);
		
		return Price.getInstance(subtotal);
	}
	
	public synchronized Price calculateTotal() {
		
		Double total = content.entrySet().stream()//.parallelStream()
			.map(e -> {
				Optional<Discount> discount = Discounts.getInstance().getAll()//.parallel()
												.filter(d -> d.getDiscountItem().equals(e.getKey())).findAny();
				PurchaseItem priceListItem = PriceList.itemExists.apply(e.getKey().getName(), e.getKey().getType()).get();
				
				Double finalCost = new Double(priceListItem.getUnitPrice().getValue()) * e.getValue();
				if (discount.isPresent() && e.getValue() > discount.get().getTresholdQuantity()) {
					int timesDiscountApplied = (int) (e.getValue().doubleValue() / discount.get().getTresholdQuantity().doubleValue());
					finalCost += discount.get().getDiscountPrice().getValue() * timesDiscountApplied;
				}
				
				return finalCost;
				})
			.reduce(0D, Double::sum);
		
		return Price.getInstance(total);
	}
}
