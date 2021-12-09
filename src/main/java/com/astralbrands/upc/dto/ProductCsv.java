package com.astralbrands.upc.dto;

import com.opencsv.bean.CsvBindByName;

public class ProductCsv {

	@CsvBindByName(column = "GS1CompanyPrefix")
	private String companyPrefix;
	@CsvBindByName(column = "UPC")
	private String upcCode;

	public String getCompanyPrefix() {
		return companyPrefix;
	}

	public void setCompanyPrefix(String companyPrefix) {
		this.companyPrefix = companyPrefix;
	}

	public String getUpcCode() {
		return upcCode;
	}

	public void setUpcCode(String upcCode) {
		this.upcCode = upcCode;
	}

	@Override
	public String toString() {
		return "ProductCsv [companyPrefix=" + companyPrefix + ", upcCode=" + upcCode + "]";
	}

	public ProductCsv(String companyPrefix, String upcCode) {
		super();
		this.companyPrefix = companyPrefix;
		this.upcCode = upcCode;
	}

	public ProductCsv() {

	}

}
