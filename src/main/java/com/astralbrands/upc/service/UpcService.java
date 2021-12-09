package com.astralbrands.upc.service;

import java.util.List;

import com.astralbrands.upc.dto.Brand;
import com.astralbrands.upc.dto.Product;

public interface UpcService {

	public default List<Product> getRecentProducts(String brand) throws Exception {
		return null;
	}
	
	public default List<Brand> getAllAvailableUpc(){
		return null;
	}

	public void uploadProductToDb(String brand, List<Product> product);
	
	public void uploadExistingProductToDb(String brand, List<Product> product);

}
