package com.ford.ibis.querymapper;

import com.ford.ibis.db.SqlUtils;

public class ProductHierarchyQueryMapper {
	
	public static String buildAllPCTRecordsForTrafficLight() {
		return new StringBuilder().append(" SELECT P02.PTHR_PRODUCT_C productCode, ")  	
    	           .append(" P02.PTHR_DESC_X productDescription, ")
    	           .append(" P02.PTHR_UPDATE_ID_C updateID, ")
    	           .append(" P02.PTHR_UPDATE_Y updateDate ")
    	           .append("  FROM " + SqlUtils.getDbInstanceData()	+ ".SGFDP02_PTHR_TBL P02 ")
    	           .append(" WHERE P02.COUNTRY_ISO3_C = ? AND P02.PTHR_HIRCHY_C = ? ")
    	           .append( "  ORDER BY (case PTHR_PRODUCT_C WHEN 'L' THEN '1' WHEN 'M' THEN '2' WHEN 'P' ")
    	           .append(" THEN '3' WHEN 'C' THEN '4' WHEN 'A' THEN '5' WHEN 'U' THEN '6' WHEN 'O' THEN '7' ")
    	           .append(" WHEN 'B' THEN '8'	 END ) ")
    	           .toString();
	}

	public static String buildAllProductsQuery() {
		return new StringBuilder().append(" SELECT P02.PTHR_PRODUCT_C productCode, ")  	
 	           .append(" P02.PTHR_HIRCHY_C productLevel,")
 	           .append(" P02.PTHR_DESC_X productDescription, ")
 	           .append(" PTHR_PRODUCT_1_C parentCode ")
 	           .append("  FROM " + SqlUtils.getDbInstanceData()	+ ".SGFDP02_PTHR_TBL P02 ")
 	           .append(" LEFT OUTER JOIN " + SqlUtils.getDbInstanceData() + ".SGFDP05_PDSTR_TBL P05 ")
 	           .append(" ON P02.PTHR_HIRCHY_C = P05.PTHR_HIRCHY_C ")
 	           .append( " AND P02.PTHR_PRODUCT_C = P05.PTHR_PRODUCT_C ")
 	           .append(" WHERE  P02.COUNTRY_ISO3_C = ? ")
 	           .append(" and P02.PTHR_HIRCHY_C  in (2,3,4,5) ")
 	           .append(" ORDER BY P02.PTHR_HIRCHY_C, P02.PTHR_PRODUCT_C ")
 	           .toString();
	}

}
