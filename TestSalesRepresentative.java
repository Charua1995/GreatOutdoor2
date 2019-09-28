package com.capgemini.go.unitTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.capgemini.go.dao.SalesRepresentativeDao;
import com.capgemini.go.dao.SalesRepresentativeDaoImpl;
import com.capgemini.go.exception.SalesRepresentativeException;

class TestSalesRepresentative {

	@Test
	void orderIdNotFound() {
		SalesRepresentativeDao salesRepDao=new SalesRepresentativeDaoImpl();
		 assertThrows(SalesRepresentativeException.class,
		           () ->salesRepDao.checkDispatchStatus("OR1235"));
		         
	}
	
	@Test
	void checkDispatchStatusTrue() throws SalesRepresentativeException {
		SalesRepresentativeDao salesRepDao= new SalesRepresentativeDaoImpl();
		assertTrue(salesRepDao.checkDispatchStatus("OR123"));
	}
	
	@Test
	 void checkDispatchStatusFalse()throws SalesRepresentativeException{
		SalesRepresentativeDao salesRepDao=new SalesRepresentativeDaoImpl();
		assertFalse(salesRepDao.checkDispatchStatus("OR157"));
	}
	
	@Test 
	void validateUserTestException() {
		SalesRepresentativeDao salesRepDao=new SalesRepresentativeDaoImpl();
		assertThrows(SalesRepresentativeException.class,
		           () ->salesRepDao.validateUser("OR169"));
	}
	
	@Test
	void validateUserTest(){
		try {
		SalesRepresentativeDao salesRepDao=new SalesRepresentativeDaoImpl();
		String user="SR02";
		assertEquals(user, salesRepDao.validateUser("OR234"));}
		catch(SalesRepresentativeException e) {}
	}
	
	@Test
	void getCountProductException() {
		try {
		SalesRepresentativeDao salesRepDao=new SalesRepresentativeDaoImpl();
		assertEquals(2,salesRepDao.getCountProduct("OR234", "prod02"));}
		catch(SalesRepresentativeException e) {}
	}
	
	@Test
	void getCountProductException1()  {
		try {
		SalesRepresentativeDao salesRepDao=new SalesRepresentativeDaoImpl();
		assertNotEquals(4,salesRepDao.getCountProduct("OR234", "prod02"));}
		catch(SalesRepresentativeException e) {}
		
	}
		
	@Test
	void getOrderProductMapException() {
		SalesRepresentativeDao salesRepDao=new SalesRepresentativeDaoImpl();
		assertThrows(SalesRepresentativeException.class, ()-> salesRepDao.getOrderProductMap("OR789"));
	}

}
