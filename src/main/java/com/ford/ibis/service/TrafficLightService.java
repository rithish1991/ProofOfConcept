package com.ford.ibis.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.ibis.common.Constant;
import com.ford.ibis.component.TrafficLightComponent;
import com.ford.ibis.model.AllMarkets;
import com.ford.ibis.model.DailySalesPerformanceNARecord;
import com.ford.ibis.model.ProductHierarchyRecord;
import com.ford.ibis.model.TrafficLightCurrencyTypes;
import com.ford.ibis.model.TrafficLightMonths;
import com.ford.ibis.model.TrafficLightResponse;
import com.ford.ibis.model.TreeStructure;
import com.ford.ibis.repository.DailySalesDetailRepository;
import com.ford.ibis.repository.ProductHierarchyRepository;
import com.ford.ibis.repository.TrafficLightNARepository;

@Service
public class TrafficLightService {

	@Autowired
	private TrafficLightNARepository trafficLightNARepository;
	@Autowired
	private DailySalesDetailRepository dailySalesDetailRepository;
	@Autowired
	private TrafficLightComponent trafficLightComponent;
	@Autowired
	private TrafficLightTreeService trafficLightTreeService;
	@Autowired
	private ProductHierarchyRepository productHierarchyRepository;
	
	private String selectedMarket;
	private String periodParam;
	private double numberOfWorkingDays;
	private double numberOfWorkingDaysProcessed;
	private List<TrafficLightMonths> allMonths;

	public TrafficLightResponse trafficLightReport(String selectedMarket, String periodParam) {
		 this.selectedMarket = selectedMarket;
		 this.periodParam = periodParam;
		 CompletableFuture<List<TrafficLightMonths>> months = trafficLightNARepository.getAllMonthsInCurrentYear();
		 CompletableFuture<List<AllMarkets>> allDealerChannels = trafficLightNARepository.getAllDlrChannels(selectedMarket);
		 CompletableFuture<List<AllMarkets>> allDealerRegions = trafficLightNARepository.getAllDlrRegions("", "");
		 CompletableFuture<List<AllMarkets>> allDealerZones = trafficLightNARepository.getAllDlrZones("", "", "");
		 CompletableFuture<List<AllMarkets>> allDealer = trafficLightNARepository.getAllDlr("", "", "", "");
		 CompletableFuture<String> selectedMarketDesc = trafficLightNARepository.getMarketDescription(selectedMarket);
		 CompletableFuture<List<AllMarkets>> allDealerGroup = trafficLightNARepository.getAllDlrGroup(selectedMarket);
		 CompletableFuture<Map<String, Date>> postingDates = trafficLightNARepository.fetchPostingDates();
		 CompletableFuture<String> displayChannelsWithoutObjectives = trafficLightNARepository.getChannelsWithoutObjectives("NA", selectedMarket, periodParam);
		 CompletableFuture.allOf(months, allDealerChannels, allDealerRegions, allDealerZones,
				                 allDealer, allDealerGroup, selectedMarketDesc, postingDates, displayChannelsWithoutObjectives).join();
		
		try {
			allMonths = months.get();
			createTreeNodeMLI(allMonths);
			return TrafficLightResponse.builder()
			                           .trafficLightMetricTypes(trafficLightComponent.getTrafficLightMetricTypes())
			                           .trafficLightMonths(allMonths)
					                   .allDealerChannels(allDealerChannels.get())
					                   .allDealerRegions(allDealerRegions.get())
					                   .allDealerZones(allDealerZones.get())
					                   .allDealer(allDealer.get())
					                   .allDealerGroup(allDealerGroup.get())
					                   .selectedMarketDesc(selectedMarketDesc.get())
					                   .displayChannelsWithoutObjectives(displayChannelsWithoutObjectives.get())
					                   .postingDates(postingDates.get())
					                   .postingDateMsg("Sales data as of " + postingDates.get().get(selectedMarket))
					                   .trafficLightCurrencyTypes(buildTraffciLightCurrency(selectedMarket))
					                   .build();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
		  
	}
	
	public List<TrafficLightCurrencyTypes> buildTraffciLightCurrency(String selectedMarket) {
		List<TrafficLightCurrencyTypes> currencyTypes = new ArrayList<>();
		currencyTypes.add(buildCurrenyTypes("USA", "1"));
		currencyTypes.add(buildCurrenyTypes("EURO", "2"));
		currencyTypes.add(buildCurrenyTypes("BER", "4"));
		if (!selectedMarket.equals(Constant.OTH)) {
			currencyTypes.add(buildCurrenyTypes("LC", "3"));
		}
		
		return currencyTypes;
	}
	
	public TrafficLightCurrencyTypes buildCurrenyTypes(String label, String value) {
		return TrafficLightCurrencyTypes.builder()
                .label(label)
                .value(value)
                .build();
	}
	
	
	public TreeNode createTreeNodeMLI(List<TrafficLightMonths> allMonths) {
		
		Map<String, String> productData = null;
		
		if (!selectedMarket.equals(Constant.OTH)) {
			calculateWorkingDays(allMonths);
		}
		productData = getProductDataMap();
		return null;
	}
	
	private Map<String, String> getProductDataMap() {
		Map<String, String> productData = new LinkedHashMap<String, String>();
		ProductHierarchyRecord rootRecord = trafficLightTreeService.getMLITreeNode();
		if(rootRecord != null) {
			Map<String, List<ProductHierarchyRecord>> allProducts = productHierarchyRepository.getAllProducts();
			List<ProductHierarchyRecord> pctList = rootRecord.getChildren();
			List<TreeStructure> treeData = new ArrayList<TreeStructure>();
			for (ProductHierarchyRecord pctRecord : pctList) {
				
				productData.put(pctRecord.getProductCode(), pctRecord.getDescription());
				TreeStructure rootNode = new TreeStructure();
				rootNode.setData(pctRecord);
				List<TreeStructure> cgTreeData = new ArrayList<TreeStructure>();
				List<ProductHierarchyRecord> cgList = trafficLightTreeService.getChildProductHierarchyRecords(allProducts, pctRecord); 
				if (cgList != null) {
					for (ProductHierarchyRecord cgRecord : cgList) {
						productData.put(cgRecord.getProductCode(), cgRecord.getDescription());
						TreeStructure cgRootNode = new TreeStructure();
						cgRootNode.setData(cgRecord);
						List<TreeStructure> mplTreeData = new ArrayList<TreeStructure>();
    					List<ProductHierarchyRecord> mplList = trafficLightTreeService.getChildProductHierarchyRecords(allProducts, cgRecord);
    					if (mplList != null) {
    						for (ProductHierarchyRecord mpl : mplList) {
    							TreeStructure mplRootNode = new TreeStructure();
    							mplRootNode.setData(mpl);
    							productData.put(mpl.getProductCode(), mpl.getDescription());
    							List<TreeStructure> mliTreeData = new ArrayList<TreeStructure>();
    							List<ProductHierarchyRecord> mliList = trafficLightTreeService.getChildProductHierarchyRecords(allProducts, mpl);
    							if (mliList != null) {
    								for (ProductHierarchyRecord mli : mliList) {
    									TreeStructure mliRootNode = new TreeStructure();
    	    							mplRootNode.setData(mli);
    									productData.put(mli.getProductCode(), mli.getDescription());
    									mliTreeData.add(mliRootNode);
    								}
    								mplRootNode.setChildrens(mliTreeData);
    							}
    							mplTreeData.add(mplRootNode);
    						}
    						cgRootNode.setChildrens(mplTreeData);
    					}
    					cgTreeData.add(cgRootNode);
					}
					rootNode.setChildrens(cgTreeData);
				}
				treeData.add(rootNode);
			}
		}
		return productData;
	}

	public void calculateWorkingDays(List<TrafficLightMonths> allMonths) {
		numberOfWorkingDays = dailySalesDetailRepository.getNumberOfWorkingDays(selectedMarket, periodParam, true);
		numberOfWorkingDays = Math.round(numberOfWorkingDays * 100.0) / 100.0;
		if (periodParam.equals(allMonths.get(0).getValue())) {
			List<String> marketList = new ArrayList<>();
			marketList.add(selectedMarket);
			double tempPostedDateWorkingDays = dailySalesDetailRepository.getMarketWorkingDays(marketList, periodParam);
			numberOfWorkingDaysProcessed = Math.round(tempPostedDateWorkingDays * 100.0) / 100.0;
		} else {
			numberOfWorkingDaysProcessed = numberOfWorkingDays;
		}
	}
	
	
	
}
