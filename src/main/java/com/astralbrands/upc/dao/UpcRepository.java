package com.astralbrands.upc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.astralbrands.upc.dto.Brand;
import com.astralbrands.upc.dto.Product;
import com.astralbrands.upc.util.APPConstants;
import com.astralbrands.upc.util.AppUtil;

@Repository
public class UpcRepository implements APPConstants {
	
	Logger log = LoggerFactory.getLogger(UpcRepository.class);

	// DB Object to interact with the bitBoot DB
	@Autowired
	@Qualifier("bitBootDataSource")
	private DataSource dataSource;

	// DB Object to interact with the x3 DB
	@Autowired
	@Qualifier("x3DataSource")
	private DataSource x3DataSource;

	private Connection connection;

	private Connection x3Connection;

	@Value("${upc.code.threshold}")
	private int upcThreshold;

	@Value("${astral.company.prefix}")
	private String astralCmpPrefix;

	@Value("${butter.company.prefix}")
	private String butterCmpPrefix;

	@Value("${lys.company.prefix}")
	private String lysCmpPrefix;

	public List<Product> getRecentUsedProduct(String brand) throws Exception {
		try {
			ResultSet resultSet = Optional.ofNullable(runQuery(getProductQuery(brand))).orElse(null);
			List<Product> products = Optional.ofNullable(populateProducts(resultSet)).orElse(null);
			return products;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ResultSet runQuery(String sql) {
		if (dataSource != null) {
			if (connection == null) {
				try {
					connection = dataSource.getConnection();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException("SQLException is null" + e.getMessage());
				}
			}
			try {
				log.info(":" + sql);
				Statement statement = connection.createStatement();
				return statement.executeQuery(sql);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(" erro while running query");
			}
		}
		throw new RuntimeException("Datasource is null");

	}

	public String getCmpPrefix(String brand) {
		if (brand.contains("LYS")) {
			return lysCmpPrefix;
		} else if (brand.contains("ASTRAL")) {
			return astralCmpPrefix;
		} else {
			return butterCmpPrefix;
		}
	}

	private void executeQuery(String sql) {
		if (dataSource != null) {
			if (connection == null) {
				try {
					connection = dataSource.getConnection();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException("SQLException is null" + e.getMessage());
				}
			}
			try {
				log.info("query : " + sql);
				PreparedStatement statement = connection.prepareStatement(sql);
				int count = statement.executeUpdate();
				log.info(" Nom of row affected  " + count);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(" error while running query");
			}
		}
	}

	private void executeBatchQuery(List<String> sql) {
		if (dataSource != null) {
			if (connection == null) {
				try {
					connection = dataSource.getConnection();
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException("SQLException is null" + e.getMessage());
				}
			}
			try {
				log.info("query : " + sql);
				Statement statement = connection.createStatement();
				for (String query : sql) {
					statement.addBatch(query);
				}
				int[] count = statement.executeBatch();
				log.info(" Number of rows affected  " + count.length);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(" error while running query");
			}
		}

	}

	private List<Product> populateProducts(ResultSet result) throws SQLException {
		if (result != null) {
			List<Product> products = new ArrayList<Product>();
			while (result.next()) {
				Product prod = new Product();
				prod.setSkuId(result.getString("SKU_ID"));
				prod.setBrandName(result.getString("BRAND_NAME"));
				prod.setDescription(result.getString("DESCRIPTION"));
				prod.setCompanyPrefix(result.getString("COMPANY_PREFIX"));
				prod.setUpcCode(result.getString("UPC_CODE"));
				products.add(prod);
			}
			return products;
		}
		return null;
	}

	public String getProductQuery(String brand) {
		return SELECT_PRODUCT_QUERY.replace("#table", AppUtil.BRAND_TABLE_NAME.get(brand));
	}

	public String getBrandQuery(String brand) {
		String query = SELECT_AVALABLE_PRODUCT_QUERY.replace("#table", AppUtil.BRAND_TABLE_NAME.get(brand));
		return query.replace("#prefix", getCmpPrefix(brand));
	}

	/*

	 */
	public List<Brand> getAllAvailableUpc() {

		List<String> queries = BRAND_TABLE.stream().map(table -> {
			String query = SELECT_AVALABLE_PRODUCT_QUERY.replace("#table", table);
			return query.replace("#prefix", getCmpPrefix(table));
		}).collect(Collectors.toList());
		List<Brand> brands = queries.stream().map(query -> {
			try {
				return getBrand(query);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}).collect(Collectors.toCollection(LinkedList::new));
		brands = checkUpcCodes(brands);
		System.out.println("This is " + brands);
		return brands;
	}

	private List<Brand> checkUpcCodes(List<Brand> brands) {
		return brands.parallelStream().map(br -> getAvailableUpc(br)).collect(Collectors.toCollection(LinkedList::new));
	}

	private String getTableName(String brandName) {
		return AppUtil.BRAND_TABLE_NAME.get(AppUtil.getBrand(brandName));
	}

	private Brand getNextAvailableUpc(Brand brand, Product product) throws Exception {

		String table = getTableName(brand.getName());
		String updateQuery = getUpdateQuery(product, brand.getUpcCode(), table);
		log.info(" updated used upc code to bitBoot, upc code :" + brand.getUpcCode());
		executeQuery(updateQuery);
		String query = SELECT_AVALABLE_NEXT_PRODUCT_QUERY.replace("#upc", brand.getUpcCode());
		query = query.replace("#table", table);
		query.replace("#prefix", getCmpPrefix(table));
		return getBrand(query);
	}

	private String getUpdateQuery(Product product, String upcCode, String table) {
		String updateQuery = UPDATE_UPC_CODE_STATUS.replace("#upc", upcCode);
		updateQuery = updateQuery.replace("#SKUID", product.getSkuId());
		updateQuery = updateQuery.replace("#DESC", product.getDescription());
		updateQuery = updateQuery.replace("#table", table);
		return updateQuery;
	}

	private Brand getAvailableUpc(Brand brand) {
		if (brand == null) {
			return null;
		}
		Product product = isUpcAvailableInX3(brand);
		if (product != null) {
			Brand tmp = brand;
			try {
				boolean flag = true;
				while (flag) {
					tmp = getNextAvailableUpc(tmp, product);
					product = isUpcAvailableInX3(tmp);
					if (product == null) {
						flag = false;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tmp;
		}
		return brand;
	}

	private Product isUpcAvailableInX3(Brand brand) {
		if (StringUtils.isEmpty(brand.getUpcCode())) {
			return null;
		}
		String query = CHECK_UPC_IN_X3_QUERY.replace("#upc", brand.getUpcCode());
		System.out.println("Brand is " + brand);
		ResultSet rs = runQueryX3(query);
		Product prod = null;
		try {
			while (rs.next()) {
				prod = new Product();
				prod.setSkuId(rs.getString("ITMREF_0"));
				prod.setBrandName(brand.getName());
				prod.setDescription(rs.getString("ITMDES1_0") + " " + rs.getString("ITMDES2_0"));
				prod.setCompanyPrefix(brand.getCompanyPrefix());
				prod.setUpcCode(brand.getUpcCode());
			}
			if (prod != null) {
				System.out.println("upc code doesn't exist, please use other upc code # " + prod);
				return prod;
			} else {
				System.out.println("upc code does not exist");
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private ResultSet runQueryX3(String query) {

		if (x3DataSource != null) {
			if (x3Connection == null) {
				try {
					x3Connection = x3DataSource.getConnection();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException("SQLException is null" + e.getMessage());
				}
			}
			try {
				log.info(":" + query);
				Statement statement = x3Connection.createStatement();
				return statement.executeQuery(query);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(" error while running query");
			}
		}
		throw new RuntimeException("Datasource is null");

	}

	private Brand getBrand(String query) throws Exception {
		ResultSet result = runQuery(query);
		if (result != null) {
			Brand brand = new Brand();
			while (result.next()) {

				brand.setName(result.getString("BRAND_NAME"));
				brand.setUpcCode(result.getString("UPC_CODE"));
				int upcCount = result.getInt("UPC_COUNT");
				brand.setTotalUpcCode(upcCount);
				if (upcCount < upcThreshold) {
					brand.setUpcThreshold(1);
				} else {
					brand.setUpcThreshold(0);
				}
				brand.setCompanyPrefix(result.getString("COMPANY_PREFIX"));

			}
			return brand;
		}
		return null;
	}

	public void uploadProductToDb(String brand, List<Product> product) {
		if (product == null) {
			log.info(" product list is empty");
			return;
		}
		List<String> queries = new LinkedList<>();
		for (Product prod : product) {
			queries.add(getInsertQuery(brand, prod, INSERT_PRODUCT_QUERY));
		}
		product = null;
		executeBatchQuery(queries);
	}

	public void uploadExistingProductToDb(String brand, List<Product> product) {
		if (product == null) {
			log.info(" product list is empty");
			return;
		}
		List<String> queries = new LinkedList<>();
		for (Product prod : product) {
			queries.add(getUpdateQuery(prod, prod.getUpcCode(), getTableName(brand)));
		}
		product = null;
		executeBatchQuery(queries);
	}

	private String getInsertQuery(String brand, Product product, String query) {
		query = INSERT_PRODUCT_QUERY.replace("#brand", brand);
		query = query.replace("#table", AppUtil.BRAND_TABLE_NAME.get(brand));
		query = query.replace("#brand", brand);
		query = query.replace("#prefix", product.getCompanyPrefix());
		query = query.replace("#upcCode", product.getUpcCode());
		query = query.replace("#desc", product.getDescription());
		return query;
	}
}
