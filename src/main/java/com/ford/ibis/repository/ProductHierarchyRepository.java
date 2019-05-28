package com.ford.ibis.repository;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ford.ibis.extractor.AllProductExtractor;
import com.ford.ibis.model.ProductHierarchy;
import com.ford.ibis.model.ProductHierarchyRecord;
import com.ford.ibis.querymapper.ProductHierarchyQueryMapper;

import lombok.extern.slf4j.Slf4j;


@Repository
@Slf4j
public class ProductHierarchyRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<ProductHierarchyRecord> getAllPCTRecordsForTrafficLight() {
		final String METHOD_NAME = "getAllPCTRecords";
		List<ProductHierarchyRecord> allPCTRecords = new ArrayList<ProductHierarchyRecord>();
		String sqlString = ProductHierarchyQueryMapper.buildAllPCTRecordsForTrafficLight();
		log.info(METHOD_NAME, "Query to fetch All PCT Records: {}" + sqlString);
		allPCTRecords.addAll(jdbcTemplate.query(sqlString, new Object[] { "USA", Integer.toString(ProductHierarchy.PCT.getValue()) },
                (rs, rowNum) -> new ProductHierarchyRecord(null, "USA", rs.getString("productCode").trim(),
                		ProductHierarchy.PCT, rs.getString("productDescription").trim(), rs.getString("updateID").trim(), rs.getDate("updateDate"))).stream()
		 .collect(toList()));
		
		return allPCTRecords;
	}

	public Map<String, List<ProductHierarchyRecord>> getAllProducts() {
		final String METHOD_NAME = "getAllProducts";

		Map<String, List<ProductHierarchyRecord>> allProducts = new HashMap<String, List<ProductHierarchyRecord>>();
		String sqlString = ProductHierarchyQueryMapper.buildAllProductsQuery();
		log.info(METHOD_NAME, "Query to fetch All Products: {}" + sqlString);
		
		allProducts.putAll(jdbcTemplate.query(sqlString, new Object[] { "USA" },  new AllProductExtractor()));
		
		return allProducts;
	}

}
