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
import com.ford.ibis.enumerations.ProductHierarchy;
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
	private String selectedMonth;
	private String selectedDlrChannel;
	private String selectedDlrRegion;
	private String selectedDlrZone;
	private String selectedDealerGroup;
	private String selectedDealers;
	private String selectedMetricTypeDescr;
	private String selectedCurrencyType;
	private String periodParam;
	private double numberOfWorkingDays;
	private double numberOfWorkingDaysProcessed;
	private List<TrafficLightMonths> allMonths;
	private List<DailySalesPerformanceNARecord> pctLevelData = null;
	private Map<String, List<DailySalesPerformanceNARecord>> cgLevelData = null;
	private Map<String, List<DailySalesPerformanceNARecord>> mplLevelData = null;
	private Map<String, List<DailySalesPerformanceNARecord>> mliLevelData = null;
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
			List<TreeStructure> treeData = createTreeNodeMLI(allMonths);
			return TrafficLightResponse.builder()
			                           .trafficLightMetricTypes(trafficLightComponent.getTrafficLightMetricTypes())
			                           .trafficLightMonths(allMonths)
					                   .allDealerChannels(allDealerChannels.get())
					                   .allDealerRegions(allDealerRegions.get())
					                   .allDealerZones(allDealerZones.get())
					                   .allDealer(allDealer.get())
					                   .trafficLightData(treeData)
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
	
	
	public List<TreeStructure> createTreeNodeMLI(List<TrafficLightMonths> allMonths) {
		
		Map<String, String> productData = null;
		
		if (!selectedMarket.equals(Constant.OTH)) {
			calculateWorkingDays(allMonths);
		}
		productData = getProductDataMap();
		//RithishTo start from here for tree structure
		selectedDlrChannel = Constant.T;
		selectedDlrRegion = Constant.T;
		selectedDlrZone = Constant.T;
		selectedDealers = Constant.T;
		selectedDealerGroup = Constant.T;
		selectedMarket = "NA";
		selectedMonth = "201906";
		selectedMetricTypeDescr = "Units";
		selectedCurrencyType = "1";
		if (!selectedDlrChannel.equals(Constant.T)
				&& !(selectedDlrChannel.equals(Constant.DEALER_GROUP) && selectedDealerGroup.equals(Constant.T))) {
			if (selectedMetricTypeDescr.equals("Units")) {
					
				pctLevelData = trafficLightTreeService.getFilteredTLRUnits(selectedMarket, selectedMonth, ProductHierarchy.PCT,
						selectedDlrChannel, selectedDlrRegion, selectedDlrZone, selectedDealers)
						.get(Constant.PCT);
				//Fetch CG level data
				cgLevelData = trafficLightTreeService.getFilteredTLRUnits(selectedMarket, selectedMonth, ProductHierarchy.CG,
						selectedDlrChannel, selectedDlrRegion, selectedDlrZone, selectedDealers);
				//Fetch MPL level data
				mplLevelData = trafficLightTreeService.getFilteredTLRUnits(selectedMarket, selectedMonth, ProductHierarchy.MPL,
						selectedDlrChannel, selectedDlrRegion, selectedDlrZone, selectedDealers);
				//Fetch MLI level data
				mliLevelData = trafficLightTreeService.getFilteredTLRUnits(selectedMarket, selectedMonth, ProductHierarchy.MLI,
						selectedDlrChannel, selectedDlrRegion, selectedDlrZone, selectedDealers);
			}
			else
			{
				
			}
			
		}
		else
		{
			
			
				pctLevelData = trafficLightTreeService.getTLRLevelData(selectedMarket, selectedMonth, false, ProductHierarchy.PCT, selectedCurrencyType)
						.get(Constant.PCT);
				//Fetch CG level data
				cgLevelData = trafficLightTreeService.getTLRLevelData(selectedMarket, selectedMonth, false, ProductHierarchy.CG, selectedCurrencyType);
				//Fetch MPL level data
				mplLevelData = trafficLightTreeService.getTLRLevelData(selectedMarket, selectedMonth, false, ProductHierarchy.MPL, selectedCurrencyType);
				//Fetch MLI level data
				mliLevelData = trafficLightTreeService.getTLRLevelData(selectedMarket, selectedMonth, false, ProductHierarchy.MLI, selectedCurrencyType);
			
		}
		List<TreeStructure> treeData = new ArrayList<TreeStructure>();
		if (pctLevelData != null) {
			for (DailySalesPerformanceNARecord pctRecord : pctLevelData) {	  
				String pct = pctRecord.getBusinessUnit();
				TreeStructure pctRootNode = new TreeStructure();
			  if(productData.containsKey(pct)){
				pctRecord.setMarket(productData.get(pct));
				pctRootNode.setData(pctRecord);
				List<TreeStructure> cgTreeData = new ArrayList<TreeStructure>();
				 if(cgLevelData.containsKey(pct)){
						List<DailySalesPerformanceNARecord> cgData = cgLevelData.get(pct);
						for (DailySalesPerformanceNARecord cgRecord : cgData) {
							TreeStructure cgRootNode = new TreeStructure();
							String cg = cgRecord.getBusinessUnit();
							String key = pct + cg;
							cgRecord.setMarket(productData.get(cg));
							cgRootNode.setData(cgRecord);
							cgTreeData.add(cgRootNode);
							if(mplLevelData.containsKey(key)){
								List<TreeStructure> mplTreeData = new ArrayList<TreeStructure>();
								List<DailySalesPerformanceNARecord> mplData = mplLevelData.get(key);
								for (DailySalesPerformanceNARecord mplRecord : mplData) {
									TreeStructure mplRootNode = new TreeStructure();
									String mpl = mplRecord.getBusinessUnit();
									String dataKey = pct + cg + mpl;
									mplRecord.setMarket(productData.get(mpl));
									mplRootNode.setData(mplRecord);
									mplTreeData.add(mplRootNode);
									if(mliLevelData.containsKey(dataKey)){
										List<TreeStructure> mliTreeData = new ArrayList<TreeStructure>();
										List<DailySalesPerformanceNARecord> mliData = mliLevelData.get(dataKey);
										for (DailySalesPerformanceNARecord mliRecord : mliData) {
											String mli = mliRecord.getBusinessUnit();
											TreeStructure mliRootNode = new TreeStructure();
											mliRecord.setMarket(mli + "-" + productData.get(mli));
											mliRootNode.setData(mliRecord);
											mliTreeData.add(mliRootNode);
										}
										mplRootNode.setChildren(mliTreeData);
									}
								}
								cgRootNode.setChildren(mplTreeData);
							}
							}
						pctRootNode.setChildren(cgTreeData);
				 	}
			  }
			  treeData.add(pctRootNode);
			}
		}
		return treeData;
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
//				rootNode.setData(pctRecord);
				List<TreeStructure> cgTreeData = new ArrayList<TreeStructure>();
				List<ProductHierarchyRecord> cgList = trafficLightTreeService.getChildProductHierarchyRecords(allProducts, pctRecord); 
				if (cgList != null) {
					for (ProductHierarchyRecord cgRecord : cgList) {
						productData.put(cgRecord.getProductCode(), cgRecord.getDescription());
						TreeStructure cgRootNode = new TreeStructure();
//						cgRootNode.setData(cgRecord);
						List<TreeStructure> mplTreeData = new ArrayList<TreeStructure>();
    					List<ProductHierarchyRecord> mplList = trafficLightTreeService.getChildProductHierarchyRecords(allProducts, cgRecord);
    					if (mplList != null) {
    						for (ProductHierarchyRecord mpl : mplList) {
    							TreeStructure mplRootNode = new TreeStructure();
//    							mplRootNode.setData(mpl);
    							productData.put(mpl.getProductCode(), mpl.getDescription());
    							List<TreeStructure> mliTreeData = new ArrayList<TreeStructure>();
    							List<ProductHierarchyRecord> mliList = trafficLightTreeService.getChildProductHierarchyRecords(allProducts, mpl);
    							if (mliList != null) {
    								for (ProductHierarchyRecord mli : mliList) {
    									TreeStructure mliRootNode = new TreeStructure();
//    	    							mplRootNode.setData(mli);
    									productData.put(mli.getProductCode(), mli.getDescription());
    									mliTreeData.add(mliRootNode);
    								}
    								mplRootNode.setChildren(mliTreeData);
    							}
    							mplTreeData.add(mplRootNode);
    						}
    						cgRootNode.setChildren(mplTreeData);
    					}
    					cgTreeData.add(cgRootNode);
					}
					rootNode.setChildren(cgTreeData);
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
