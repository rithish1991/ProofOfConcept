package com.ford.ibis.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class TrafficLightResponse {

	private List<AllMarkets> allDealerChannels;
	private List<TrafficLightMetricTypes> trafficLightMetricTypes;
	private List<AllMarkets> allDealerRegions;
	private List<AllMarkets> allDealerZones;
	private List<AllMarkets> allDealer;
	private List<AllMarkets> allDealerGroup;
	private List<TrafficLightMonths> trafficLightMonths;
	private List<TrafficLightCurrencyTypes> trafficLightCurrencyTypes;
	private Map<String, Date> postingDates;
	private String selectedMarketDesc;
	private String postingDateMsg;
	private String displayChannelsWithoutObjectives;
}
