package com.smpm.domain;

import com.smpm.functional.Predicates;



public class Receipt {
	private Basket basket;
	
	private Receipt() {}
	
	public static Receipt getInstance(Basket basket) {
		if (Predicates.isNull().test(basket))
			throw new IllegalArgumentException("Basket is absolutely required. ");
		
		Receipt instance = new Receipt();
		return instance.setBasket(basket);
	}
	
	private Basket getBasket() {
		return basket;
	}
	private Receipt setBasket(Basket basket) {
		this.basket = basket;
		return this;
	}
	
	public Price calculateTotal() {
		Price subtotal = basket.calculateSubtotal();
		System.out.println("SUBTOTAL : " + subtotal); // TODO - change with more meaningful output
		
		Price total = basket.calculateTotal();
		System.out.println("TOTAL : " + total); // TODO - change with more meaningful output
		
		return total;
	}
}
