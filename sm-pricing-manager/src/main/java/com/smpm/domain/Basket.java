package com.smpm.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

public class Basket {
	private static DoubleUnaryOperator roundOffTo3 = (double d) -> Math.round(d * 1000.00) / 1000.00;
	
	private Map<PurchaseItem, Double> content = new ConcurrentHashMap<>();

	public boolean putInBasket(PurchaseItem item, double amountToAdd) {
		if(content.containsKey(item))
			content.put(item, content.get(item) + roundOffTo3.applyAsDouble(amountToAdd));
		else
			content.put(item, roundOffTo3.applyAsDouble(amountToAdd));
		
		return true;
	}
	
	public Optional<Double> removeFromBasket(PurchaseItem item, double amountToRemove) {
		if(content.containsKey(item))
			if(content.get(item).doubleValue() > roundOffTo3.applyAsDouble(amountToRemove))
				return Optional.of(content.put(item, content.get(item) - roundOffTo3.applyAsDouble(amountToRemove)));
			else
				return Optional.of(content.remove(item));
		
		return Optional.empty();	
	}
	
	public Double calculateSubtotal() {
		return content.values().parallelStream().reduce(0D, Double::sum);
	}
}
