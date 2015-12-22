package concurrentTrading.runnable;

import java.util.concurrent.Callable;

import com.btcc.MessageProvider;

import concurrentTrading.MarketData;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.SubscriptionRequestType;

public class MktDataUpdator implements Callable<MarketData> {
	
	private SessionID sessionID;
	
	private String uid;
	
	private boolean isResponse;
	
	public MktDataUpdator(SessionID sessionID, String uid){
		System.out.println("In thread!");
		this.sessionID = sessionID;
		this.uid = uid;
	}
	
	@Override
	public MarketData call() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("In thread in run");
		Message msg = MessageProvider.createMarketDataRequest("XBTCNY", SubscriptionRequestType.SNAPSHOT, this.uid);
//
		Session.sendToTarget(msg, this.sessionID);
		while(true){
			System.out.print(".");
			Thread.sleep(200);
		}
		
	}

	public void response(){
		
		this.isResponse = true;
		
		
	}
	
	
	

}
