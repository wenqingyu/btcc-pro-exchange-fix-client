package concurrentTrading;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.btcc.Config;
import com.btcc.MessageProvider;

import concurrentTrading.runnable.MktDataUpdator;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.SubscriptionRequestType;
 

public class SimpleTrader{
	
	private Initiator init;
	
	private TradeApplication app;

	private SessionID sessionID;
	
	private ExecutorService executor;
	
	public boolean startTrader() throws FileNotFoundException, ConfigError, InterruptedException, SessionNotFound, FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType, RejectLogon {
		// TODO Auto-generated method stub
		// INITIALIZE
		
		this.executor = Executors.newFixedThreadPool(20);
		
		this.app = new TradeApplication();
		InputStream inputStream = new FileInputStream("quickfix-client.properties");
		SessionSettings settings = new SessionSettings(inputStream);
		MessageStoreFactory storeFactory = new FileStoreFactory(settings);
		LogFactory logFactory = new FileLogFactory(settings);
		MessageFactory messageFactory = new DefaultMessageFactory();
		
		this.init = new SocketInitiator(app, storeFactory, settings, logFactory, messageFactory);
			
//		app.testMarketMessage(app.getSessionID());
		
		this.init.start();

		System.out.println("Trading Bot ==> Waiting for login . . .");
		while(!this.init.isLoggedOn()){
			System.out.println(".");
			Thread.sleep(200);
		}
		
		this.sessionID = app.getSessionID();
		System.out.println("Trading Bot ==> SessionID: " + this.sessionID + " Login success!");
		
		return false;
	}



	public void buyLimit(double price, int number) throws InterruptedException {
		String uid = UUID.randomUUID().toString();
		String accountString = Config.getInstance().getAccount();
		
		Message msg = MessageProvider.createNewOrderSingle(accountString, uid, Side.BUY, OrdType.LIMIT, price, number, "XBTCNY", '0');
		try {
			System.out.println("SENDING!");
			Session.sendToTarget(msg, this.sessionID);
		} catch (SessionNotFound e) {
			e.printStackTrace();
		}

	}



	public void sellLimit(double price, int number) {
		String uid = UUID.randomUUID().toString();
		String accountString = Config.getInstance().getAccount();
		Message msg = MessageProvider.createNewOrderSingle(accountString, uid, Side.SELL, OrdType.LIMIT, price, number, "XBTCNY", '0');
		try {
			Session.sendToTarget(msg, this.sessionID);
		} catch (SessionNotFound e) {
			e.printStackTrace();
		}
	}



	public void buyStop(double price, int number) {
		String uid = UUID.randomUUID().toString();
		String accountString = Config.getInstance().getAccount();
		Message msg = MessageProvider.createNewOrderSingle(accountString, uid, Side.BUY, OrdType.STOP, price, number, "XBTCNY", '0');
		try {
			Session.sendToTarget(msg, this.sessionID);
		} catch (SessionNotFound e) {
			e.printStackTrace();
		}
	}



	public void sellStop(double price, int number) {
		String uid = UUID.randomUUID().toString();
		String accountString = Config.getInstance().getAccount();
		Message msg = MessageProvider.createNewOrderSingle(accountString, uid, Side.SELL, OrdType.STOP, price, number, "XBTCNY", '0');
		try {
			Session.sendToTarget(msg, this.sessionID);
		} catch (SessionNotFound e) {
			e.printStackTrace();
		}
	}



	public void buyMarket(double price, int number) {
		String uid = UUID.randomUUID().toString();
		String accountString = Config.getInstance().getAccount();
		Message msg = MessageProvider.createNewOrderSingle(accountString, uid, Side.BUY, OrdType.MARKET, price, number, "XBTCNY", '0');
		try {
			Session.sendToTarget(msg, this.sessionID);
		} catch (SessionNotFound e) {
			e.printStackTrace();
		}
	}



	public void sellMarket(double price, int number) {
		String uid = UUID.randomUUID().toString();
		String accountString = Config.getInstance().getAccount();
		Message msg = MessageProvider.createNewOrderSingle(accountString, uid, Side.SELL, OrdType.MARKET, price, number, "XBTCNY", '0');
		try {
			Session.sendToTarget(msg, this.sessionID);
		} catch (SessionNotFound e) {
			e.printStackTrace();
		}
	}



	public void addOrder() {
		// TODO Auto-generated method stub
		
	}



	public void removeOrder() {
		// TODO Auto-generated method stub
		
	}

//	public void updateMarketData() {
//		System.out.println("Opening UpdateMktData Thread Start!");
//		
//		this.app.busyMktupdating = true;
//		try {
//			String uid = UUID.randomUUID().toString();
//			Message msg = MessageProvider.createMarketDataRequest("XBTCNY", SubscriptionRequestType.SNAPSHOT, uid);
//
//			Session.sendToTarget(msg, this.sessionID);
//			
//			while(this.app.busyMktupdating){
//				System.out.println("MKT DATA UPDATING...");
//				Thread.sleep(1000);
//			}
//			System.out.println("FINISHED MKT DATA UPDATING!");
//		
//		} catch (SessionNotFound e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
	
	public MarketData getMktData() {
		// TODO Auto-generated method stub
		return this.app.getMarketData();
	}

	public ArrayList<Order> getOrderList() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean stopTrader() {
		this.init.stop();	
		System.out.println("Trading Bot ==> Trader Stopped!");
		return false;
	}



	public boolean updateOrderList() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void mktTest(){
		String uid = UUID.randomUUID().toString();
		String accountString = Config.getInstance().getAccount();
		Message msg = MessageProvider.createMarketDataRequest("XBTCNY", SubscriptionRequestType.SNAPSHOT, uid);
		
		try {
			Session.sendToTarget(msg, this.sessionID);
		} catch (SessionNotFound e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
