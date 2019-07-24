package com.ford.ibis.querymapper;

import com.ford.ibis.common.Constant;
import com.ford.ibis.db.SqlUtils;
import com.ford.ibis.enumerations.ProductHierarchy;


public class TrafficLightQueryMapper {

	public static String buildAllDlrChannelQuery() {
		return new StringBuilder().append(" SELECT DISTINCT MRKT_MARKET_C channel ")
				.append(" FROM " + SqlUtils.getDbInstanceData() + ".SGFDG40_NA_DSPR_TBL ")
				.append(" WHERE GFDG40_DSPLY_CTRY_C = ? ").append(" ORDER BY 1 ").toString();
	}
	
	public static String buildAllMonthsInCurrentYearQuery(int year, String day) {
		
		StringBuilder sqlString = new StringBuilder();
		sqlString.append(" SELECT DISTINCT MSMLI_MNTHYR_Y ");
		sqlString.append(" FROM " + SqlUtils.getDbInstanceData() + ".SGFDM10_MSMLI_TBL");
		if(day.trim().equalsIgnoreCase("00")){
			sqlString.append(" WHERE MSMLI_MNTHYR_Y BETWEEN  ( " + (year-1) + " || '12') AND ( " + year + " || '01') ");
		} else {
			sqlString.append(" WHERE MSMLI_MNTHYR_Y BETWEEN  ( " + year + " || '" + day + "') AND ( " + year + " || '12') ");
		}
		sqlString.append(" ORDER BY MSMLI_MNTHYR_Y DESC");
		return sqlString.toString();
	}
	
	public static String buildAllDealerRegions() {
		return new StringBuilder().append(" SELECT DISTINCT m01.OLEV1_SA_LEVEL1_C region ")
				  .append(" FROM " + SqlUtils.getDbInstanceData() + ".SGFDM01_MKT_TBL m01, ")
				  .append(  SqlUtils.getDbInstanceData() + ".SGFDM14_ACTDLR_TBL m14 ")
				  .append(" WHERE m01.COUNTRY_ISO3_C = m14.COUNTRY_ISO3_C ")
				  .append(" AND m01.MKT_CUSTOMER_ID_C = m14.MKT_CUSTOMER_ID_C ")
				  .append(" AND CHAR2HEXINT(TRIM(m01.MKT_CUSTOMER_ID_C)) <> '00000000000000' ")
				  .append(" AND m01.COUNTRY_ISO3_1_C = ? ")
				  .append(" AND m01.MRKT_MARKET_C = ? ")
				  .append(" AND m01.MKT_CUST_TYPE_C <>  '9' ")
				  .append(" AND m01.MNMKT_MAIN_MKT_C =  'D' ")
				  .append(" ORDER BY m01.OLEV1_SA_LEVEL1_C ; ")
				  .toString();
		
	}
	
	public static String buildAllDealerZones() {
		return new StringBuilder().append(" SELECT	DISTINCT m01.OLEV3_SA_LEVEL3_C zon ")
		           .append(" FROM " + SqlUtils.getDbInstanceData() + ".SGFDM01_MKT_TBL m01, ")
		           .append(  SqlUtils.getDbInstanceData() + ".SGFDM14_ACTDLR_TBL m14 ")
		           .append(" WHERE m01.COUNTRY_ISO3_C = m14.COUNTRY_ISO3_C")
		           .append(" AND m01.MKT_CUSTOMER_ID_C = m14.MKT_CUSTOMER_ID_C ")
		           .append(" AND CHAR2HEXINT(TRIM(m01.MKT_CUSTOMER_ID_C)) <> '00000000000000' ")
		           .append(" AND m01.COUNTRY_ISO3_1_C = ? ")
		           .append(" AND m01.MRKT_MARKET_C = ? ")
		           .append(" AND m01.OLEV1_SA_LEVEL1_C = ? ")
		           .append(" AND m01.MKT_CUST_TYPE_C <>  '9' ")
		           .append(" AND m01.MNMKT_MAIN_MKT_C =  'D' ")
		           .append(" ORDER BY m01.OLEV3_SA_LEVEL3_C ")
				  .toString();
		
	}
	
	public static String buildAllDealer() {
		return new StringBuilder().append("  SELECT	DISTINCT TRIM(m01.MKT_CUSTOMER_ID_C) dealerID, ")
		          .append("	(TRIM(m01.MKT_CUSTOMER_ID_C) || '-' || TRIM(m01.SSA_TRADE_N)) dealerDescription ")
		          .append("  FROM " + SqlUtils.getDbInstanceData() + ".SGFDM01_MKT_TBL m01, ")
		          .append(	SqlUtils.getDbInstanceData() + ".SGFDM14_ACTDLR_TBL m14 ")
		          .append("  WHERE m01.COUNTRY_ISO3_C = m14.COUNTRY_ISO3_C ")
		          .append(" 	AND m01.MKT_CUSTOMER_ID_C = m14.MKT_CUSTOMER_ID_C  ")
		          .append("	AND CHAR2HEXINT(TRIM(m01.MKT_CUSTOMER_ID_C)) <> '00000000000000' ")
		          .append(" 	AND m01.COUNTRY_ISO3_1_C = ?  ")
		          .append(" 	AND m01.MRKT_MARKET_C = ? ")
		          .append(" 	AND m01.OLEV1_SA_LEVEL1_C = ? ")
		          .append(" 	AND m01.OLEV3_SA_LEVEL3_C = ? ")
		          .append("  AND m01.MKT_CUST_TYPE_C <>  '9' ")
		          .append("  AND m01.MNMKT_MAIN_MKT_C =  'D' ")
		          .append("  ORDER BY m01.MKT_CUSTOMER_ID_C ")
				  .toString();
		
	}
	
	public static String buildAllDealerGroup() {
		return new StringBuilder().append(" SELECT	distinct GFDM04_DLR_GRP_C dealerGroup ")
		          .append(" FROM "  + SqlUtils.getDbInstanceData() +  ".SGFDM04_DLR_GROUP_TBL ")
		          .append(" WHERE	COUNTRY_ISO3_C= ? ")
				  .toString();
		
	}
	
	public static String buildMarketDescription() {
		return new StringBuilder().append(" SELECT COUNTRY_NAME_X ")
		          .append(" FROM " + SqlUtils.getDbInstanceData() + ".SGFDC03_CNTRY_TBL ")
		          .append(" WHERE COUNTRY_ISO3_C = ? ")
				  .toString();
		
	}
	
	public static String buildPostingDates() {
		return new StringBuilder().append("SELECT ")
		           .append("GFDG32_MARKET_C marketCode, ")
		           .append("GFDG32_UPDTRAN_Y updatedTnsDate ")
		           .append(" FROM ")
		           .append(SqlUtils.getDbInstanceData() + ".SGFDG32_POSTDATE_TBL")
		           .append(" WHERE GFDA07_REGION_C = 'NA' ")
				  .toString();
		
	}
	
	public static String buildChannelsWithoutObjectives() {
		return new StringBuilder().append("SELECT DISTINCT MRKT_MARKET_C channel ")
		           .append(" FROM " + SqlUtils.getDbInstanceData() + ".SGFDG40_NA_DSPR_TBL ")
		           .append("	WHERE GFDA07_REGION_C = ? ")
		           .append("	AND  GFDG40_DSPLY_CTRY_C = ? ")
		           .append("	AND  GFDG40_MNTHYR_Y = ? ")
		           .append("	AND MRKT_MARKET_C NOT IN ( ")
		           .append("	SELECT DISTINCT  MRKT_MARKET_C ")
		           .append(" FROM "+ SqlUtils.getDbInstanceData() + ".SGFDG41_NA_OBJC_INPUT_TBL ")
		           .append("	WHERE GFDA07_REGION_C = ? ")
		           .append("	AND  GFDG41_MARKET_C = ? ")
		           .append("	AND  GFDG41_MNTHYR_Y = ? ) ")
				  .toString();
		
	}
	
	public static String buildQueryForTLRUnitsData(String selectedMarket, String selectedMonth, boolean strObjDisplayFlag,
			ProductHierarchy productType, String selectedCurrencyType)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT GFDG40_PCT_C, MONEX_LOCAL_RATE_R, MONEX_EURO_RATE_R, MONEX_BUDG_RATE_R, ");
		if (productType.equals(ProductHierarchy.CG)) {
			sql.append("GFDG40_CG_C, ");
		} else if (productType.equals(ProductHierarchy.MPL)) {
			sql.append("GFDG40_CG_C, GFDG40_MPL_C, ");
		} else if (productType.equals(ProductHierarchy.MLI)) {
			sql.append("GFDG40_CG_C, GFDG40_MPL_C, GFDG40_MLI_C, ");
		}
		sql.append("sum(GFDG40_BDN_MTD_A ) mtdGrossRev, "); // Mtd Gross Revenue
		sql.append("sum(GFDG40_BDN_PROJ_A) projGrossRev, "); //Proj. Gross Revenue 
		sql.append("sum(GFDG40_VW_PROJ_A)  warranty, "); // Warranty
		sql.append("sum(GFDG40_NPRC_PROJ_A) newPrice, "); // Pricing
		sql.append("sum(GFDG40_GROSS_CPV_PROJ_A) grossCPVProjected, ");	//Gross CPV Proj.
		sql.append("sum(GFDG40_CPV_OBJ_A) grossCPVObjective, "); //Gross CPV Obj.
		
		sql.append("sum(GFDG40_PROJ_VS_OBJ_A)  projectedVsObjective, "); // Proj. vs Obj.  has to be commented later 10/12
		
		sql.append("sum(GFDG40_CPV_PRIOR_YR_A) grossCPVPY, "); // Gross CPV PY
		
		// Proj. CPV vs PY is a derived value projectedVsPY
		
		sql.append("sum(GFDG40_BDN_TCYTCMD_A) AS GrossCPVProjYTD,");  // Gross CPV Proj. YTD
		
		sql.append("sum(GFDG40_BDN_TPYTPYCMD_A) AS projYtdVsPYTD, "); // Proj. YTD vs PYTD tpytpycmd TPYTPYCMD
		
		sql.append("sum(GFDG40_BDN_LAST5_DAYS_A) last5daysAmt, "); // last 5 days
		
		sql.append("sum(GFDG40_BDN_LAST20_DAYS_A) last20daysAmt "); // last 20 days
		
		sql.append("from " + SqlUtils.getDbInstanceData() + ".SGFDG40_NA_DSPR_TBL ");
		sql.append("WHERE GFDA07_REGION_C='NA' ");
		sql.append("and GFDG40_DSPLY_CTRY_C= 'USA' ");
		sql.append("and GFDG40_MNTHYR_Y='201905' ");
		sql.append("and GFDG40_OBJ_PROD_MKT_LVL_C is not null ");
		sql.append("and MONEX_LOCAL_RATE_R <> 0 ");
	
		
		if(productType.equals(ProductHierarchy.PCT)){
			sql.append("group by GFDG40_PCT_C ");
		}else if (productType.equals(ProductHierarchy.CG)) {
			sql.append(" and  GFDG40_CG_C <>'' group by GFDG40_PCT_C, GFDG40_CG_C ");
		} else if (productType.equals(ProductHierarchy.MPL)) {
			sql.append("and GFDG40_MPL_C <>'' group by GFDG40_PCT_C, GFDG40_CG_C, GFDG40_MPL_C ");
		} else if (productType.equals(ProductHierarchy.MLI)) {
			sql.append("and GFDG40_MLI_C <>'' group by GFDG40_PCT_C, GFDG40_CG_C, GFDG40_MPL_C, GFDG40_MLI_C ");
		}
		sql.append(", MONEX_LOCAL_RATE_R ");
		sql.append(",MONEX_EURO_RATE_R ");
		sql.append(",MONEX_BUDG_RATE_R ");
		
		sql.append("order by GFDG40_PCT_C ");
		if (productType.equals(ProductHierarchy.CG)) {
			sql.append(", GFDG40_CG_C ");
		} else if (productType.equals(ProductHierarchy.MPL)) {
			sql.append(", GFDG40_CG_C, GFDG40_MPL_C ");
		} else if (productType.equals(ProductHierarchy.MLI)) {
			sql.append(", GFDG40_CG_C, GFDG40_MPL_C, GFDG40_MLI_C ");
		}
		return sql.toString();
	}
	
	public static String buildQueryForFilteredTLRUnits(ProductHierarchy productType,String selectedDlrChannel,String selectedDlrRegion,String selectedDlrZone,String selectedDealers,String selectedMarket,String selectedMonth)
	{
		StringBuilder sql =new StringBuilder();
		sql.append(" SELECT GFDG40_PCT_C, ");
		if (productType.equals(ProductHierarchy.CG)) {
			sql.append("GFDG40_CG_C, ");
		} 
		else if (productType.equals(ProductHierarchy.MPL)) {
			sql.append("GFDG40_CG_C, GFDG40_MPL_C, ");
		}
		else if (productType.equals(ProductHierarchy.MLI)) {
			sql.append("GFDG40_CG_C, GFDG40_MPL_C, GFDG40_MLI_C, ");
		}
		sql.append("sum(SALES_GROSS_PART_T) mtdGrossRev, ");
		sql.append("sum(WRNTY_PIECE_PAID_T)  warranty, ");
		sql.append("sum(GFDG40_GROSS_CPV_PROJ_T) as grossCPVProjected, ");
		sql.append("sum(GFDG40_GROSS_CPV_PY_T) grossCPVPY, ");
		sql.append("sum(GFDG40_PROJ_MTD_VS_PY_T) grossProjMtdPy, ");
		sql.append("sum(GFDG40_BDN_TCYTCMD_T) TCYTCMD, ");
		sql.append("sum(GFDG40_PROJ_YTD_VS_PYTD_T) TPYTPYCMD, ");
		sql.append("sum(GFDG40_BDN_LAST5_DAYS_T) last5daysAmt, ");
		sql.append("sum(GFDG40_BDN_LAST20_DAYS_T) last20daysAmt ");
		sql.append("from " + SqlUtils.getDbInstanceData() + ".SGFDG40_NA_DSPR_TBL ");
		sql.append("WHERE GFDA07_REGION_C='NA' ");
		sql.append("and GFDG40_DSPLY_CTRY_C='"+selectedMarket+"' ");
		sql.append("and GFDG40_MNTHYR_Y='"+selectedMonth+"' ");
		if (!selectedDlrChannel.equals(Constant.T) && !selectedDlrChannel.equals(Constant.DEALER_GROUP)) {
			sql.append("and MRKT_MARKET_C='"+selectedDlrChannel+"' ");
		}
		if (!selectedDlrRegion.equals(Constant.T)) {
			sql.append("and OLEV1_SA_LEVEL1_C='"+selectedDlrRegion+"' ");
		}
		if (!selectedDlrZone.equals(Constant.T)) {
			sql.append("and OLEV3_SA_LEVEL3_C= '"+selectedDlrZone+"' ");
		}
		if (!selectedDealers.equals(Constant.T) && selectedDealers.contains(",")) {
			String[] dealers = selectedDealers.split(",");
			sql.append("and MKT_CUSTOMER_ID_C in (? ");
			for (int i = 1; i < dealers.length; i++) {
				if(i == dealers.length-1){
					sql.append(","+dealers[i]+")");
				} else {
					sql.append(","+dealers[i]+"");
				}
			}
		} else if (!selectedDealers.equals(Constant.T)){
			sql.append("and MKT_CUSTOMER_ID_C="+selectedDealers+" ");
		}
		sql.append("group by GFDG40_PCT_C ");
		if (productType.equals(ProductHierarchy.CG)) {
			sql.append(", GFDG40_CG_C ");
		} else if (productType.equals(ProductHierarchy.MPL)) {
			sql.append(", GFDG40_CG_C, GFDG40_MPL_C ");
		} else if (productType.equals(ProductHierarchy.MLI)) {
			sql.append(", GFDG40_CG_C, GFDG40_MPL_C, GFDG40_MLI_C ");
		}
		return sql.toString();
	}
	
	
}
