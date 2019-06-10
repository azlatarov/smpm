package com.smpm.domain;

import com.smpm.functional.Predicates;

import static com.smpm.functional.Predicates.*;



public class PurchaseItem {
	private String name;
	private PurchaseItemType type;
	private UnitPrice unitPrice;
	private PurchaseItemStatus status;
	
	private PurchaseItem() {}
	
	public static PurchaseItem getInstance(String name, PurchaseItemType type, UnitPrice unitPrice, PurchaseItemStatus status) 
			throws IllegalArgumentException {
		if (Predicates.<String>isNull().or(isEmpty()).test(name) 
				|| isNull().test(type) 
				|| isNull().test(unitPrice))
			throw new IllegalArgumentException("Name, type and price are required purchase item properties. ");
		
		// TODO - handle situation where item with same name exists
		
		if (isNull().test(status))
			status = PurchaseItemStatus.ACTIVE;
		
		PurchaseItem INSTANCE = new PurchaseItem();
		return INSTANCE.setName(name).setType(type).setUnitPrice(unitPrice);
	}

	public String getName() {
		return name;
	}

	private PurchaseItem setName(String name) {
		this.name = name;
		return this;
	}

	public PurchaseItemType getType() {
		return type;
	}

	private PurchaseItem setType(PurchaseItemType type) {
		this.type = type;
		return this;
	}

	public UnitPrice getUnitPrice() {
		return unitPrice;
	}

	private PurchaseItem setUnitPrice(UnitPrice unitPrice) {
		this.unitPrice = unitPrice;
		return this;
	}

	public PurchaseItemStatus getStatus() {
		return status;
	}

	public PurchaseItem setStatus(PurchaseItemStatus status) {
		this.status = status;
		return this;
	}

}
