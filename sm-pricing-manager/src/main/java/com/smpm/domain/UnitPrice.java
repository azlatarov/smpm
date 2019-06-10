package com.smpm.domain;

import com.smpm.functional.Predicates;

import static com.smpm.functional.Predicates.*;

import com.smpm.PricingManager;



public class UnitPrice {
	
	private double value;
	private static String currencySymbol;
	
	private UnitPrice() {}
	
	static {
		currencySymbol = System.getProperty(PricingManager.CURRENCY_SYMBOL_PROPERTY);
	}
	
	public static UnitPrice getInstance(double value) throws IllegalArgumentException {
		if (isPositive().negate().test(value)) {
			throw new IllegalArgumentException("Unit price cannot have non-positive value.");
		}
		
		if (Predicates.<String>isNull().or(isEmpty()).test(currencySymbol))
			throw new IllegalStateException("Highly unlikely, but currency is not set for the application!");
		
		UnitPrice INSTANCE = new UnitPrice();
		return INSTANCE.setValue(value);
	}
	
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	public double getValue() {
		return value;
	}
	private UnitPrice setValue(double value) {
		this.value = Math.round(value * 100.00) / 100.00;
		return this;
	}

}
