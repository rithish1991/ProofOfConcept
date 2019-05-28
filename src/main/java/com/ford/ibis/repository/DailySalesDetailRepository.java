package com.ford.ibis.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ford.ibis.querymapper.DailySalesDetailQueryMapper;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DailySalesDetailRepository {
	
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public int getMarketWorkingDays(List<String> marketList, String yearMonth) {
			
			final String METHOD_NAME = "getMarketWorkingDays";
			int postedWorkingDays = 0;
			
			String sqlString = DailySalesDetailQueryMapper.buildMarketWorkingDaysQuery();
			log.info(METHOD_NAME, "Query To Fetch Market Working Days: {}" + sqlString);
			marketList.stream()
			          .filter(market -> !"".equals(market))
			          .forEach(market -> jdbcTemplate.query(sqlString, new Object[] { market, yearMonth },
		                      (rs, rowNum) -> postedWorkingDays +  rs.getInt(1)));
			    	 
			return postedWorkingDays;
	}


	public double getNumberOfWorkingDays(String market, String yearMonth, boolean isMarket) {
		
		final String METHOD_NAME = "getNumberOfWorkingDays";
		int numberOfWorkingDays = 0;		
		String sqlString = DailySalesDetailQueryMapper.buildNumberOfWorkingDaysQuery(isMarket);
		log.info(METHOD_NAME, "Query To Fetch getNumberOfWorkingDays : {}" + sqlString);
		
		Map namedParameters = new HashMap();   
	    namedParameters.put("monthyear", yearMonth);   
	    namedParameters.put("market", market);
	      
	     numberOfWorkingDays =  (int) namedParameterJdbcTemplate.queryForObject(sqlString, namedParameters, (rs, rowNum) -> rs.getInt("DAYCOUNT"));
		 return numberOfWorkingDays;
	}
	
	

}
