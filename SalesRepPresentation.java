package com.capgemini.go.presentationLayer;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import com.capgemini.go.exception.SalesRepresentativeException;
import com.capgemini.go.service.SalesRepresentativeService;
import com.capgemini.go.service.SalesRepresentativeServiceImpl;
import com.capgemini.go.utility.GoLog;
import com.capgemini.go.utility.PropertiesLoader;

public class SalesRepPresentation {
	private static Properties exceptionProps = null;
	private static Properties goProps = null;
	private static final String EXCEPTION_PROPERTIES_FILE = "exceptionStatement.properties";
	private static final String GO_PROPERTIES_FILE = "goUtility.properties";

	public SalesRepPresentation() {
	}

	static void interfacesales() {

		boolean salesRepEntry = true;
		Scanner scanner = new Scanner(System.in);

		SalesRepresentativeService salesRepService = new SalesRepresentativeServiceImpl();
		String orderId;
		String reason;
		String productId;
		int qty;
		String userId;
		boolean status;
		while (salesRepEntry == true) {
			System.out.println("************* SALES REPRESENTATIVE MENU ***********");
			System.out.println("Press The key according to the operation you want to perform");
			System.out.println("1 RETURN AN ORDER ...\n 2 RETURN A PRODUCT...\n 0  TO GO BACK TO THE MAIN MENU");
			int salesRepChoice = scanner.nextInt();
			scanner.nextLine();
			switch (salesRepChoice) {
			case 0:
				salesRepEntry = false;
				break;
			case 1:
				System.out.println("Enter the order Id");
				orderId = scanner.nextLine();
				System.out.println("Enter the user id");
				userId = scanner.nextLine();
				System.out.println("Enter the reason");
				reason = scanner.nextLine();
				try {
					exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
					goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);
					if ((salesRepService.validateUser(orderId)).equals(userId)) {
						status = salesRepService.returnOrder(orderId, userId, reason);
						if (status == true) {
							GoLog.logger.info(exceptionProps.getProperty("order_processed"));
						} else {
							GoLog.logger.error(exceptionProps.getProperty("failure_order"));
							break;
						}
					} else {
						GoLog.logger.error(exceptionProps.getProperty("validate_user_error"));
						break;
					}
				} catch (SalesRepresentativeException | IOException e) {
					GoLog.logger.error(exceptionProps.getProperty("validate_user_error"));
				}
				break;
			case 2:
				System.out.println("Enter the order Id");
				orderId = scanner.nextLine();
				System.out.println("Enter the user id");
				userId = scanner.nextLine();
				System.out.println("Enter the product id");
				productId = scanner.nextLine();
				System.out.println("Enter the reason");
				reason = scanner.nextLine();
				System.out.println("Enter the quantity");
				qty = scanner.nextInt();
				try {
					exceptionProps = PropertiesLoader.loadProperties(EXCEPTION_PROPERTIES_FILE);
					goProps = PropertiesLoader.loadProperties(GO_PROPERTIES_FILE);
					if ((salesRepService.validateUser(orderId)).equals(userId)) {
						status = salesRepService.returnProduct(orderId, userId, productId, qty, reason);
						if (status == true) {
							GoLog.logger.info(exceptionProps.getProperty("return_order_processed"));
						} else {
							GoLog.logger.error(exceptionProps.getProperty("failure_order"));
						}
					} else {
						GoLog.logger.error(exceptionProps.getProperty("validate_user_error"));
						GoLog.logger.error(exceptionProps.getProperty("failure_order"));
					}
				} catch (SalesRepresentativeException | IOException e) {
					GoLog.logger.error(exceptionProps.getProperty("failure_order"));
				}
				break;

			default:
				System.out.println("Invalid Input . Enter a valid choice ");
			}
		}
	}

}
