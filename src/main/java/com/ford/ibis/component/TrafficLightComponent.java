package com.ford.ibis.component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ford.ibis.common.Constant;
import com.ford.ibis.model.TrafficLightMetricTypes;

@Component
public class TrafficLightComponent {
	
	private static List<TrafficLightMetricTypes> trafficLightMetricTypes = new ArrayList<>();
	
	public void buildMetricTypes() {
		trafficLightMetricTypes.add(TrafficLightMetricTypes.builder()
				                                           .value(Constant.BDNP_V)
				                                           .label(Constant.VALUES).build());
		trafficLightMetricTypes.add(TrafficLightMetricTypes.builder()
                                                           .value(Constant.BDN_V)
                                                           .label(Constant.UNITS).build());
	} 

	public List<TrafficLightMetricTypes> getTrafficLightMetricTypes() {
		return trafficLightMetricTypes;
	}
	
}
