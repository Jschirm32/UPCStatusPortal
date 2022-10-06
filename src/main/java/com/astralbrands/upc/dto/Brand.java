package com.astralbrands.upc.dto;

/*
	Class for Getter/Setter methods for
	Multiple variables initialized to retrieve
	or re-assign values associated with a particular
	brand
 */
public class Brand {

	private String name;
	private String upcCode;
	private String companyPrefix;
	private int totalUpcCode;
	private boolean isSelected;
	private int upcThreshold;

	public int getUpcThreshold() {
		return upcThreshold;
	}
	public void setUpcThreshold(int upcThreshold) {
		this.upcThreshold = upcThreshold;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUpcCode() {
		return upcCode;
	}

	public void setUpcCode(String upcCode) {
		this.upcCode = upcCode;
	}

	public int getTotalUpcCode() {
		return totalUpcCode;
	}

	public void setTotalUpcCode(int totalUpcCode) {
		this.totalUpcCode = totalUpcCode;
	}
	
	public void setCompanyPrefix(String companyPrefix) {
		this.companyPrefix = companyPrefix;
	}
	public String getCompanyPrefix() {
		return companyPrefix;
	}

	/*
   		Formats an SQL query to retrieve a brands' information
    */
	@Override
	public String toString() {
		return "Brand [name=" + name + ", upcCode=" + upcCode + ", totalUpcCode=" + totalUpcCode + "]";
	}

}
