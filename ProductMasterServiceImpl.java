package com.capgemini.go.service;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import com.capgemini.go.dao.ProductMasterDao;
import com.capgemini.go.dao.ProductMasterDaoImpl;
import com.capgemini.go.dto.Product;
import com.capgemini.go.exception.ProductMasterException;
import com.capgemini.go.utility.PropertiesLoader;

public class ProductMasterServiceImpl implements ProductMasterService {

	
	private static Properties exceptionProps = null;
	private static final String EXCEPTION_PROPERTIES_FILE = "exceptionStatement.properties";
	
	private ProductMasterDao productMaster = new ProductMasterDaoImpl();
	// ------------------------ GreatOutdoor Application --------------------------
	/*******************************************************************************************************
	 * - Function Name : addProduct - Input Parameters : Product - Return Type : boolean -
	 * Throws : - Author : CAPGEMINI - Creation Date : 21/9/2019 - Description : to
	 * add a product in product database
	 * @throws ProductMasterException 
	 ********************************************************************************************************/
	public boolean addProduct(Product product,Connection connection) throws ProductMasterException {
		
		
		boolean productAddStatus = false;
		productAddStatus = productMaster.addProduct(product,connection);
		return productAddStatus;

	}

	

	// ------------------------ GreatOutdoor Application --------------------------
	/*******************************************************************************************************
	 * - Function Name : updateProduct - Input Parameters : Product - Return
	 * Type : boolean - Throws : - Author : CAPGEMINI - Creation Date : 21/9/2019 -
	 * Description : to update a particular product
	 * @throws ProductMasterException 
	 ********************************************************************************************************/
	public boolean updateProduct(Product product,Connection connection) throws ProductMasterException {

		boolean productUpdateStatus = false;
		productUpdateStatus = productMaster.updateProduct(product,connection);
		return productUpdateStatus;
	}

	// ------------------------ GreatOutdoor Application --------------------------
	/*******************************************************************************************************
	 * - Function Name : deleteProduct - Input Parameters : productId, userId - Return
	 * Type : boolean - Throws : - Author : CAPGEMINI - Creation Date : 21/9/2019 -
	 * Description : to delete a product in product database
	 * @throws ProductMasterException 
	 ********************************************************************************************************/
	public boolean deleteProduct(String productId,Connection connection) throws ProductMasterException {

		boolean productDeleteStatus = false;
		productDeleteStatus = productMaster.deleteProduct(productId,connection);
		return productDeleteStatus;
	}
	
	// ------------------------ GreatOutdoor Application --------------------------
		/*******************************************************************************************************
		 * - Function Name : addExistingProduct - Input Parameters : Product - Return
		 * Type : boolean - Throws : - Author : CAPGEMINI - Creation Date : 21/9/2019 -
		 * Description : to increase   product Qty  in product database
		 * @throws ProductMasterException 
		 ********************************************************************************************************/
		public boolean addExistingProduct(Product product,Connection connection) throws ProductMasterException {

			boolean productIncreaseStatus = false;	
			productIncreaseStatus = productMaster.addExistingProduct(product,connection);
			return productIncreaseStatus;
		}


}
