package gamestopsim;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Customer extends Thread {
	private long arrivalTime;
	private long serviceTime;
	private long waitingTimeStart = -1, elapsedWaitingTime = -1;
	private static int sessionsCompleted = 0;
	private static ArrayList<Long> collectedWaitingTimes = new ArrayList<Long>(),
			collectedServiceTime = new ArrayList<Long>(), collectedArrivalTime = new ArrayList<Long>(),
			collectedRoamingTime = new ArrayList<Long>();
	private static Calendar today;
	private static Semaphore employeeSemaphore = new Semaphore(1, true);
	private CustomerType type;

	public CustomerType getCustomerType() {
		return type;
	}

	public void setCustomerType(CustomerType type) {
		this.type = type;
	}

	public long getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(long arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public static void setEmployeeSemaphore(int numOfEmployees) {
		employeeSemaphore = new Semaphore(numOfEmployees, true);
	}

	public long getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(long serviceTime) {
		this.serviceTime = serviceTime;
	}

	public Customer(long arrivalTime, long serviceTime, CustomerType type) {
		this.arrivalTime = arrivalTime;
		this.serviceTime = serviceTime;
		if (type == null) {
			type = CustomerType.ROAMING;
		}
		this.type = type;
	}

	public Customer(long arrivalTime, long serviceTime) {
		this.arrivalTime = arrivalTime;
		this.serviceTime = serviceTime;
		if (this.type == null) {
			this.type = CustomerType.ROAMING;
		}
	}

	public void arrival() {
		try {
			Random rand = new Random();
			double randValue = rand.nextDouble();
			final long arrivalTime = (long) (-Math.log((1 - randValue)) * this.getArrivalTime());
			Thread.sleep(arrivalTime);
			Customer.today = Calendar.getInstance();
			Customer.today.set(Calendar.HOUR_OF_DAY, 0);
			System.out.println("Customer " + this.getId() + " has arrived. (Workday time: " + (today.getTime()) + " )");
			collectedArrivalTime.add(arrivalTime);
			if (type == CustomerType.PREORDER_OR_PICKUP) {
				waitingTimeStart = System.nanoTime();
			} else if (type == CustomerType.ROAMING) {
				roam();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void roam() {
		try {
			long[] roamingTime = { 2000, 3000, 5000 };
			Random rand = new Random();
			long selectedRoamTime = roamingTime[rand.nextInt(3)];
			double randValue = rand.nextDouble();
			final long roamTime = (long) (-Math.log((1 - randValue)) * selectedRoamTime);
			Thread.sleep(roamTime);
			Customer.today = Calendar.getInstance();
			Customer.today.set(Calendar.HOUR_OF_DAY, 0);
			System.out.println(
					"Customer " + this.getId() + " found a game to buy. (Workday time: " + (today.getTime()) + " )");
			collectedRoamingTime.add(roamTime);
			waitingTimeStart = System.nanoTime();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void service() {
		Random rand = new Random();
		double randValue = rand.nextDouble();
		final long serviceTime = (long) (-Math.log((1 - randValue)) * this.getServiceTime());
		try {
			Thread.sleep(serviceTime);
			collectedServiceTime.add(serviceTime);
			Customer.today = Calendar.getInstance();
			Customer.today.set(Calendar.HOUR_OF_DAY, 0);
			System.out.println("Employee is finished with Customer " + this.getId() + "'s order. Customer "
					+ this.getId() + " leaves. (Work day time: " + (today.getTime()) + " )");
			update();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void update() {
		if (collectedArrivalTime.size() != 0 && collectedServiceTime.size() != 0) {
			long waitingTimeSum = 0, arrivalTimeSum = 0, serviceTimeSum = 0, roamingTimeSum = 0;
			if (collectedWaitingTimes.size() != 0) {
				for (int x = 0; x < collectedWaitingTimes.size(); x++) {
					waitingTimeSum = waitingTimeSum + collectedWaitingTimes.get(x);
				}
			}
			if (collectedRoamingTime.size() != 0) {
				for (int x = 0; x < collectedRoamingTime.size(); x++) {
					roamingTimeSum = roamingTimeSum + collectedRoamingTime.get(x);
				}
			}
			for (int x = 0; x < collectedArrivalTime.size(); x++) {
				arrivalTimeSum = arrivalTimeSum + collectedArrivalTime.get(x);
			}
			for (int x = 0; x < collectedServiceTime.size(); x++) {
				serviceTimeSum = serviceTimeSum + collectedServiceTime.get(x);
			}
			Customer.today = Calendar.getInstance();
			Customer.today.set(Calendar.HOUR_OF_DAY, 0);
			long averageWaitingTime = waitingTimeSum, averageRoamingTime = roamingTimeSum;
			if (collectedWaitingTimes.size() != 0) {
				averageWaitingTime = (long) waitingTimeSum / collectedWaitingTimes.size();
			}
			if (collectedRoamingTime.size() != 0) {
				averageRoamingTime = (long) roamingTimeSum / collectedRoamingTime.size();
			}
			long averageArrivalTime = (long) arrivalTimeSum / collectedArrivalTime.size();
			long averageServiceTime = (long) serviceTimeSum / collectedServiceTime.size();
			System.out.println("Current average arrival time: " + averageArrivalTime
					+ " millisecond(s)| Current average waiting time: " + averageWaitingTime
					+ " millisecond(s)| Current average service time: " + averageServiceTime
					+ " millisecond(s)| Current average roaming time: " + averageRoamingTime
					+ " millisecond(s)| (Work day time: " + today.getTime() + ")");

		}
	}

	public void run() {
		int x = sessionsCompleted;
		try {
			employeeSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			elapsedWaitingTime = System.nanoTime() - waitingTimeStart;
			collectedWaitingTimes.add(elapsedWaitingTime);
			Customer.today = Calendar.getInstance();
			Customer.today.set(Calendar.HOUR_OF_DAY, 0);
			System.out
					.println("Employee is processing Customer " + this.getId() + "'s order. Customer  was waiting for "
							+ elapsedWaitingTime + " millisecond(s). (Work day time: " + (today.getTime()) + " )");
			service();

		} finally {
			employeeSemaphore.release();
		}
		x++;
		sessionsCompleted = x;
	}

}