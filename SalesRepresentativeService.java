package com.capgemini.go.service;

import com.capgemini.go.exception.OrderNotFoundException;
import com.capgemini.go.exception.ProductNotFoundException;
import com.capgemini.go.exception.SalesRepresentativeException;

public interface SalesRepresentativeService {

	boolean returnOrder(String orderId, String userId, String reason) throws SalesRepresentativeException;

	boolean returnProduct(String orderId, String userId, String productID, int qty, String reason)throws SalesRepresentativeException;

	String cancelProduct(String orderId, String userId, String productId, int qty)
			throws OrderNotFoundException, ProductNotFoundException;

	String validateUser(String orderId) throws SalesRepresentativeException;

}
