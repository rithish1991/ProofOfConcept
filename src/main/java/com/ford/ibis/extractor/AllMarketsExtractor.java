package com.ford.ibis.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.ford.ibis.repository.TrafficLightNARepository;
import com.ford.ibis.model.AllMarkets;

public final class AllMarketsExtractor implements ResultSetExtractor<List<AllMarkets>> {
	
	 @Override
	 public List<AllMarkets> extractData(ResultSet rs) throws SQLException {
		 List<AllMarkets>  allDlrChannels = new ArrayList<AllMarkets>();
		 while (rs.next()) {				 
			 String tempChannel = rs.getString("channel").trim();				 
			 String lastCharacter = tempChannel.substring(tempChannel.length() - 1, tempChannel.length());				   
			 String displayChannelName = TrafficLightNARepository.hMapDealerChannelMap.get(lastCharacter);
			 allDlrChannels.add(new AllMarkets(tempChannel, displayChannelName));
	    }
	        return allDlrChannels;
	    }

}
