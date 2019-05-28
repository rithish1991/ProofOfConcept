package com.ford.ibis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TrafficLightMetricTypes {
	
	private String value;	
	private String label;

}
