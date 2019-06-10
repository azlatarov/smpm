package com.smpm.domain;

import com.smpm.functional.Predicates;

import static com.smpm.functional.Predicates.*;



public class PurchaseItem {
	private static final double DEFAULT_PRICE = 0.01D;
	
	private String name;
	private PurchaseItemType type;
	private UnitPrice unitPrice;
	private PurchaseItemStatus status;
	
	private PurchaseItem() {}
	
	public static PurchaseItem getInstance(String name, PurchaseItemType type, UnitPrice unitPrice, PurchaseItemStatus status) 
			throws IllegalArgumentException {
		if (Predicates.<String>isNull().or(isEmpty()).test(name) 
				|| isNull().test(type))
			throw new IllegalArgumentException("Name and type are required purchase item properties. ");
		
		PurchaseItem INSTANCE = new PurchaseItem();
		return INSTANCE.setName(name)
						.setType(type)
						.setUnitPrice(isNull().test(unitPrice) ? UnitPrice.getInstance(DEFAULT_PRICE) : unitPrice)
						.setStatus(isNull().test(status) ? PurchaseItemStatus.ACTIVE : status);
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

	public PurchaseItem setUnitPrice(UnitPrice unitPrice) {
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

	@Override
	public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PurchaseItem)) {
            return false;
        }
        
        PurchaseItem item = (PurchaseItem) o;
        
		return item.getName().equalsIgnoreCase(name) && item.getType().equals(type);
	}
	
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
