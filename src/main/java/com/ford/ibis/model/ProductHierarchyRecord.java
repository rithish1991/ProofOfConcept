package com.ford.ibis.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ProductHierarchyRecord {
	
	private String productCountry;
	private String productCode;
	private ProductHierarchy productLevel;
	private String description;
	private String updateID;
	private Date updateDate;
	private String parentCode;
	private String parentDescr;
	private Map<String, String> parentCodeList = null;
	private ProductHierarchyRecord parent;
	private List<ProductHierarchyRecord> children = null;
	private boolean isSuccessfull;
	
	public ProductHierarchyRecord() {
		productCode = "";
		description = "";
	}

	public ProductHierarchyRecord(ProductHierarchyRecord parent, String productCountry, String productCode,
			ProductHierarchy productLevel, String description, String updateID,
			Date updateDate) {
		this.parent = parent;
		this.productCountry = productCountry;
		this.productCode = productCode;
		this.productLevel = productLevel;
		this.description = description;
		this.updateID = updateID;
		this.updateDate = updateDate;
	}

}
