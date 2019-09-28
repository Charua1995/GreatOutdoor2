package com.capgemini.go.service;

import java.sql.Connection;
import java.util.List;

import com.capgemini.go.dto.Product;
import com.capgemini.go.exception.ProductMasterException;

public interface ProductMasterService {

	boolean addProduct(Product product,Connection connection) throws ProductMasterException;
	boolean updateProduct(Product product,Connection connection) throws ProductMasterException;
	boolean deleteProduct(String productId,Connection connection) throws ProductMasterException;
	boolean addExistingProduct(Product product,Connection connection) throws ProductMasterException;

}
