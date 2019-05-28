package com.ford.ibis.querymapper;

import com.ford.ibis.db.SqlUtils;


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
	
}
