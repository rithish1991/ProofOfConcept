package com.ford.ibis.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.ford.ibis.model.TrafficLightMonths;

public class AllMonthsInYearExtractor implements ResultSetExtractor<List<TrafficLightMonths>>{

	 @Override
	 public List<TrafficLightMonths> extractData(ResultSet rs) throws SQLException {
		 List<TrafficLightMonths>  allMonthsInYear = new ArrayList<>();
		 while (rs.next()) {				 
			 String period = rs.getString("MSMLI_MNTHYR_Y");
			 int month = Integer.parseInt(period.substring(4));				   
			 allMonthsInYear.add(TrafficLightMonths.builder()
					                               .value(period)
					                               .label(Month.of(month).name() + " " + period.substring(0, 4))
					                               .build());
	    }
	        return allMonthsInYear;
	    }
}
