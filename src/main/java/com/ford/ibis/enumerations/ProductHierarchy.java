package com.ford.ibis.enumerations;

public enum ProductHierarchy {
	PCT(5),
	CG(4),
	MPL(3),
	MLI(2),
	PARTNUMBER(1),
	ALL(9),
	NULL(0);
	
	private int value;
	private static String description;
	
	private ProductHierarchy(int value) {
		this.value = value;
	}
	
    public int getValue() {
    	return value;
    }
	
    public static ProductHierarchy toEnum(int value) {
        for(ProductHierarchy v : values()) {
            if(v.getValue() == value) {
            	return v;
            }
        }
        throw new IllegalArgumentException();
    }
    
	public String getDescription() {

		String description = null;
		switch(this) {
			case PCT: description = "Product Commodity Team"; break;
			case CG: description = "Commodity Group"; break;
			case MPL: description = "Marketing Product Line"; break;
			case MLI: description = "Marketing Line Item"; break;
			case PARTNUMBER: description = "Part Number"; break;
			case ALL: description = "All"; break;
			case NULL: description = "None"; break;
		}
		return description;
	}
}
