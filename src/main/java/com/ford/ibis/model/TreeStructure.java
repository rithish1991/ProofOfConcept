package com.ford.ibis.model;

import java.util.List;

public class TreeStructure {
	
	private DailySalesPerformanceNARecord data;
	private List<TreeStructure> children;
	public DailySalesPerformanceNARecord getData() {
		return data;
	}
	public void setData(DailySalesPerformanceNARecord data) {
		this.data = data;
	}
	public List<TreeStructure> getChildren() {
		return children;
	}
	public void setChildren(List<TreeStructure> children) {
		this.children = children;
	}
	
	
	

}
