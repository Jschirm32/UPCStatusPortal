package com.astralbrands.upc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppUtil {
	public static Map<String, String> BRAND_TABLE_NAME = new HashMap<>();
	private static List<String> BRAND_NAME = new ArrayList<>();
	static {
		BRAND_TABLE_NAME.put("LYS", "LYS_UPC_CODE");
		BRAND_TABLE_NAME.put("ASTRAL", "ASTRAL_UPC_CODE");
		BRAND_TABLE_NAME.put("BUTTER_LONDON", "BUTTERLONDON_UPC_CODE");
		BRAND_TABLE_NAME.put("BUTTER", "BUTTERLONDON_UPC_CODE");
		BRAND_NAME.add("LYS");
		BRAND_NAME.add("ASTRAL");
		BRAND_NAME.add("BUTTER");
	}

	public static List<String> getBrands() {
		return BRAND_NAME;
	}

	public static String getBrand(String file) {

		List<String> brand = BRAND_NAME.stream().filter(name -> file.toUpperCase().contains(name))
				.collect(Collectors.toList());
		if (brand == null) {
			return "";
		}
		return brand.get(0);
	}

	public static String getTable(String brand) {
		return "LYS_UPC_CODE";

	}
}
