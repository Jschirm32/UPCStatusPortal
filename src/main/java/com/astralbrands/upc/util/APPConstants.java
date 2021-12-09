package com.astralbrands.upc.util;

import java.util.List;

public interface APPConstants {

	static final String SELECT_PRODUCT_QUERY = "SELECT TOP 10 SKU_ID, BRAND_NAME, DESCRIPTION,COMPANY_PREFIX,UPC_CODE FROM #table WHERE STATUS =1 ORDER BY  UPC_CODE DESC ;";
	static final String SELECT_AVALABLE_PRODUCT_QUERY = "select TOP 1 ID, BRAND_NAME,COMPANY_PREFIX, UPC_CODE, (SELECT count(*) FROM #table WHERE STATUS = 0) AS UPC_COUNT FROM #table where STATUS = 0 and COMPANY_PREFIX='#prefix' ORDER BY UPC_CODE ASC ;";
	static final String INSERT_PRODUCT_QUERY = "Insert into #table(BRAND_NAME,DESCRIPTION,COMPANY_PREFIX,UPC_CODE,STATUS) SELECT '#brand','','#prefix','#upcCode',0  WHERE NOT EXISTS ( SELECT 1 FROM #table where UPC_CODE='#upcCode');";
	static final String NEXT_UPC_BT_QUERY = "select MIN(UPC_CODE) UPC_CODE from BUTTERLONDON_UPC_CODE where STATUS = 0;";
	static final String NEXT_UPC_QUERY = "select MIN(UPC_CODE) UPC_CODE from #table where STATUS = 0;";
	static final String NEXT_UPC_LYS_QUERY = "select MIN(UPC_CODE) UPC_CODE from LYS_UPC_CODE where STATUS = 0;";
	static final String NEXT_UPC_ASTRAL_QUERY = "select MIN(UPC_CODE) UPC_CODE from ASTRAL_UPC_CODE where STATUS = 0;";

	static final String NEXT_UPC_FOR_NOT_BRAND = "select MIN(UPC_CODE) UPC_CODE from #table where STATUS = 0 and UPC_CODE <> '#upc';";
	static final List<String> BRAND_TABLE = List.of("BUTTERLONDON_UPC_CODE", "ASTRAL_UPC_CODE", "LYS_UPC_CODE");
	static final String UPDATE_UPC_CODE_STATUS = "update #table set STATUS = 1 , DESCRIPTION = '#DESC' , SKU_ID = '#SKUID'  where UPC_CODE = '#upc';";
	static final String SELECT_AVALABLE_NEXT_PRODUCT_QUERY = "select TOP 1 ID, BRAND_NAME,COMPANY_PREFIX, UPC_CODE, (SELECT count(*) FROM #table WHERE STATUS = 0) AS UPC_COUNT FROM #table where STATUS = 0 and UPC_CODE <> '#upc' and COMPANY_PREFIX='#prefix' ORDER BY UPC_CODE ASC;";
	//static final String CHECK_UPC_IN_X3_QUERY = "select EANCOD_0, TCLCOD_0, ITMREF_0,ITMDES1_0,ITMDES2_0  from TEST_UPC_CODES where EANCOD_0 = '#USC';";
	static final String CHECK_UPC_IN_X3_QUERY = "select EANCOD_0, TCLCOD_0, ITMREF_0,ITMDES1_0,ITMDES2_0  from PROD.ITMMASTER where EANCOD_0 = '#upc';";
	static final String INSERT_EXISTING_PRODUCT_QUERY = "Insert into #table(BRAND_NAME,DESCRIPTION,COMPANY_PREFIX,UPC_CODE,STATUS) SELECT '#brand','#desc','#prefix','#upcCode',1  WHERE NOT EXISTS ( SELECT 1 FROM #table where UPC_CODE='#upcCode');";
}
