package com.ford.ibis.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.ford.ibis.repository.TrafficLightNARepository;

public class ChannelsWithoutObjectivesExtractor implements ResultSetExtractor<String> {

	@Override
	public String extractData(ResultSet rs) throws SQLException {
		StringBuffer strBufferChannels = new StringBuffer();
		while (rs.next()) {
			String tempChannel = rs.getString("channel").trim();
			String lastCharacter = tempChannel.substring(tempChannel.length() - 1, tempChannel.length());
			String displayChannelName = "";
			if (!TrafficLightNARepository.hMapDealerChannelMap.isEmpty()) {
				displayChannelName = TrafficLightNARepository.hMapDealerChannelMap.get(lastCharacter);
			}

			if (strBufferChannels.length() == 0) {
				strBufferChannels.append(displayChannelName);
			} else {
				strBufferChannels.append(", " + displayChannelName);
			}
		}
		return strBufferChannels.toString();
	}
}
