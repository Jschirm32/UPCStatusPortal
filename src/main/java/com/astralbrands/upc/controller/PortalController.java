package com.astralbrands.upc.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.astralbrands.upc.dto.Brand;
import com.astralbrands.upc.dto.Product;
import com.astralbrands.upc.service.UpcService;
import com.astralbrands.upc.util.AppUtil;
//import com.astralbrands.upc.LoggingController;

@Controller
public class PortalController {
	
	Logger log = LoggerFactory.getLogger(PortalController.class);

	@Value("${spring.application.name}")
	String appName;

	@Autowired
	private UpcService upcService;


	@RequestMapping(value = { "/" }, method = RequestMethod.GET)
	public ModelAndView homePage() {
		log.info("home page");
		ModelAndView map = new ModelAndView("home");
		return displayHomePage(map, "LYS");
	}

	/*

	 */
	private ModelAndView displayHomePage(ModelAndView map, String brand) {
		List<Product> list;
		List<Brand> brands;
		try {
			String message;
			list = upcService.getRecentProducts(brand);
			brands = upcService.getAllAvailableUpc();
			for(Brand brand2 : brands) {

			}
			log.info("list : " + list);
			log.info(": " + brands);
			map.addObject("brands", brands);
			map.addObject("products", list);
			map.addObject("brandName", brand);
		} catch (Exception e) {
			log.error("error while loading data from db : "+e.getMessage());
			map.addObject("brands", new ArrayList());
			map.addObject("products", new ArrayList());
			map.addObject("brandName", brand);
			map.addObject("message", "Error while loading data from data base");
		}
		return map;
	}

	@RequestMapping(value = { "/brand" }, method = RequestMethod.GET)
	public ModelAndView displayBrandRecentData(@RequestParam("name") String brand) {
		log.info("display recent for " + brand);
		ModelAndView map = new ModelAndView("home");
		return displayHomePage(map, brand);
	}

	@PostMapping("/upload-csv-file")
	public ModelAndView uploadCSVFile(@RequestParam("file") MultipartFile file, Model model) {
		ModelAndView map = new ModelAndView("home");

		// validate file
		if (file.isEmpty()) {
			log.info(" file is empty:" + file.getName());
			map.addObject("message", "Please select a CSV file to upload.");
			map.addObject("status", false);
		} else {
			log.info(" file is not empty" + file.getOriginalFilename());
			String line = "";
			String splitBy = ",";
			// parse CSV file to create a list of `User` objects
			int companyPrefixIndex = -1;
			int upcCodeIndex = -1;
			boolean header = true;

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
				String brand = AppUtil.getBrand(file.getOriginalFilename());
				if (brand == null) {
					throw new RuntimeException("Brand Name is not supported, please check file Name");
				}
				// create csv bean reader
				List<Product> productRow = new ArrayList<>();
				while ((line = reader.readLine()) != null) // returns a Boolean value
				{
					log.info("line :" + line);
					String[] products = line.split(splitBy); // use comma as separator
					for (int i = 0; i < products.length; i++) {
						log.info("col :" + products[i]);
						if (products[i].contains("GS1CompanyPrefix")) {
							companyPrefixIndex = i;
						}
						if (products[i].contains("UPC")) {
							upcCodeIndex = i;
						}
					}
					if (!header && companyPrefixIndex != -1 && upcCodeIndex != -1) {
						Product product = new Product();
						product.setCompanyPrefix(products[companyPrefixIndex]);
						product.setUpcCode(products[upcCodeIndex]);
						log.info("product :" + product + " brand : " + brand);
						productRow.add(product);
					}
					header = false;
				}
				upcService.uploadProductToDb(brand, productRow);

			} catch (Exception ex) {
				log.error("error while reading csv file");
				ex.printStackTrace();
				map.addObject("message", "An error occurred while processing the CSV file. \n" + ex.getMessage());
				map.addObject("status", false);
			}
		}
		return displayHomePage(map, "LYS");
	}

	@PostMapping("/upload-existing-csv-file")
	public ModelAndView uploadExistingBrandRecord(@RequestParam("file") MultipartFile file, Model model) {
		ModelAndView map = new ModelAndView("home");

		// validate file
		if (file.isEmpty()) {
			log.info(" file is empty:" + file.getName());
			map.addObject("message", "Please select a CSV file to upload.");
			map.addObject("status", false);
		} else {
			log.info(" file is not empty" + file.getOriginalFilename());
			String line = "";
			String splitBy = ",";
			// parse CSV file to create a list of `User` objects
			int skuIdIndex = -1;
			int upcCodeIndex = -1;
			int descIndex = -1;
			int descIndex1 = -1;
			boolean header = true;

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
				String brand = AppUtil.getBrand(file.getOriginalFilename());
				if (brand == null) {
					throw new RuntimeException("Brand Name is not supported, please check file Name");
				}
				// create csv bean reader
				List<Product> productRow = new ArrayList<>();
				while ((line = reader.readLine()) != null) // returns a Boolean value
				{
					if(!isValidRow(line)) {
						continue;
					}
					log.info("line :" + line);
					String[] products = line.split(splitBy); // use comma as separator
					for (int i = 0; i < products.length; i++) {
						log.info("col :" + products[i]);
						if (products[i].contains("SKUID")) {
							skuIdIndex = i;
						}
						if (products[i].contains("UPCCODE")) {
							upcCodeIndex = i;
						}
						if (products[i].contains("DESC")) {
							descIndex = i;
						}
						if (products[i].contains("DESC1")) {
							descIndex1 = i;
						}
					}
					log.info(" product length :"+products.length);
					if (!header && skuIdIndex != -1 && upcCodeIndex != -1 && descIndex != -1) {
						Product product = new Product();
						product.setSkuId(products[skuIdIndex]);
						product.setUpcCode(products[upcCodeIndex]);
						product.setDescription(products[descIndex] + " " + products[descIndex1]);
						log.info("product :" + product + " brand : " + brand);
						productRow.add(product);
					}
					header = false;
				}
				upcService.uploadExistingProductToDb(brand, productRow);

			} catch (Exception ex) {
				log.error("error while reading csv file");
				ex.printStackTrace();
				map.addObject("message", "An error occurred while processing the CSV file. \n" + ex.getMessage());
				map.addObject("status", false);
			}
		}
		return displayHomePage(map, "LYS");
	}
	
	private boolean isValidRow(String line) {
		return line != null && line.trim().length() > 1;
	}

}
