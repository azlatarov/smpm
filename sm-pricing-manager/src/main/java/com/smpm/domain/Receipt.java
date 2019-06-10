package com.smpm.domain;

import java.util.stream.Stream;

import com.smpm.pricing.Discount;
import com.smpm.pricing.Discounts;

public class Receipt {
	private Basket basket;
	private Stream<Discount> allActiveDiscounts = Discounts.getInstance().getAll();

	/**	
	public Price calculateTotal() {
		
	}
	*/
}
