package concurrentTrading.runnable;

import java.util.UUID;

import com.btcc.Config;
import com.btcc.MessageProvider;

import concurrentTrading.Order;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.fix44.ExecutionReport;

public class PlaceOrder implements Runnable{
	
	private SessionID sessionID;

	private char side;
	
	private char orderType;
	
	private int price;
	
	private int number;
	
	private String uid;
	
	private String accountString;
	
	private int threadID;
	
	private boolean isOn = true;
	
	
	
	public PlaceOrder(SessionID sessionID, int threadID, char side, char orderType, int price, int number){
		this.threadID = threadID;
		this.side = side;
		this.orderType = orderType;
		this.number = number;
		this.uid = UUID.randomUUID().toString();
		this.accountString = Config.getInstance().getAccount();
		System.out.println("Place New Order ( " + this.threadID + " ): " + this.uid);

	}
	
	@Override
	public void run() {
		Message msg = MessageProvider.createNewOrderSingle(accountString, this.uid, this.side, this.orderType, this.price, this.number, "XBTCNY", '0');
		try {
			Session.sendToTarget(msg, this.sessionID);
		} catch (SessionNotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(isOn){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Finished Place Order ( " + this.threadID + " ): " + this.uid);
		
	}
	
	public void executionResponseDecode(Message executionReport) throws FieldNotFound{
		this.free();
	
	}
	
	/**
	 * When response complete, turn this thread off
	 */
	public void free(){
		this.isOn =false;
	}
	
	/**
	 * 
	 * @return ThreadID
	 */
	public int getThreadID(){
		return this.threadID;
	}
	
	/**
	 * return uid
	 */
	public String toString(){
		return this.uid;
	}
	
}
