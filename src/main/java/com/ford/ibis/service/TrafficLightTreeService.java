package com.ford.ibis.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ford.ibis.common.Constant;
import com.ford.ibis.enumerations.ProductHierarchy;
import com.ford.ibis.model.DailySalesPerformanceNARecord;
import com.ford.ibis.model.ProductHierarchyRecord;
import com.ford.ibis.querymapper.TrafficLightQueryMapper;
import com.ford.ibis.repository.ProductHierarchyRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TrafficLightTreeService {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	private ProductHierarchyRepository productHierarchyRepository;

	private static final String CLASS_NAME = TrafficLightTreeService.class.getName();
	private static final String PATTERN1 = "#,##0.00;(#,##0.00)";
	public HashMap <String,HashMap <String,Integer>> hmapObjectiveLevels;
	public HashMap<String,String> hMapDealerChannelMap;
	public TrafficLightTreeService()
	{
		
		this.hmapObjectiveLevels = buildObjectiveLevels();
		loadCustomerMap();
	}
	
	public ProductHierarchyRecord getMLITreeNode() {
		final String  METHOD_NAME = "getMLITreeNode";
		log.info(CLASS_NAME, METHOD_NAME);
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.applyPattern(PATTERN1);
		ProductHierarchyRecord productRoot = new ProductHierarchyRecord();
		List<ProductHierarchyRecord> pctList = productHierarchyRepository.getAllPCTRecordsForTrafficLight();
		productRoot.setChildren(pctList);
		Map<String, List<ProductHierarchyRecord>> allProducts = productHierarchyRepository.getAllProducts();
		
		for (ProductHierarchyRecord pct : pctList) {
			pct.setParent(productRoot);
			List<ProductHierarchyRecord> cgList = getChildProductHierarchyRecords(allProducts, pct);
			pct.setChildren(cgList);
			if (cgList != null) {
				for (ProductHierarchyRecord cg : cgList) {
					cg.setParent(pct);
					List<ProductHierarchyRecord> mplList = getChildProductHierarchyRecords(allProducts, cg);
					cg.setChildren(mplList);
					 if (mplList != null) {
						for (ProductHierarchyRecord mpl : mplList) {
							mpl.setParent(cg); 
							List<ProductHierarchyRecord> mliList = getChildProductHierarchyRecords(allProducts, mpl);
							mpl.setChildren(mliList);
							if (mliList != null) {
								for (ProductHierarchyRecord mli : mliList) {
									mli.setParent(mpl);
									mli.setChildren(null);
								}
							}
						}
					}
				}
			}
		}
		
		return productRoot;
	}

	
	public List<ProductHierarchyRecord> getChildProductHierarchyRecords(Map<String, List<ProductHierarchyRecord>> allProducts, ProductHierarchyRecord parent) {
		String key = parent.getProductLevel().getValue() + parent.getProductCode().trim();			
		return allProducts.get(key);
	}
	public Map<String, List<DailySalesPerformanceNARecord>> getTLRLevelData(String selectedMarket, String selectedMonth, boolean strObjDisplayFlag,
			ProductHierarchy productType, String selectedCurrencyType) {
		
		final String METHOD_NAME = "getTLRLevelData";
		log.info(CLASS_NAME, METHOD_NAME);
		HashMap<String,List<Integer>> hMapCountryPostedDateWorkingDaysNWorkingDays = new HashMap<String,List<Integer>>();		
		log.info("hMapCountryPostedDateWorkingDaysNWorkingDays--"+hMapCountryPostedDateWorkingDaysNWorkingDays);
		Map<String, List<DailySalesPerformanceNARecord>> dataMap = new LinkedHashMap<String, List<DailySalesPerformanceNARecord>>();
		String sql = TrafficLightQueryMapper.buildQueryForTLRUnitsData( selectedMarket,  selectedMonth,  strObjDisplayFlag, productType,  selectedCurrencyType);
		jdbcTemplate.query(sql, (ResultSet rs) ->{
			processResultSet(rs, dataMap, productType, strObjDisplayFlag, hMapCountryPostedDateWorkingDaysNWorkingDays,
					true, selectedCurrencyType);
		});
		return dataMap;
	}
	private void processResultSet(ResultSet rs,	Map<String, List<DailySalesPerformanceNARecord>> dataMap,
			ProductHierarchy productType, boolean objectiveDisplayFlag, HashMap<String,List<Integer>> hMapTemp,
			boolean isDivideBy1000, String selectedCurrencyType) throws SQLException {
		
		DailySalesPerformanceNARecord record = new DailySalesPerformanceNARecord();
		DecimalFormat decimalFormatter = new DecimalFormat("#,###.0;(#,###.0)");
		DecimalFormat numberFormatter = new DecimalFormat("#,###;(#,###)");

		// Fetching from Result set
		double mtdGrossRev = rs.getDouble("mtdGrossRev");	
		double projGrossRev = rs.getDouble("projGrossRev");
		double warranty = rs.getDouble("warranty");
		double newPrice = rs.getDouble("newPrice");
		double grossCPVPY = rs.getDouble("grossCPVPY");
		// System.out.println("projectedVsPY ###$#$$#$---"+grossCPVPY);
		double grossCPVProjected = rs.getDouble("grossCPVProjected");
		// System.out.println("grossCPVProjected----"+grossCPVProjected);
		double grossCPVObjective = rs.getDouble("grossCPVObjective");
	//	double projectedVsObjective = rs.getDouble("projectedVsObjective");
	    double GrossCPVProjYTD = rs.getDouble("GrossCPVProjYTD");
	   // System.out.println("GrossCPVProjYTD----"+GrossCPVProjYTD);
		
		double projYtdVsPYTD = rs.getDouble("projYtdVsPYTD");
		// System.out.println("projYtdVsPYTD----"+projYtdVsPYTD);
		double avg5 = rs.getDouble("last5daysAmt");// Changes 08/21
		double avg20 = rs.getDouble("last20daysAmt");// Changes 08/21

		// Local currency calculation
		double localRate = rs.getDouble("MONEX_LOCAL_RATE_R");
		// Local currency calculation
		double euroRate = rs.getDouble("MONEX_EURO_RATE_R");
		double berRate = rs.getDouble("MONEX_BUDG_RATE_R");
		
		// Variable for calculation
		double projectedVsObjective = 0;  // derived value 
		double projectedVsPY = 0;
		double ytdVsPY = 0;  // derives value
		double average5 = 0;
		double average20 = 0;
		
		/*// Changes 08/21 */
		if (avg5 != 0) {
			average5 = avg5 / 5;
		}
		if (avg20 != 0) {
			average20 = avg20 / 20;
		}
		
		if (grossCPVObjective != 0 ) {
			projectedVsObjective = (grossCPVProjected / grossCPVObjective) * 100;
		}
		else{
			projectedVsObjective=0;
		}
		
		if(projYtdVsPYTD !=0){
			ytdVsPY = ((GrossCPVProjYTD / projYtdVsPYTD) * 100);//tpytpycmd
			if (ytdVsPY >= 102.5) {
				record.setYtdVsPYImagePath("/resources/images/green.gif");
			} else if (ytdVsPY < 97.5) {
				record.setYtdVsPYImagePath("/resources/images/red.gif");
			} else {
				record.setYtdVsPYImagePath("/resources/images/yellow.gif");
			}
		}else{
			record.setYtdVsPYImagePath("/resources/images/yellow.gif");
		}
		
		
		if (grossCPVPY != 0) {
			projectedVsPY =  grossCPVProjected/grossCPVPY * 100;
		
			if (projectedVsPY >= 102.5){
				record.setImgPath("/resources/images/green.gif");
			} else if (projectedVsPY < 97.5) {
				record.setImgPath("/resources/images/red.gif");
			} else {
				record.setImgPath("/resources/images/yellow.gif");
			}
		} else {
			record.setImgPath("/resources/images/yellow.gif");
		}
		if (average20 != 0) {//Changes 08/21
			if ((average5 / average20) >= 1.025) {//Changes 08/211.076
				record.setArrowPath("/resources/images/over.gif");
			} else if ((average5 / average20) < 0.975) {//Changes 08/21
				record.setArrowPath("/resources/images/under.gif");
			} else {
				record.setArrowPath("/resources/images/right.gif");
			}
		} 
		else {
				record.setArrowPath("/resources/images/right.gif");
		}
		
		if (selectedCurrencyType.equals("3") && localRate != 0) {
			mtdGrossRev = mtdGrossRev / localRate;		
			projGrossRev = projGrossRev / localRate;
			warranty = warranty / localRate;
			newPrice = newPrice / localRate;
			grossCPVPY = grossCPVPY / localRate;
			grossCPVProjected = grossCPVProjected / localRate;
			grossCPVObjective = grossCPVObjective / localRate;
			GrossCPVProjYTD = GrossCPVProjYTD / localRate; //tcytcmd
			projYtdVsPYTD = projYtdVsPYTD / localRate;			
			avg5 = avg5 / localRate;
			avg20 = avg20 / localRate;
		}else if(selectedCurrencyType.equals("2")){
			mtdGrossRev = mtdGrossRev / euroRate;		
			projGrossRev = projGrossRev / euroRate;
			warranty = warranty / euroRate;
			newPrice = newPrice / euroRate;
			grossCPVPY = grossCPVPY / euroRate;
			grossCPVProjected = grossCPVProjected / euroRate;
			grossCPVObjective = grossCPVObjective / euroRate;
			GrossCPVProjYTD = GrossCPVProjYTD / euroRate; //tcytcmd
			projYtdVsPYTD = projYtdVsPYTD / euroRate;			
			avg5 = avg5 / euroRate;
			avg20 = avg20 / euroRate;
		}
		else if(selectedCurrencyType.equals("4")){
			mtdGrossRev = mtdGrossRev / berRate;		
			projGrossRev = projGrossRev / berRate;
			warranty = warranty / berRate;
			newPrice = newPrice / berRate;
			grossCPVPY = grossCPVPY / berRate;
			grossCPVProjected = grossCPVProjected / berRate;
			grossCPVObjective = grossCPVObjective / berRate;
			GrossCPVProjYTD = GrossCPVProjYTD / berRate; //tcytcmd
			projYtdVsPYTD = projYtdVsPYTD / berRate;			
			avg5 = avg5 / berRate;
			avg20 = avg20 / berRate;
		}
		// If All Dealers is selected then divide by 1000
		if (isDivideBy1000) {
			mtdGrossRev = mtdGrossRev / 1000;
		
			projGrossRev = projGrossRev / 1000;
			warranty = warranty / 1000;
			newPrice = newPrice / 1000;
			grossCPVPY = grossCPVPY / 1000;
			grossCPVProjected = grossCPVProjected / 1000;
			grossCPVObjective = grossCPVObjective / 1000;
			GrossCPVProjYTD = GrossCPVProjYTD / 1000;
			projYtdVsPYTD = projYtdVsPYTD / 1000;		
			avg5 = avg5 / 1000;
			avg20 = avg20 / 1000;
		}
	
		if (mtdGrossRev != 0.0) {
			record.setMtdGrossRev(numberFormatter.format(mtdGrossRev));
		} else {
			record.setMtdGrossRev("");
		}
		if (projGrossRev != 0.0) {
			record.setProjGrossRev(numberFormatter.format(projGrossRev));
		} else {
			record.setProjGrossRev("");
		}
		if (warranty != 0.0) {
			record.setWarranty(numberFormatter.format(warranty));
		} else {
			record.setWarranty("");
		}
		if (newPrice != 0.0) {
			record.setNewPrice(numberFormatter.format(newPrice));
		} else {
			record.setNewPrice("");
		}
		if(grossCPVProjected != 0.0){
			record.setGrossCPVProjected(numberFormatter.format(grossCPVProjected));
		} else {
			record.setGrossCPVProjected("");
		}
		
		if(objectiveDisplayFlag){
			if(grossCPVObjective != 0.0){
				record.setGrossCPVObjective((numberFormatter.format(grossCPVObjective)));
			} else {
				record.setGrossCPVObjective("");
			}
			if (projectedVsObjective != 0.0) {   // Proj vs Obj
				record.setProjectedVsObjective(numberFormatter.format(projectedVsObjective) + "%");
			} else {
				record.setProjectedVsObjective("");
			}
		} else {
			record.setGrossCPVObjective("");
			record.setProjectedVsObjective("");
		}
		
		if (grossCPVPY != 0.0) {
			record.setGrossCPVPY(numberFormatter.format(grossCPVPY));
		} else {
			record.setGrossCPVPY("");
		}

		if (grossCPVProjected < 0 && grossCPVPY < 0) {
			record.setProjectedVsPY("N/A%");
			record.setImgPath("/resources/images/red.gif");
		} else if (projGrossRev != 0 && (grossCPVPY == 0 || grossCPVPY < 0)) {
			record.setProjectedVsPY("N/A%");
			record.setImgPath("/resources/images/green.gif");
		} else if (projectedVsPY > 0.0) {
			record.setProjectedVsPY(decimalFormatter.format(projectedVsPY) + "%");
		} else if (projectedVsPY < 0.0) {
			record.setProjectedVsPY("N/A%");
		} else {
			record.setProjectedVsPY("");
		}
		
		if (GrossCPVProjYTD != 0.0) {
			record.setYtd(numberFormatter.format(GrossCPVProjYTD));
		} else {
			record.setYtd("");
		}
		
		if (ytdVsPY != 0.0) {
			record.setYtdVsPY(decimalFormatter.format(ytdVsPY) + "%");
		} else {
			record.setYtdVsPY("");
		}
		
		String key = "";
		
		if (productType == null) {
			key = Constant.GRAND_TOTAL;
			record.setMarket(rs.getString("GFDG40_PCT_C").trim());
		} else if (productType.equals(ProductHierarchy.PCT)) {
			key = Constant.PCT;
			record.setBusinessUnit(rs.getString("GFDG40_PCT_C").trim());
		} else if (productType.equals(ProductHierarchy.CG)) {
			key = rs.getString("GFDG40_PCT_C").trim();
			record.setBusinessUnit(rs.getString("GFDG40_CG_C").trim());
		} else if (productType.equals(ProductHierarchy.MPL)) {
			String pct = rs.getString("GFDG40_PCT_C").trim();
			String cg = rs.getString("GFDG40_CG_C").trim();
			record.setBusinessUnit(rs.getString("GFDG40_MPL_C").trim());
			key = pct + cg;
		} else if (productType.equals(ProductHierarchy.MLI)) {
			String pct = rs.getString("GFDG40_PCT_C").trim();
			String cg = rs.getString("GFDG40_CG_C").trim();
			String mpl = rs.getString("GFDG40_MPL_C").trim();
			record.setBusinessUnit(rs.getString("GFDG40_MLI_C").trim());
			key = pct + cg + mpl;
		}
		if (dataMap.containsKey(key)) {
			dataMap.get(key).add(record);
		} else {
			List<DailySalesPerformanceNARecord> data = new ArrayList<DailySalesPerformanceNARecord>();
			data.add(record);
			dataMap.put(key, data);
		}

	}
	public Map<String, List<DailySalesPerformanceNARecord>> getFilteredTLRUnits(String selectedMarket, String selectedMonth,
			ProductHierarchy productType, String selectedDlrChannel, String selectedDlrRegion, 
			String selectedDlrZone, String selectedDealers) {
		
		
		final String METHOD_NAME = "getFilteredTLRData";
		log.info(CLASS_NAME, METHOD_NAME);
		Map<String, List<DailySalesPerformanceNARecord>> dataMap = new LinkedHashMap<String, List<DailySalesPerformanceNARecord>>();
		int objectiveAtProductLevel = 0;
		boolean displayObjectives = true;
		if(!"".equals(selectedMarket) && !"".equals(selectedDlrChannel)){
			
			if (hmapObjectiveLevels.containsKey(selectedMarket)){
				
				HashMap<String,Integer> hmapTemp = new HashMap<String,Integer>();
				
				hmapTemp = hmapObjectiveLevels.get(selectedMarket);
				
				if(hmapTemp.containsKey(selectedDlrChannel)){			
					
					objectiveAtProductLevel = hmapTemp.get(selectedDlrChannel);
				}else{
					
					objectiveAtProductLevel = hmapTemp.get("OTHER");
				}
				
				
			}
		}

			log.info("PRODUCT TYPE VALUE ::::::::"+productType.getValue());
			
			if (objectiveAtProductLevel >= productType.getValue() ){
				
				displayObjectives = false;
			}
			String sql =  TrafficLightQueryMapper.buildQueryForFilteredTLRUnits(productType, selectedDlrChannel, selectedDlrRegion, selectedDlrZone, selectedDealers,selectedMarket,selectedMonth);
			jdbcTemplate.query(sql, (ResultSet rs) ->{
				
				
				   while (rs.next()) {
					   processUnitResultSet(rs, dataMap, productType, true);
				   }
				
			});
			return dataMap;
		
		
	}
	private void processUnitResultSet(ResultSet rs,	Map<String, List<DailySalesPerformanceNARecord>> dataMap,
			ProductHierarchy productType, boolean objectiveDisplayFlag)			 throws SQLException {
		
		
		
		DailySalesPerformanceNARecord record = new DailySalesPerformanceNARecord();
		DecimalFormat decimalFormatter = new DecimalFormat("#,###.0;(#,###.0)");
		DecimalFormat numberFormatter = new DecimalFormat("#,###;(#,###)");
		// Fetching from Result set
		double mtdGrossRev = rs.getDouble("mtdGrossRev");
	//	double projGrossRev = rs.getDouble("projGrossRev");//blank
		double warranty = rs.getDouble("warranty");
		double grossCPVProjected = rs.getDouble("grossCPVProjected");
		//double newPrice = rs.getDouble("newPrice");//blank
		double grossCPVPY = rs.getDouble("grossCPVPY");
	    double grossProjMtdPy=rs.getDouble("grossProjMtdPy");  // Commented as on 10/11/2018 by Sanjeev as requested by Carlos
		//double grossCPVObjective = rs.getDouble("grossCPVObjective");//blank
		double tcytcmd = rs.getDouble("TCYTCMD");
		double tpytpycmd = rs.getDouble("TPYTPYCMD"); // Commented as on 10/11/2018 by Sanjeev as requested by Carlos
	//	double ytd = rs.getDouble("grossCPVProjYTD");  // Commented as on 10/11/2018 by Sanjeev as requested by Carlos
		double avg5 = rs.getDouble("last5daysAmt");// Changes 08/21
		double avg20 = rs.getDouble("last20daysAmt");// Changes 08/21

	
		// Variable for calculation
		double projectedVsObjective = 0;
		double projectedVsPY = 0;
		double ytdVsPY = 0;
		double average5 = 0;
		double average20 = 0;

		/*// Changes 08/21 */
		if (avg5 != 0) {
			average5 = avg5 / 5;
		}
		if (avg20 != 0) {
			average20 = avg20 / 20;
		}
	
		
		if (tpytpycmd != 0) {
			//ytdVsPY = ((tcytcmd / tpytpycmd) * 100);
			if (tpytpycmd >= 102.5) {
				record.setYtdVsPYImagePath("/resources/images/green.gif");
			} else if (tpytpycmd < 97.5) {
				record.setYtdVsPYImagePath("/resources/images/red.gif");
			} else {
				record.setYtdVsPYImagePath("/resources/images/yellow.gif");
			}
		} else {
			record.setYtdVsPYImagePath("/resources/images/yellow.gif");
		}
		if (grossCPVPY != 0) {
			projectedVsPY =  grossCPVProjected/grossCPVPY * 100;
			if (projectedVsPY >= 102.5){
				record.setImgPath("/resources/images/green.gif");
			} else if (projectedVsPY < 97.5) {
				record.setImgPath("/resources/images/red.gif");
			} else {
				record.setImgPath("/resources/images/yellow.gif");
			}
		} else {
			record.setImgPath("/resources/images/yellow.gif");
		}
		if (average20 != 0) {//Changes 08/21
			if ((average5 / average20) >= 1.025) {//Changes 08/211.076
				record.setArrowPath("/resources/images/over.gif");
			} else if ((average5 / average20) < 0.975) {//Changes 08/21
				record.setArrowPath("/resources/images/under.gif");
			} else {
				record.setArrowPath("/resources/images/right.gif");
			}
		} 
		else {
				record.setArrowPath("/resources/images/right.gif");
		}
		if (mtdGrossRev != 0.0) {
			record.setMtdGrossRev(numberFormatter.format(mtdGrossRev));
		} else {
			record.setMtdGrossRev("");
		}
		/*if (mtdGrossRev != 0) {
			if(hMapPostedDateWorkingDaysNWorkingDays.containsKey(strMarket)){
				List<Integer> workingDays = hMapPostedDateWorkingDaysNWorkingDays.get(strMarket);
				if(workingDays.size()>0){
					double monthWorkingDays = workingDays.get(0);
					double postingDateWorkingDays = workingDays.get(1);
					log.info("postingDateWorkingDays-->"+postingDateWorkingDays);
					//if(postingDateWorkingDays>0)
					projGrossRev = mtdGrossRev/ postingDateWorkingDays * monthWorkingDays ; 
				}
			}
		}*/
		
		if (warranty != 0.0) {
			record.setWarranty(numberFormatter.format(warranty));
		} else {
			record.setWarranty("");
		}
		
		if(grossCPVProjected != 0.0){
			record.setGrossCPVProjected(numberFormatter.format(grossCPVProjected));
		} else {
			record.setGrossCPVProjected("");
		}
		if(objectiveDisplayFlag){
			
			if (projectedVsObjective != 0.0) {   // Proj vs Obj
				record.setProjectedVsObjective(numberFormatter.format(projectedVsObjective) + "%");
			} else {
				record.setProjectedVsObjective("");
			}
		} else {
				record.setProjectedVsObjective("");
		}
		if (grossCPVPY != 0.0) {
			record.setGrossCPVPY(numberFormatter.format(grossCPVPY));
		} else {
			record.setGrossCPVPY("");
		}

		// Commented below condition as on 10/11/2018 by Sanjeev as requested by Carlos.
		
		record.setProjectedVsPY("");
		
		if (tcytcmd != 0.0) {
			record.setYtd(numberFormatter.format(tcytcmd));
		} else {
			record.setYtd("");
		}
		/* Commented as on 10/11/2018 by Sanjeev */
		
		 record.setYtdVsPY("");

		String key = "";
		if (productType == null) {
			key = Constant.GRAND_TOTAL;
			record.setMarket(rs.getString("GFDG40_PCT_C").trim());
		} else if (productType.equals(ProductHierarchy.PCT)) {
			key = Constant.PCT;
			record.setBusinessUnit(rs.getString("GFDG40_PCT_C").trim());
		} else if (productType.equals(ProductHierarchy.CG)) {
			key = rs.getString("GFDG40_PCT_C").trim();
			record.setBusinessUnit(rs.getString("GFDG40_CG_C").trim());
		} else if (productType.equals(ProductHierarchy.MPL)) {
			String pct = rs.getString("GFDG40_PCT_C").trim();
			String cg = rs.getString("GFDG40_CG_C").trim();
			record.setBusinessUnit(rs.getString("GFDG40_MPL_C").trim());
			key = pct + cg;
		} else if (productType.equals(ProductHierarchy.MLI)) {
			String pct = rs.getString("GFDG40_PCT_C").trim();
			String cg = rs.getString("GFDG40_CG_C").trim();
			String mpl = rs.getString("GFDG40_MPL_C").trim();
			record.setBusinessUnit(rs.getString("GFDG40_MLI_C").trim());
			key = pct + cg + mpl;
		}
		if (dataMap.containsKey(key)) {
			dataMap.get(key).add(record);
		} else {
			List<DailySalesPerformanceNARecord> data = new ArrayList<DailySalesPerformanceNARecord>();
			data.add(record);
			dataMap.put(key, data);
		}
		
	}
	
	
	 private HashMap<String,String> loadCustomerMap(){;
			final String METHOD_NAME = "loadCustomerMap()";
	        log.info(CLASS_NAME, METHOD_NAME);
	    	
	        hMapDealerChannelMap = new HashMap<String, String>(); 	

	        hMapDealerChannelMap.put("1","Dealer");
	        hMapDealerChannelMap.put("2","Warehouse");
	        hMapDealerChannelMap.put("3","Powertrain Dist");    	
	        hMapDealerChannelMap.put("4","FCS");
	        hMapDealerChannelMap.put("5","Special Mkts");
	        hMapDealerChannelMap.put("6","Joint Venture");
	        hMapDealerChannelMap.put("7","Strategic Cust");
	        hMapDealerChannelMap.put("8","Fleets");
	        hMapDealerChannelMap.put("9","FMC");
	        hMapDealerChannelMap.put("A","Other");
	        hMapDealerChannelMap.put("B","Special Cust");
	    	
	    	log.info(CLASS_NAME, METHOD_NAME);	
	    	return hMapDealerChannelMap;    	
	    	
	    }
	 public HashMap <String,HashMap <String,Integer>> buildObjectiveLevels(){
			
		   final String  METHOD_NAME = "buildObjectiveLevels";
		   log.info(CLASS_NAME, METHOD_NAME);
			
		   HashMap <String,HashMap <String,Integer>> hmapObjectiveLevels = new HashMap<String, HashMap<String,Integer>>();
		   
		   HashMap<String,Integer> hmapUSAObjectives = new HashMap<String,Integer>();
		   hmapUSAObjectives.put("DUSA1", 6);
		   hmapUSAObjectives.put("OTHER", 3);
		   
		   HashMap<String,Integer> hmapCANObjectives = new HashMap<String,Integer>();
		   hmapCANObjectives.put("DCAN1", 6);
		   hmapCANObjectives.put("OTHER", 6);
		   
		   HashMap<String,Integer> hmapMEXObjectives = new HashMap<String,Integer>();
		   hmapMEXObjectives.put("DMEX1",5);
		   hmapMEXObjectives.put("OTHER",3);
		   
		   hmapObjectiveLevels.put("USA",hmapUSAObjectives);
		   hmapObjectiveLevels.put("CAN",hmapCANObjectives);
		   hmapObjectiveLevels.put("MEX",hmapMEXObjectives);
		   
		   log.info(CLASS_NAME, METHOD_NAME);
		   return hmapObjectiveLevels;
			
		}
	
	
}
