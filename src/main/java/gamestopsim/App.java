package gamestopsim;

import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		Customer.setEmployeeSemaphore(2);
		long[] serviceTimes = { 2000, 3000, 5000 };
		ArrayList<Customer> customers = new ArrayList<Customer>();
		/* 11 */ customers.add(new Customer(1000, serviceTimes[0]));
		/* 12 */ customers.add(new Customer(1000, serviceTimes[1], CustomerType.PREORDER_OR_PICKUP));
		/* 13 */ customers.add(new Customer(10000, serviceTimes[2], CustomerType.ROAMING));
		/* 14 */ customers.add(new Customer(20000, serviceTimes[2]));
		/* 15 */ customers.add(new Customer(30000, serviceTimes[1], CustomerType.PREORDER_OR_PICKUP));
		/* 16 */ customers.add(new Customer(40000, serviceTimes[0], CustomerType.PREORDER_OR_PICKUP));
		/* 17 */ customers.add(new Customer(50000, serviceTimes[2]));
		/* 18 */ customers.add(new Customer(60000, serviceTimes[2], CustomerType.PREORDER_OR_PICKUP));
		/* 19 */ customers.add(new Customer(70000, serviceTimes[1]));

		for (int x = 0; x < customers.size(); x++) {
			customers.get(x).arrival();
			customers.get(x).start();
		}
	}
}
