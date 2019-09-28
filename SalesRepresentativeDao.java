package com.capgemini.go.dao;

import java.util.List;

import com.capgemini.go.dto.OrderProductMap;
import com.capgemini.go.dto.OrderReturn;
import com.capgemini.go.exception.SalesRepresentativeException;

public interface SalesRepresentativeDao {

	List<OrderProductMap> getOrderProductMap(String orderId) throws SalesRepresentativeException;

	boolean returnOrder(OrderReturn or) throws SalesRepresentativeException;

	boolean checkDispatchStatus(String orderId) throws SalesRepresentativeException;

	boolean updateOrderProductMap(String orderId) throws SalesRepresentativeException;

	String validateUser(String orderId) throws SalesRepresentativeException;

	int getCountProduct(String orderId,String productId) throws SalesRepresentativeException;

	boolean updateOrderProductMapByQty(String orderId, String productId, int qty) throws SalesRepresentativeException;

	boolean updateOrderReturn(String orderId,String productId,String userId,String reason,int qty) throws SalesRepresentativeException;

}
