package com.ford.ibis.service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ford.ibis.model.ProductHierarchyRecord;
import com.ford.ibis.repository.ProductHierarchyRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TrafficLightTreeService {
	
	@Autowired
	private ProductHierarchyRepository productHierarchyRepository;

	private static final String CLASS_NAME = TrafficLightTreeService.class.getName();
	private static final String PATTERN1 = "#,##0.00;(#,##0.00)";
	
	
	public ProductHierarchyRecord getMLITreeNode() {
		final String  METHOD_NAME = "getMLITreeNode";
		log.info(CLASS_NAME, METHOD_NAME);
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.applyPattern(PATTERN1);
		ProductHierarchyRecord productRoot = new ProductHierarchyRecord();
		List<ProductHierarchyRecord> pctList = productHierarchyRepository.getAllPCTRecordsForTrafficLight();
		productRoot.setChildren(pctList);
		Map<String, List<ProductHierarchyRecord>> allProducts = productHierarchyRepository.getAllProducts();
		
		for (ProductHierarchyRecord pct : pctList) {
			pct.setParent(productRoot);
			List<ProductHierarchyRecord> cgList = getChildProductHierarchyRecords(allProducts, pct);
			pct.setChildren(cgList);
			if (cgList != null) {
				for (ProductHierarchyRecord cg : cgList) {
					cg.setParent(pct);
					List<ProductHierarchyRecord> mplList = getChildProductHierarchyRecords(allProducts, cg);
					cg.setChildren(mplList);
					 if (mplList != null) {
						for (ProductHierarchyRecord mpl : mplList) {
							mpl.setParent(cg); 
							List<ProductHierarchyRecord> mliList = getChildProductHierarchyRecords(allProducts, mpl);
							mpl.setChildren(mliList);
							if (mliList != null) {
								for (ProductHierarchyRecord mli : mliList) {
									mli.setParent(mpl);
									mli.setChildren(null);
								}
							}
						}
					}
				}
			}
		}
		
		return productRoot;
	}

	
	public List<ProductHierarchyRecord> getChildProductHierarchyRecords(Map<String, List<ProductHierarchyRecord>> allProducts, ProductHierarchyRecord parent) {
		String key = parent.getProductLevel().getValue() + parent.getProductCode().trim();			
		return allProducts.get(key);
	}
}
