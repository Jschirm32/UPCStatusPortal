package com.astralbrands.upc.dto;

/*
	Class for Getter/Setter methods for
	Multiple variables initialized to retrieve
	or re-assign values associated with a particular
	product
 */
public class Product {
	
	private String brandName;
	private String description;
    private String upcCode;
    private String skuId;
    private String id;
    private String companyPrefix;
    private int status;
    
    public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
    
    public String getSkuId() {
		return skuId;
	}
    
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUpcCode() {
		return upcCode;
	}
	public void setUpcCode(String upcCode) {
		this.upcCode = upcCode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCompanyPrefix() {
		return companyPrefix;
	}
	public void setCompanyPrefix(String companyPrefix) {
		this.companyPrefix = companyPrefix;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	/*
		Formats an SQL query to retrieve a products' information
	 */
	@Override
	public String toString() {
		return "Product [brandName=" + brandName + ", description=" + description + ", upcCode=" + upcCode + ", skuId="
				+ skuId + ", id=" + id + ", companyPrefix=" + companyPrefix + ", status=" + status + "]";
	}
	
    
    
    
}
