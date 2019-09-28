package com.capgemini.go.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.capgemini.go.dto.Product;
import com.capgemini.go.exception.DatabaseException;
import com.capgemini.go.exception.ProductMasterException;
import com.capgemini.go.utility.DbConnection;
import com.capgemini.go.utility.GoLog;
import com.capgemini.go.utility.PropertiesLoader;

public class ProductMasterDaoImpl implements ProductMasterDao {

	private static Properties exceptionProps = null;
	private static Properties goProps = null;
	private static final String EXCEPTION_PROPERTIES_FILE = "exceptionStatement.properties";
	private static final String GO_PROPERTIES_FILE = "goUtility.properties";
	

	

	// ------------------------ GreatOutdoor Application --------------------------
	/*******************************************************************************************************
	 * - Function Name : addProduct - Input Parameters : Product product - Return
	 * Type : boolean - Throws : - Author : AGNIBHA - Creation Date : 21/9/2019 -
	 * Description : to add a product in product database
	 * 
	 * @throws ProductMasterException
	 ********************************************************************************************************/

	public boolean addProduct(Product product, Connection connection) throws ProductMasterException {

		boolean addProductState = false;
		String productId = product.getProductId();
		double productPrice = product.getPrice();
		String productColour = product.getColour();
		String productDimension = product.getDimension();
		int productQuantity = product.getQuantity();
		String productSpecification = product.getSpecification();
		int productCategory = product.getProductCategory();
		String manufacturer = product.getManufacturer();
		String productName = product.getProductName();
		int productUIN = 0;
		try {
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);

			
			/**
			 * Condition to Check Product is Already Present or Not
			 * 
			 */
			PreparedStatement statement = connection.prepareStatement(QuerryMapper.COUNT_NO_OF_PRODUCT);
			statement.setString(1, productId);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			if (resultSet.getInt(1) == 0) {

				/**
				 * Check for product category
				 */
				if (productCategory != Integer.valueOf(goProps.getProperty("CAMPING_EQUIPMENT"))
						&& productCategory != Integer.valueOf(goProps.getProperty("OUTDOOR_EQUPMENT"))
						&& productCategory != Integer.valueOf(goProps.getProperty("MOUNTANEERING_EQUPMENT"))
						&& productCategory != Integer.valueOf(goProps.getProperty("GOLF_EQUIPMENT"))
						&& productCategory != Integer.valueOf(goProps.getProperty("PERSONAL_EQUIPMENT"))) {
					throw new ProductMasterException(exceptionProps.getProperty("invalid_product_category"));
				}

				/**
				 * Setting the features of the product in the prepared statement to insert
				 * 
				 */
				PreparedStatement preparedStatement = connection.prepareStatement(QuerryMapper.ADD_PRODUCT);
				preparedStatement.setString(1, productId);
				preparedStatement.setDouble(2, productPrice);
				preparedStatement.setString(3, productColour);
				preparedStatement.setString(4, productDimension);
				preparedStatement.setInt(5, productQuantity);
				preparedStatement.setString(6, productSpecification);
				preparedStatement.setInt(7, productCategory);
				preparedStatement.setString(8, manufacturer);
				preparedStatement.setString(9, productName);
				int number_of_record_inserted = preparedStatement.executeUpdate();

				GoLog.logger.debug(number_of_record_inserted + "Product added in Product database");

				/**
				 * Entering product in product_uin_map for each item of a particular product
				 * type
				 */

				preparedStatement = connection.prepareStatement(QuerryMapper.ADD_PRODUCT_WITH_UIN);
				number_of_record_inserted = 0;
				for (int index = 1; index <= productQuantity; index++) {

					preparedStatement.setString(1, productId);
					preparedStatement.setString(2, productId + Integer.toString(++productUIN));
					preparedStatement.setInt(3, 1);
					int record = preparedStatement.executeUpdate();
					number_of_record_inserted++;

					GoLog.logger.debug(number_of_record_inserted + "record inserted in product_UIN_Map");

				}
				addProductState = true;

			} else {
				GoLog.logger.error(exceptionProps.getProperty("add_product_failure"));
				throw new ProductMasterException(exceptionProps.getProperty("product_id_already_exist"));
			}

		} catch ( SQLException | IOException e) {

			GoLog.logger.error(exceptionProps.getProperty("add_product_failure"));
			throw new ProductMasterException(" >>>" + e.getMessage());
		}
	
		return addProductState;

	}

	

	// ------------------------ GreatOutdoor Application --------------------------
	/*******************************************************************************************************
	 * - Function Name : updateProduct - Input Parameters : productId, product -
	 * Return Type : boolean - Throws : - Author : AGNIBHA - Creation Date :
	 * 21/9/2019 - Description : to update a particular product
	 * @throws ProductMasterException 
	 ********************************************************************************************************/
	public boolean updateProduct(Product product,Connection connection) throws ProductMasterException {

		boolean updateProductState = false;
		String productId = product.getProductId();
		double productPrice = product.getPrice();
		String productColour = product.getColour();
		String productDimension = product.getDimension();
		String productSpecification = product.getSpecification();
		int productCategory = product.getProductCategory();
		String manufacturer = product.getManufacturer();
		String productName = product.getProductName();
		
		try {
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);
			
			
			/**
			 * Condition to Check Product is Already Present or Not
			 * 
			 */
			PreparedStatement statement = connection.prepareStatement(QuerryMapper.IS_PRODUCT_PRESENT);
			statement.setString(1, productId);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			if (resultSet.getInt(1) == 0) {
				GoLog.logger.error(exceptionProps.getProperty("update_product_failure"));
				throw new ProductMasterException(exceptionProps.getProperty("product_id_do_not_exists"));
			} 
			else {
				PreparedStatement getProduct = connection.prepareStatement(QuerryMapper.GET_PRODUCT_BY_PRODUCT_ID);
				getProduct.setString(1, productId);
				
				resultSet = getProduct.executeQuery();
				resultSet.next();
				PreparedStatement preparedStatement = connection.prepareStatement(QuerryMapper.UPDATE_PRODUCT);
				preparedStatement.setString(8, productId);
				if (productPrice == 0) {
					preparedStatement.setDouble(1, resultSet.getDouble(2));

				} else {
					preparedStatement.setDouble(1, productPrice);
				}

				if (productColour.equals("".trim())) {
					preparedStatement.setString(2, resultSet.getString(3));
				} else {
					preparedStatement.setString(2, productColour);

				}

				if (productDimension.equals("".trim())) {
					preparedStatement.setString(3, resultSet.getString(4));
				} else {
					preparedStatement.setString(3, productDimension);
				}

				if (productSpecification.equals("".trim())) {
					preparedStatement.setString(4, resultSet.getString(6));
				} else {
					preparedStatement.setString(4, productSpecification);
				}

				if (productCategory == 0) {
					preparedStatement.setString(5, resultSet.getString(7));
				} else {
					if (productCategory != Integer.valueOf(goProps.getProperty("CAMPING_EQUIPMENT"))
							&& productCategory != Integer.valueOf(goProps.getProperty("OUTDOOR_EQUPMENT"))
							&& productCategory != Integer.valueOf(goProps.getProperty("MOUNTANEERING_EQUPMENT"))
							&& productCategory != Integer.valueOf(goProps.getProperty("GOLF_EQUIPMENT"))
							&& productCategory != Integer.valueOf(goProps.getProperty("PERSONAL_EQUIPMENT"))) {
						GoLog.logger.error(exceptionProps.getProperty("update_product_failure"));
						throw new ProductMasterException(exceptionProps.getProperty("invalid_product_category"));
					}
					preparedStatement.setInt(5, productCategory);
				}

				if (manufacturer.equals("".trim())) {
					preparedStatement.setString(6, resultSet.getString(8));
				} else {
					preparedStatement.setString(6, manufacturer);
				}
				if (productName.equals("".trim())) {
					preparedStatement.setString(7, resultSet.getString(9));
				} else {
					preparedStatement.setString(7, productName);
				}

				int no_of_updates = preparedStatement.executeUpdate();
				GoLog.logger.debug(no_of_updates + " Product Updated");
				updateProductState = true;

			}
		} catch ( SQLException | IOException | ProductMasterException e) {
			GoLog.logger.error(exceptionProps.getProperty("update_product_failure"));
			throw new ProductMasterException(">>>" + e.getMessage());

		}
		
		return updateProductState;

	}

	// ------------------------ GreatOutdoor Application --------------------------
	/*******************************************************************************************************
	 * - Function Name : deleteProduct - Input Parameters : productId - Return Type
	 * : boolean- Throws : - Author : CAPGEMINI - Creation Date : 21/9/2019 -
	 * Description : to delete a particular product
	 * @throws ProductMasterException 
	 ********************************************************************************************************/
	public boolean deleteProduct(String productId,Connection connection) throws ProductMasterException {
		boolean deleteProductStatus = false;
		
		try
		{
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			
			PreparedStatement isProductPresent = connection.prepareStatement(QuerryMapper.IS_PRODUCT_PRESENT);
			PreparedStatement delProduct = connection.prepareStatement(QuerryMapper.DELETE_PRODUCT);
			PreparedStatement softDelProd = connection.prepareStatement(QuerryMapper.SOFT_DELETE_PRODUCT);
			
			isProductPresent.setString(1, productId);
			
			ResultSet resultSet = isProductPresent.executeQuery();
			resultSet.next();
			if(resultSet.getInt(1) == 0)
			{
				GoLog.logger.error(exceptionProps.getProperty("delete_product_failure"));
				throw new ProductMasterException(exceptionProps.getProperty("product_not_exists"));
			}
			else
			{
				delProduct.setString(1, productId);
				softDelProd.setString(1, productId);
				int no_of_products_deleted = delProduct.executeUpdate();
				int prod_del = softDelProd.executeUpdate();
				GoLog.logger.debug(prod_del+ "Product Deleted");
				GoLog.logger.debug(no_of_products_deleted + " Product Item Deleted");
				deleteProductStatus=true;
			}
			
			
			
		}
		catch(ProductMasterException | SQLException | IOException e)
		{
			throw new ProductMasterException( " >>>" + e.getMessage());
		}
		
		return deleteProductStatus;
	}

	// ------------------------ GreatOutdoor Application --------------------------
	/*******************************************************************************************************
	 * - Function Name : addExistingProduct - Input Parameters : productId - Return
	 * Type : boolean- Throws : - Author : AGNIBHA - Creation Date : 21/9/2019 -
	 * Description : to increase the product
	 ********************************************************************************************************/
	public boolean addExistingProduct(Product product,Connection connection) throws ProductMasterException {
		boolean addExistingProductStatus = false;
		String productID = product.getProductId();
		
		int quantity_to_be_added = product.getQuantity();
		try {
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			
			
			/**
			 * Condition to Check Product is Already Present or Not
			 * 
			 */
			PreparedStatement statement = connection.prepareStatement(QuerryMapper.IS_PRODUCT_PRESENT);
			statement.setString(1, productID);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			int initialQuantity = resultSet.getInt(1);
			if(initialQuantity == 0)
			{
				GoLog.logger.error(exceptionProps.getProperty("product_id_do_not_exists"));
				throw new ProductMasterException(exceptionProps.getProperty("product_id_do_not_exists"));
				
			}
			else
			{
				PreparedStatement increaseQty = connection.prepareStatement(QuerryMapper.INCREASE_QUANTITY);
				PreparedStatement preStmt = connection.prepareStatement(QuerryMapper.ADD_PRODUCT_WITH_UIN);
				increaseQty.setInt(1, initialQuantity+quantity_to_be_added);
				increaseQty.setString(2,productID);
				increaseQty.executeUpdate();
				
				
				for(int index = 1 ;index <= quantity_to_be_added;index++)
				{
					preStmt.setString(1, productID);
					preStmt.setString(2, productID+Integer.toString(++initialQuantity));
					preStmt.setInt(3, 1);
					preStmt.executeUpdate()	;	
				}
				GoLog.logger.debug(increaseQty + " item added against the  Product Id :" + productID );
				
				addExistingProductStatus = true;
			}
			
	}
		catch( IOException | SQLException| ProductMasterException e)
		{
			throw new ProductMasterException(exceptionProps.getProperty("add_existing_error") + ">>>"+ e.getMessage());
		}
		return addExistingProductStatus;
}
}
