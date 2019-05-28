package com.ford.ibis.querymapper;

import com.ford.ibis.db.SqlUtils;


public class DailySalesDetailQueryMapper {

	public static String buildMarketWorkingDaysQuery() {
		return new StringBuilder().append(" SELECT COUNT(*)  FROM "+ SqlUtils.getDbInstanceView()+".SGFDW03 W03 ")  	
    	           .append(" WHERE W03.COUNTRY_ISO3_C  =  ?  AND  W03.WKDAY_MNTHYR_Y =  ?  ")
    	           .append(" AND W03.WKDAY_WKDAY_R  = 1 ")
    	           .append(" AND (W03.GFDG73_PT_SLS_ANLYS_GRP_C   ,W03.COUNTRY_ISO3_C  ,W03.WKDAY_MNTHYR_Y)  ")
    	           .append(" IN ")
    	           .append( "( SELECT GFDA07_REGION_C ,GFDG32_MARKET_C, CAST(CAST(CAST(GFDG32_UPDTRAN_Y  AS DATE FORMAT 'YYYYMM')  AS CHAR(06)) AS INTEGER) ")
    	           .append( " FROM " + SqlUtils.getDbInstanceView() + ".SGFDG32 WHERE W03.WKDAY_DAY_R <=  EXTRACT (DAY FROM GFDG32_UPDTRAN_Y))")
    	           .toString();
	}
	
	public static String buildNumberOfWorkingDaysQuery(boolean isMarket) {
		
		return new StringBuilder().append(" SELECT SUM(WKDAY_WKDAY_R ) DAYCOUNT ")
		           .append(" FROM "+ SqlUtils.getDbInstanceData()+".SGFDW03_WKDAY_TBL  W03 ")
		           .append(" WHERE TRIM(WKDAY_MNTHYR_Y) = :monthyear ")
		           .append(" AND GFDG73_PT_SLS_ANLYS_GRP_C = 'NA' ")
		           .append(isMarket ? " AND COUNTRY_ISO3_C = :market " : "")
		           .append(" AND  WKDAY_WKDAY_R = '1.0000' ")
		           .append(" AND WKDAY_DAY_R <=  31 ")
		           .toString();
	}

}
