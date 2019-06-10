package com.smpm;

import static com.smpm.functional.Predicates.isEmpty;

import com.smpm.functional.Predicates;
import com.smpm.pricing.PriceList;

public class PricingManager {
	
	public static final String CURRENCY_SYMBOL_PROPERTY = "CURRENCY_SYMBOL";
	private static final String DEFAULT_CURRENCY_SYMBOL = "BGN";
	
	private PricingManager() {}
	
	public static PricingManager getInstance() {
		return PricingManager.Helper.INSTANCE;
	}
	
	private static class Helper {
		private static final PricingManager INSTANCE = new PricingManager();
	}
	

	public static void main(String[] args) {
		String currency = Predicates.<String>isNull().or(isEmpty()).test(args[0]) ? DEFAULT_CURRENCY_SYMBOL : args[0];
		getInstance().load(currency);
	}
	
	public void load(String currency) {
		System.setProperty(CURRENCY_SYMBOL_PROPERTY, currency);
		
		// TODO
	}

}
