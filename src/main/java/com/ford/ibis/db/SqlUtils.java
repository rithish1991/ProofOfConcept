package com.ford.ibis.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SqlUtils {

	public static class PreStmt {
		
		private static int parameterIndex = 1;
		private static HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		
		public PreStmt() { resetIndex(); }
		
		public static void resetIndex() { 
			parameterIndex = 1;
			params.clear();
		}
		
		public static void buildPrepareStatement(PreparedStatement ps) throws SQLException {

		    Iterator<Map.Entry<Integer, Object>> it = params.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<Integer, Object> pairs = (Map.Entry<Integer, Object>)it.next();
		        ps.setObject(pairs.getKey(), pairs.getValue());
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		}
		
		
		public static String inClauseAddPlaceholders(int numberOfPlaceholders) {
			
			String placeholders = "";
			for(int i = 1; i <= numberOfPlaceholders; i++) {
				placeholders += "?,";
			}
			
			if(placeholders.length() > 0) {
				placeholders = placeholders.substring(0, placeholders.length()-1);
			}
			
			return placeholders;
		}
		
		public static void inClauseAddValues(PreparedStatement ps, int startIndex, List<Object> values) throws SQLException {
			
			int parameterIndex = startIndex;
			for(Object value: values) {
				ps.setObject(parameterIndex++, value);
			}
		}
		
	}
	
	public static String BuildWherePredicate(String columnName, String columnValue, Boolean isString) {

		String prefixSuffixCharacter = "";
		if(isString) {
			prefixSuffixCharacter = "'";
		}
		
		String sqlPredicate = "";
		if(columnValue != null && columnValue.length() > 0) {
			String operator = " = ";
			if(columnValue.contains("%") || columnValue.contains("_")) {
				operator = " like ";
			}
			sqlPredicate = 
				" and " + 
				columnName + "  " + 
				operator + 
				prefixSuffixCharacter + columnValue + prefixSuffixCharacter + " ";
		}
		
		return sqlPredicate;
	}

	public static String TranslateRegion(String regionAbbreviation) {
		
		String region = "";
		
		if(regionAbbreviation.compareToIgnoreCase("NA") == 0) {
			region = "North America";
		}
		else if(regionAbbreviation.compareToIgnoreCase("SA") == 0) {
			region = "South America";
		}
		else if(regionAbbreviation.compareToIgnoreCase("MEA") == 0) {
			region = "Middle-East/Asia";
		}
		else if(regionAbbreviation.compareToIgnoreCase("EU") == 0 || regionAbbreviation.compareToIgnoreCase("EU1") == 0) {
			region = "Europe";
		}
		
		return region;
	}
	
	public static String getDbInstanceData() {
	     
			
				return "gfin_data";
    }

 

	public static String getDbInstanceView() {
       return "gfin_view";
		
	}

}
