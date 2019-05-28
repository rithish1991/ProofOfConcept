package com.ford.ibis.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.ford.ibis.common.Constant;
import com.ford.ibis.extractor.AllMarketsExtractor;
import com.ford.ibis.extractor.AllMonthsInYearExtractor;
import com.ford.ibis.extractor.ChannelsWithoutObjectivesExtractor;
import com.ford.ibis.model.AllMarkets;
import com.ford.ibis.model.TrafficLightMonths;
import com.ford.ibis.querymapper.TrafficLightQueryMapper;

import static java.util.stream.Collectors.toList;

import java.sql.ResultSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class TrafficLightNARepository {

	public static HashMap<String, String> hMapDealerChannelMap;
	public HashMap<String, HashMap<String, Integer>> hmapObjectiveLevels;
	private static final String CLASS_NAME = TrafficLightNARepository.class.getName();

	@Autowired
	JdbcTemplate jdbcTemplate;

	public TrafficLightNARepository() {
		log.info(CLASS_NAME);
		this.hmapObjectiveLevels = buildObjectiveLevels();
		loadCustomerMap();
	}

	private static HashMap<String, String> loadCustomerMap() {
		final String METHOD_NAME = "loadCustomerMap()";
		log.info(CLASS_NAME + METHOD_NAME);

		hMapDealerChannelMap = new HashMap<String, String>();

		hMapDealerChannelMap.put("1", "Dealer");
		hMapDealerChannelMap.put("2", "Warehouse");
		hMapDealerChannelMap.put("3", "Powertrain Dist");
		hMapDealerChannelMap.put("4", "FCS");
		hMapDealerChannelMap.put("5", "Special Mkts");
		hMapDealerChannelMap.put("6", "Joint Venture");
		hMapDealerChannelMap.put("7", "Strategic Cust");
		hMapDealerChannelMap.put("8", "Fleets");
		hMapDealerChannelMap.put("9", "FMC");
		hMapDealerChannelMap.put("A", "Other");
		hMapDealerChannelMap.put("B", "Special Cust");

		log.info(CLASS_NAME + METHOD_NAME);
		return hMapDealerChannelMap;

	}

	public HashMap<String, HashMap<String, Integer>> buildObjectiveLevels() {

		final String METHOD_NAME = "buildObjectiveLevels";
		log.info(CLASS_NAME + METHOD_NAME);

		HashMap<String, HashMap<String, Integer>> hmapObjectiveLevels = new HashMap<String, HashMap<String, Integer>>();

		HashMap<String, Integer> hmapUSAObjectives = new HashMap<String, Integer>();
		hmapUSAObjectives.put("DUSA1", 6);
		hmapUSAObjectives.put("OTHER", 3);

		HashMap<String, Integer> hmapCANObjectives = new HashMap<String, Integer>();
		hmapCANObjectives.put("DCAN1", 6);
		hmapCANObjectives.put("OTHER", 6);

		HashMap<String, Integer> hmapMEXObjectives = new HashMap<String, Integer>();
		hmapMEXObjectives.put("DMEX1", 5);
		hmapMEXObjectives.put("OTHER", 3);

		hmapObjectiveLevels.put("USA", hmapUSAObjectives);
		hmapObjectiveLevels.put("CAN", hmapCANObjectives);
		hmapObjectiveLevels.put("MEX", hmapMEXObjectives);

		log.info(CLASS_NAME + METHOD_NAME);
		return hmapObjectiveLevels;

	}

	@Async
	public CompletableFuture<List<AllMarkets>> getAllDlrChannels(String selectedMarket) {
		final String METHOD_NAME = "getAllDlrChannels";
		log.info(CLASS_NAME + METHOD_NAME);
		List<AllMarkets> allDlrChannels = new ArrayList<AllMarkets>();
		// Add the total Dealer channels
		allDlrChannels.add(new AllMarkets("T", Constant.DEALER_CHANNEL));
		String queryAllDlrChannel = TrafficLightQueryMapper.buildAllDlrChannelQuery();
		
		log.info(METHOD_NAME + "Query To Fetch All Dealer channels: {}", queryAllDlrChannel);
		allDlrChannels.addAll(jdbcTemplate.query(queryAllDlrChannel,
				              new Object[] { selectedMarket }, new AllMarketsExtractor()));

		allDlrChannels.add(new AllMarkets(Constant.DEALER_GROUP, Constant.DEALER_GROUP));
		
		return CompletableFuture.completedFuture(allDlrChannels);
	}
	
	@Async
	public CompletableFuture<List<TrafficLightMonths>> getAllMonthsInCurrentYear() {
		final String METHOD_NAME = "getAllMonthsInCurrentYear";
		log.info(CLASS_NAME + METHOD_NAME);
		List<TrafficLightMonths>  allMonths = new ArrayList<>();	
		LocalDate localDate = LocalDate.now();
		int year = localDate.getYear();
		String day = localDate.getMonthValue() < 10 ? "0" + (localDate.getMonthValue() - 1) : (localDate.getMonthValue() - 1) + "" ;
		String sqlString = TrafficLightQueryMapper.buildAllMonthsInCurrentYearQuery(year, day);
		log.info(METHOD_NAME + "Query To Fetch All months in current year: {}", sqlString);
		allMonths.addAll(jdbcTemplate.query(sqlString, new AllMonthsInYearExtractor()));
		
		return CompletableFuture.completedFuture(allMonths);
	}
	
	@Async
	public CompletableFuture<List<AllMarkets>> getAllDlrRegions(String market, String channel) {
		final String  METHOD_NAME = "getDealerRegionPerChannel";
		log.info(CLASS_NAME, METHOD_NAME);
		List<AllMarkets>  allDlrRegionsPerMarket = new ArrayList<>();
		allDlrRegionsPerMarket.add(new AllMarkets("T", Constant.DEALER_REGION));
		if (!market.equals("") && !channel.equals("")) {
			String sqlString = TrafficLightQueryMapper.buildAllDealerRegions();
			log.info(METHOD_NAME + "Query To Fetch All dealer regions: {}", sqlString);
			
			allDlrRegionsPerMarket.addAll(jdbcTemplate.query(sqlString, new Object[] { market, channel },
	                (rs, rowNum) -> new AllMarkets(rs.getString("region").trim(), rs.getString("region").trim())
	        ).stream()
			 .collect(toList()));
		}
		
		return CompletableFuture.completedFuture(allDlrRegionsPerMarket);
	}
	
	@Async
	public CompletableFuture<List<AllMarkets>> getAllDlrZones(String market, String channel, String region) {
		final String  METHOD_NAME = "getAllDlrZones";
		log.info(CLASS_NAME, METHOD_NAME);
		List<AllMarkets>  allDlrZones = new ArrayList<>();
		allDlrZones.add(new AllMarkets("T", Constant.DEALER_ZONE));
		if (!market.equals("") && !channel.equals("") && !region.equals("")) {
			String sqlString = TrafficLightQueryMapper.buildAllDealerZones();
			log.info(METHOD_NAME + "Query To Fetch All dealer zones: {}", sqlString);
			
			allDlrZones.addAll(jdbcTemplate.query(sqlString, new Object[] { market, channel, region },
	                (rs, rowNum) -> new AllMarkets(rs.getString("zon"), rs.getString("zon"))
	        ).stream()
			 .collect(toList()));
		}
		
		return CompletableFuture.completedFuture(allDlrZones);
	}
	
	@Async
	public CompletableFuture<List<AllMarkets>> getAllDlr(String market, String channel, String region, String zone) {
		final String  METHOD_NAME = "getAllDlr";
		log.info(CLASS_NAME, METHOD_NAME);
		List<AllMarkets>  allDlr = new ArrayList<>();
		allDlr.add(new AllMarkets("T", Constant.ALL_DEALER));
		if (!market.equals("") && !channel.equals("") && !region.equals("") && !zone.equals("")) {
			String sqlString = TrafficLightQueryMapper.buildAllDealer();
			log.info(METHOD_NAME + "Query To Fetch All dealer : {}", sqlString);
			
			allDlr.addAll(jdbcTemplate.query(sqlString, new Object[] { market, channel, region, zone },
	                (rs, rowNum) -> new AllMarkets(rs.getString("dealerID"), rs.getString("dealerDescription"))
	        ).stream()
			 .collect(toList()));
		}
		
		return CompletableFuture.completedFuture(allDlr);
	}
	
	@Async
	public CompletableFuture<List<AllMarkets>> getAllDlrGroup(String selectedMarket) {
		final String  METHOD_NAME = "getAllDlrGroup";
		log.info(CLASS_NAME, METHOD_NAME);
		List<AllMarkets>  allDlrGroup = new ArrayList<>();
		allDlrGroup.add(new AllMarkets("T", Constant.DEALER_GROUP));
		
			String sqlString = TrafficLightQueryMapper.buildAllDealerGroup();
			log.info(METHOD_NAME + "Query To Fetch All dealer group: {}", sqlString);
			
			allDlrGroup.addAll(jdbcTemplate.query(sqlString, new Object[] { selectedMarket },
	                (rs, rowNum) -> new AllMarkets(rs.getString("dealerGroup").trim(), rs.getString("dealerGroup").trim())
	        ).stream()
			 .collect(toList()));

		return CompletableFuture.completedFuture(allDlrGroup);
	}
	
	@Async
	public CompletableFuture<String> getMarketDescription(String selectedMarket) {
		final String  METHOD_NAME = "getMarketDescription";
		log.info(CLASS_NAME, METHOD_NAME);
		String  selectedMarketDesc = "";
	    String sqlString = TrafficLightQueryMapper.buildMarketDescription();
		log.info(METHOD_NAME + "Query To Fetch market description: {}", sqlString);
		selectedMarketDesc = jdbcTemplate.queryForObject(sqlString, new Object[] { selectedMarket }
			                                                 , String.class);

		return CompletableFuture.completedFuture(selectedMarketDesc.trim());
	}
	
	@Async
	public CompletableFuture<Map<String, Date>> fetchPostingDates() {
		final String  METHOD_NAME = "fetchPostingDates";
		log.info(CLASS_NAME, METHOD_NAME);
		Map<String, Date> records = new LinkedHashMap<>();
	    String sqlString = TrafficLightQueryMapper.buildPostingDates();
		log.info(METHOD_NAME + "Query To Fetch posting dates: {}", sqlString);
		records = jdbcTemplate.query(sqlString, (ResultSet rs) -> {
		    HashMap<String, Date> results = new LinkedHashMap<>();
		    while (rs.next()) {
		        results.put(rs.getString("marketCode").trim(), rs.getDate("updatedTnsDate"));
		    }
		    return results;
		});

		return CompletableFuture.completedFuture(records);
	}
	
	
	@Async
	public CompletableFuture<String> getChannelsWithoutObjectives(String strRegion, String strMarket, String strMonthYr) {
		final String  METHOD_NAME = "getChannelsWithoutObjectives";
		log.info(CLASS_NAME, METHOD_NAME);
	    String sqlString = TrafficLightQueryMapper.buildChannelsWithoutObjectives();
		log.info(METHOD_NAME + "Query To Fetch channel without objectives: {}", sqlString);
		String  strChannels = "";
		strChannels = jdbcTemplate.query(sqlString, new Object[] { strRegion, strMarket, strMonthYr,
				                                  strRegion, strMarket, strMonthYr } , new ChannelsWithoutObjectivesExtractor());

		return CompletableFuture.completedFuture(strChannels);
	}

}
