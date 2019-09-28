package com.capgemini.go.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.capgemini.go.dto.OrderProductMap;
import com.capgemini.go.dto.OrderReturn;
import com.capgemini.go.exception.DatabaseException;
import com.capgemini.go.exception.SalesRepresentativeException;
import com.capgemini.go.utility.DbConnection;
import com.capgemini.go.utility.GoLog;
import com.capgemini.go.utility.PropertiesLoader;

public class SalesRepresentativeDaoImpl implements SalesRepresentativeDao {

	private static Properties exceptionProps = null;
	private static Properties goProps = null;
	private static final String EXCEPTION_PROPERTIES_FILE = "exceptionStatement.properties";
	private static final String GO_PROPERTIES_FILE = "goUtility.properties";
	private Connection connection;

	/*******************************************************************************************************
	 * - Function Name : returnOrder - Input Parameters : OrderReturn or- Return
	 * Type : boolean - Throws :SalesRepresentativeException- Author : CAPGEMINI -
	 * Creation Date : 23/09/2019 - Description : Return order adds the respective
	 * order in the order_return table in the database
	 ********************************************************************************************************/
	@Override
	public boolean returnOrder(OrderReturn or) throws SalesRepresentativeException {
		boolean returnOrderStatus = false;
		OrderProductMap opm = null;
		Connection connection = null;
		try {
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);
			connection = DbConnection.getInstance().getConnection();

			PreparedStatement statement = connection.prepareStatement(QuerryMapper.ADD_RETURN_ORDER);
			statement.setString(1, or.getOrderId());
			statement.setString(2, or.getUserId());
			statement.setString(3, or.getProductId());
			statement.setString(4, or.getProductUIN());
			java.util.Date utilDate = or.getOrderReturnTime();
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			statement.setDate(5, sqlDate);
			statement.setString(6, or.getReturnReason());

			statement.setInt(7, or.getOrderReturnStatus());
			int numberOfRows = statement.executeUpdate();
			returnOrderStatus = true;
		} catch (SQLException | IOException | DatabaseException e) {
			GoLog.logger.error(exceptionProps.getProperty("return_order_failure"));
			throw new SalesRepresentativeException(exceptionProps.getProperty("return_order_failure"));
		}
		return returnOrderStatus;
	}

	/*******************************************************************************************************
	 * - Function Name : updateOrderProductMap - Input Parameters : String orderId -
	 * Return Type : boolean - Throws :SalesRepresentativeException - Author :
	 * CAPGEMINI - Creation Date : 23/09/2019 - Description : updating
	 * Order_Product_Map in the database by setting product_status=0 for the
	 * products that have been returned
	 ********************************************************************************************************/

	public boolean updateOrderProductMap(String orderId) throws SalesRepresentativeException {
		boolean orderProductMapFlag = false;
		connection = null;
		try {
			connection = DbConnection.getInstance().getConnection();
			PreparedStatement statement = connection.prepareStatement(QuerryMapper.UPDATE_ORDER_PRODUCT_MAP);
			statement.setString(1, orderId);
			int numberOfRows = statement.executeUpdate();
			orderProductMapFlag = true;
		} catch (SQLException | DatabaseException e) {
			GoLog.logger.error(exceptionProps.getProperty("order_product_map_failure"));
			throw new SalesRepresentativeException(exceptionProps.getProperty("order_product_map_failure"));
		}

		return orderProductMapFlag;

	}

	/*******************************************************************************************************
	 * - Function Name : getOrderProductMap - Input Parameters : String orderId -
	 * Return Type :List<ORderProductMap>- Throws :SalesRepresentativeException -
	 * Author : CAPGEMINI - Creation Date : 23/09/2019 - Description : getting all
	 * the products against a particular order
	 ********************************************************************************************************/

	@Override
	public List<OrderProductMap> getOrderProductMap(String orderId) throws SalesRepresentativeException {
		List<OrderProductMap> orderProductMap = new ArrayList<OrderProductMap>();
		Connection connection = null;
		try {
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);
			connection = DbConnection.getInstance().getConnection();
			PreparedStatement statement = connection.prepareStatement(QuerryMapper.GET_ORDER_PRODUCT_MAP);
			statement.setString(1, orderId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next() == false) {
				GoLog.logger.error(exceptionProps.getProperty("product_return_failure"));
				throw new SalesRepresentativeException(exceptionProps.getProperty("product_return_failure"));
			}
			while (resultSet.next()) {
				String productId = resultSet.getString("PRODUCT_ID");
				String productUIN = resultSet.getString("PRODUCT_UIN");
				int productStatus = resultSet.getInt("PRODUCT_STATUS");
				orderProductMap.add(
						new OrderProductMap(orderId, productId, productUIN, productStatus == 1 ? true : false, false));
			}
		} catch (DatabaseException | SQLException | IOException e) {

		}
		return orderProductMap;

	}

	/*******************************************************************************************************
	 * - Function Name : checkDispatchStatus - Input Parameters : String orderId -
	 * Return Type :List<OrderProductMap>- Throws :SalesRepresentativeException -
	 * Author : CAPGEMINI - Creation Date : 23/09/2019 - Description : checking
	 * whether the order is at all despatched
	 ********************************************************************************************************/
	@Override
	public boolean checkDispatchStatus(String orderId) throws SalesRepresentativeException {
		boolean dispatchStatus = false;
		Connection connection = null;
		try {
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);
			connection = DbConnection.getInstance().getConnection();
			PreparedStatement statement = connection.prepareStatement(QuerryMapper.CHECK_ORDER_DISPATCH_STATUS);
			statement.setString(1, orderId);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			dispatchStatus = (resultSet.getInt(1) == 1) ? true : false;
		} catch (SQLException | IOException | DatabaseException e) {
			GoLog.logger.error(exceptionProps.getProperty("orderId_not_found_failure"));
			throw new SalesRepresentativeException(exceptionProps.getProperty("orderId_not_found_failure"));
		}
		return dispatchStatus;
	}

	/*******************************************************************************************************
	 * - Function Name : validateUser - Input Parameters : String orderId - Return
	 * Type :String- Throws :SalesRepresentativeException - Author : CAPGEMINI -
	 * Creation Date : 23/09/2019 - Description : checking whether the order is
	 * linked with a particular user
	 ********************************************************************************************************/
	@Override
	public String validateUser(String orderId) throws SalesRepresentativeException {
		String user = null;
		Connection connection = null;
		try {
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);

			connection = DbConnection.getInstance().getConnection();
			PreparedStatement statement = connection.prepareStatement(QuerryMapper.VALIDATE_USER);
			statement.setString(1, orderId);
			ResultSet resultSet = null;
			resultSet = statement.executeQuery();
			resultSet.next();

			user = resultSet.getString(1);

		} catch (SQLException | IOException | DatabaseException e) {
			GoLog.logger.error(exceptionProps.getProperty("validate_user_error"));
			throw new SalesRepresentativeException(exceptionProps.getProperty("validate_user_error"));
		}
		return user;

	}

	/*******************************************************************************************************
	 * - Function Name : getCountProduct - Input Parameters : String orderId,String
	 * productId - Return Type :int- Throws :SalesRepresentativeException - Author :
	 * CAPGEMINI - Creation Date : 23/09/2019 - Description : getting the count of
	 * the products ordered against a particular order
	 ********************************************************************************************************/

	@Override
	public int getCountProduct(String orderId, String productId) throws SalesRepresentativeException {
		int count = 0;

		Connection connection = null;
		try {
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);

			connection = DbConnection.getInstance().getConnection();
			PreparedStatement statement = connection.prepareStatement(QuerryMapper.COUNT_PRODUCT);
			statement.setString(1, orderId);
			statement.setString(2, productId);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();

			count = resultSet.getInt(1);

		} catch (SQLException | IOException | DatabaseException e) {
		}
		return count;
	}

	/*******************************************************************************************************
	 * - Function Name : updateOrderProductMapByQty - Input Parameters : String
	 * orderId, String productId, int qty - Return Type :boolean - Throws
	 * :SalesRepresentativeException - Author : CAPGEMINI - Creation Date :
	 * 23/09/2019 - Description : Updates the respective product status in the order
	 * product map
	 * 
	 * @throws SalesRepresentativeException
	 ********************************************************************************************************/

	@Override
	public boolean updateOrderProductMapByQty(String orderId, String productId, int qty)
			throws SalesRepresentativeException {
		boolean updateStatus = false;
		Connection connection = null;
		try {
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);

			connection = DbConnection.getInstance().getConnection();
			PreparedStatement statement = connection.prepareStatement(QuerryMapper.UPDATE_ORDER_PRODUCT_MAP_BY_QTY);
			statement.setString(1, orderId);
			statement.setString(2, productId);
			statement.setInt(3, qty);

			int update = statement.executeUpdate();
			updateStatus = true;

		} catch (SQLException | IOException | DatabaseException e) {
			GoLog.logger.error(exceptionProps.getProperty("order_product_map_error"));
			throw new SalesRepresentativeException(exceptionProps.getProperty("order_product_map_error"));
		}

		return updateStatus;

	}

	/*******************************************************************************************************
	 * - Function Name : updateOrderReturn - Input Parameters : String orderId,
	 * String productID, int qty ,String reason,String userId - Return Type :boolean
	 * - Throws :SalesRepresentativeException - Author : CAPGEMINI - Creation Date :
	 * 23/09/2019 - Description : Upload the respective products in the orderReturn
	 * Table
	 * 
	 * @throws SalesRepresentativeException
	 ********************************************************************************************************/

	@Override
	public boolean updateOrderReturn(String orderId, String productId, String userId, String reason, int qty)
			throws SalesRepresentativeException {
		SalesRepresentativeDao salesRepDao = new SalesRepresentativeDaoImpl();
		boolean orderReturnStatus = false;
		Date dt = new Date();
		Connection connection = null;
		try {
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);

			connection = DbConnection.getInstance().getConnection();
			PreparedStatement statement = connection.prepareStatement(QuerryMapper.GET_PRODUCT_UIN);
			statement.setString(1, orderId);
			statement.setString(2, productId);
			statement.setInt(3, qty);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				System.out.println(resultSet.getString(1));
				salesRepDao.returnOrder(
						new OrderReturn(orderId, userId, productId, resultSet.getString(1), dt, reason, 1));
			}
			orderReturnStatus = true;

		} catch (SQLException | IOException | DatabaseException e) {
			GoLog.logger.error(exceptionProps.getProperty("order_product_map_error"));
			throw new SalesRepresentativeException(exceptionProps.getProperty("order_product_map_error"));
		}
		return orderReturnStatus;
	}
}
