package com.ford.ibis.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
//import org.primefaces.component.treetable.TreeTable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ford.ibis.model.TrafficLightResponse;
import com.ford.ibis.service.TrafficLightService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/trafficlight")
@AllArgsConstructor
@Slf4j
public class TrafficLightController {

	
	private TrafficLightService trafficLightService;
	
	@CrossOrigin
	@GetMapping("/init/{selectedMarket}/{periodParam}")
	public TrafficLightResponse trafficLightReport(@PathVariable("selectedMarket") final String selectedMarket,
			@PathVariable("periodParam") final String periodParam) {
		
		 long start = System.currentTimeMillis();
		 TrafficLightResponse trafficLightResponse =  trafficLightService.trafficLightReport(selectedMarket, periodParam);
		  log.info("Elapsed time: " + (System.currentTimeMillis() - start));
		  return trafficLightResponse;
	}
	

}
