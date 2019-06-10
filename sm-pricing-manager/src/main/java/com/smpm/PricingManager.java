package com.smpm;

import com.smpm.pricing.PriceList;

public class PricingManager {
	
	public static final String CURRENCY_SYMBOL_PROPERTY = "CURRENCY_SYMBOL";

	public static void main(String[] args) {
		// TODO - add option to change currency via program parameter
		
		run();
	}
	
	public static void run() {
		System.setProperty(CURRENCY_SYMBOL_PROPERTY, "£");
		PriceList.initialize();
		
		// TODO - prepare for servicing purchase requests
	}

}
