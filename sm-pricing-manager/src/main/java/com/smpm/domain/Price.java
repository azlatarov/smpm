package com.smpm.domain;

import com.smpm.functional.Predicates;

import static com.smpm.functional.Predicates.*;

import java.util.function.DoubleUnaryOperator;

import com.smpm.PricingManager;



public class Price {
	private static DoubleUnaryOperator roundOffTo2 = (double d) -> Math.round(d * 100.00) / 100.00;
	
	private double value;
	private static String currencySymbol;
	
	private Price() {}
	
	static {
		currencySymbol = System.getProperty(PricingManager.CURRENCY_SYMBOL_PROPERTY);
	}
	
	public static Price getInstance(double value) throws IllegalArgumentException {
		if (Predicates.<String>isNull().or(isEmpty()).test(currencySymbol))
			throw new IllegalStateException("Highly unlikely, but currency is not set for the application!");
		
		Price instance = new Price();
		return instance.setValue(value);
	}
	
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	public double getValue() {
		return value;
	}
	private Price setValue(double value) {
		this.value = roundOffTo2.applyAsDouble(value);
		return this;
	}
	
	@Override
	public String toString() {
		return this.getCurrencySymbol().concat(this.getValue()+"");
	}

}
