package com.ford.ibis.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailySalesPerformanceNARecord {

	private String market;
	private String businessUnit;
	private String mtdGrossRev;
	private String projGrossRev;
	private String warranty;
	private String grossCPVObjective;
	private String newPrice;
	private String grossCPVProjected;
	private String projectedVsObjective;
	private String grossCPVPY;
	private String projectedVsPY;
	private String ytd;
	private String ytdVsPY;
	private String imgPath;
	private String arrowPath;
	private String ytdVsPYImagePath;

}
