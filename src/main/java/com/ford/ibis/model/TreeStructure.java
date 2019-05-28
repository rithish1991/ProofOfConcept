package com.ford.ibis.model;

import java.util.List;

public class TreeStructure {
	
	
	public ProductHierarchyRecord data;
	public List<TreeStructure> childrens;
	public ProductHierarchyRecord getData() {
		return data;
	}
	public void setData(ProductHierarchyRecord data) {
		this.data = data;
	}
	public List<TreeStructure> getChildrens() {
		return childrens;
	}
	public void setChildrens(List<TreeStructure> childrens) {
		this.childrens = childrens;
	}
	
	
	
}
