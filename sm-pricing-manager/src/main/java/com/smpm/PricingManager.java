package com.smpm;

import com.smpm.pricing.PriceList;

public class PricingManager {
	
	public static final String CURRENCY_SYMBOL_PROPERTY = "CURRENCY_SYMBOL";

	public static void main(String[] args) {
		// TODO - add option to change currency via program parameter
		
		start();
	}
	
	public static void start() {
		System.setProperty(CURRENCY_SYMBOL_PROPERTY, "£");
		PriceList currentList = PriceList.getInstance();
		
		// TODO - handle receipts
	}

}
