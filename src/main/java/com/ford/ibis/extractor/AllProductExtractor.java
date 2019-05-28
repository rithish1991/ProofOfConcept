package com.ford.ibis.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.ford.ibis.model.ProductHierarchy;
import com.ford.ibis.model.ProductHierarchyRecord;

public final class AllProductExtractor implements ResultSetExtractor<Map<String, List<ProductHierarchyRecord>>> {
	
	 @Override
	 public Map<String, List<ProductHierarchyRecord>> extractData(ResultSet rs) throws SQLException {
		 Map<String, List<ProductHierarchyRecord>> allProducts = new HashMap<String, List<ProductHierarchyRecord>>();
		 while (rs.next()) {				 
				int productLevel = Integer.parseInt(rs.getString("productLevel").trim());
				int parentProductLevel = productLevel + 1;					
				ProductHierarchyRecord productRecord = new ProductHierarchyRecord();
				productRecord.setProductCode(rs.getString("productCode").trim());
				productRecord.setProductLevel(ProductHierarchy.toEnum(productLevel));
				productRecord.setDescription(rs.getString("productDescription").trim());					
				String key = parentProductLevel + rs.getString("parentCode").trim();
									
				List<ProductHierarchyRecord> childrenList;
				if (!allProducts.containsKey(key)) {
					childrenList = new ArrayList<ProductHierarchyRecord>();
					allProducts.put(key, childrenList);
				}
				else {
					childrenList = allProducts.get(key);
				}
				
				childrenList.add(productRecord);
	    }
	        return allProducts;
	    }

}
