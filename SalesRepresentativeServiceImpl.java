package com.capgemini.go.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.capgemini.go.dao.SalesRepresentativeDao;
import com.capgemini.go.dao.SalesRepresentativeDaoImpl;
import com.capgemini.go.dto.OrderProductMap;
import com.capgemini.go.dto.OrderReturn;
import com.capgemini.go.exception.OrderNotFoundException;
import com.capgemini.go.exception.ProductNotFoundException;
import com.capgemini.go.exception.SalesRepresentativeException;
import com.capgemini.go.utility.GoLog;
import com.capgemini.go.utility.PropertiesLoader;

public class SalesRepresentativeServiceImpl implements SalesRepresentativeService {

	private static Properties exceptionProps = null;
	private static Properties goProps = null;
	private static final String EXCEPTION_PROPERTIES_FILE = "exceptionStatement.properties";
	private static final String GO_PROPERTIES_FILE = "goUtility.properties";

	private SalesRepresentativeDao salesRepDao = new SalesRepresentativeDaoImpl();

	/*******************************************************************************************************
	 * - Function Name : returnOrder - Input Parameters : String orderId ,String
	 * userId,String reason- Return Type : - Throws :SalesRepresentativeException -
	 * Author : CAPGEMINI - Creation Date : 23/09/2019 - Description : Return order
	 * calls the dao calls checkDispatchStatus ,getOrderProductMap,and returnOrder
	 * along with updateProductMap
	 ********************************************************************************************************/
	public boolean returnOrder(String orderId, String userId, String reason) throws SalesRepresentativeException {

		boolean statusOrderReturn = false;
		boolean orderProductMapStatus = false;
		try {
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);
			if (salesRepDao.checkDispatchStatus(orderId)) {
				List<OrderProductMap> opm = salesRepDao.getOrderProductMap(orderId);
				Date dt = new Date();
				for (OrderProductMap index : opm) {
					OrderReturn or = new OrderReturn(orderId, userId, index.getProductId(), index.getProductUIN(), dt,
							reason, 1);
					statusOrderReturn = salesRepDao.returnOrder(or);
				}
				if (statusOrderReturn) {
					orderProductMapStatus = salesRepDao.updateOrderProductMap(orderId);
				}
			} else {
				GoLog.logger.error(exceptionProps.getProperty("not_yet_dispatched"));
				throw new SalesRepresentativeException(exceptionProps.getProperty("not_yet_dispatched"));
			}
		} catch (SalesRepresentativeException | IOException e) {
			throw new SalesRepresentativeException(exceptionProps.getProperty("failure_order"));

		}

		return orderProductMapStatus;
	}

	/*******************************************************************************************************
	 * - Function Name : returnProduct - Input Parameters : String orderId,String
	 * productId,int qty,String reason - Return Type :boolean- Throws
	 * :SalesRepresentativeException - Author : CAPGEMINI - Creation Date :
	 * 23/09/2019 - Description : checking whether the order is at all despatched
	 ********************************************************************************************************/
	public boolean returnProduct(String orderId, String userId, String productId, int qty, String reason)
			throws SalesRepresentativeException {
		boolean returnProductStatus = false;
		try {
			exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
			goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);
			if (salesRepDao.checkDispatchStatus(orderId)) {
				int countProd = salesRepDao.getCountProduct(orderId, productId);
				if (countProd >= qty) {
					salesRepDao.updateOrderProductMapByQty(orderId, productId, qty);
					salesRepDao.updateOrderReturn(orderId, productId, userId, reason, qty);
					returnProductStatus = true;
				} else {
					GoLog.logger.error(exceptionProps.getProperty("prod_not_ordered"));
					throw new SalesRepresentativeException(exceptionProps.getProperty("prod_not_ordered"));
				}
			} else {
				GoLog.logger.error(exceptionProps.getProperty("not_yet_dispatched"));
				throw new SalesRepresentativeException(exceptionProps.getProperty("not_yet_dispatched"));
			}
		} catch (SalesRepresentativeException | IOException e) {
			throw new SalesRepresentativeException(exceptionProps.getProperty("failure_order"));
		}

		return returnProductStatus;
	}

	/*******************************************************************************************************
	 * - Function Name : cancelOrder - Input Parameters : String orderId - Return
	 * Type : - Throws :SalesRepresentativeException - Author : CAPGEMINI - Creation
	 * Date : 23/09/2019 - Description : Cancel Order to database calls dao method
	 * getOrderDetails(sr)
	 * 
	 * @throws Exception
	 ********************************************************************************************************/

	/*******************************************************************************************************
	 * - Function Name : cancelProduct - Input Parameters : String orderId, String
	 * productID, int qty - Return Type : - Throws :SalesRepresentativeException -
	 * Author : CAPGEMINI - Creation Date : 23/09/2019 - Description : Cancel Order
	 * to database calls dao method getOrderDetails(sr)
	 * 
	 * @throws SalesRepresentativeException
	 ********************************************************************************************************/

	@Override
	public String validateUser(String orderId) throws SalesRepresentativeException {
		return (salesRepDao.validateUser(orderId));
	}

	@Override
	public String cancelProduct(String orderId, String userId, String productId, int qty)
			throws OrderNotFoundException, ProductNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
