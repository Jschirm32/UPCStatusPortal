	package com.astralbrands.upc.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.astralbrands.upc.dao.UpcRepository;
import com.astralbrands.upc.dto.Brand;
import com.astralbrands.upc.dto.Product;
import com.astralbrands.upc.service.UpcService;

/*
	Implementation of UpcService interface
	Object of the UpcRepository class to connect
	and interact with databases ||| Functions defined
	retrieve list of available/recent products and two
	other functions to execute queries to upload a products'
	information to the bitBoot DB
 */
@Service
public class UpcServiceImpl implements UpcService {
	
	Logger log = LoggerFactory.getLogger(UpcServiceImpl.class);

	private UpcRepository upcRepository;
	
	@Override
	public List<Product> getRecentProducts(String brand) throws Exception {
		return upcRepository.getRecentUsedProduct(brand);
	}
	
	@Override
	public List<Brand> getAllAvailableUpc() {
		return upcRepository.getAllAvailableUpc();
	}

	@Override
	public void uploadProductToDb(String brand, List<Product> product) {
		log.info("total products : "+product+" for brand"+brand);
		upcRepository.uploadProductToDb(brand, product);
	}

	@Override
	public void uploadExistingProductToDb(String brand, List<Product> product) {
		// TODO Auto-generated method stub
		log.info("total products : "+product+" for brand"+brand);
		upcRepository.uploadExistingProductToDb(brand, product);
	}
}
