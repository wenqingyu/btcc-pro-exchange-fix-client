package com.btcc.trading;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import com.btcc.Config;
import com.btcc.MessageProvider;
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
import quickfix.fix44.OrderCancelRequest;

public class ProexTrader implements Trader{

	private Initiator init;
	
	private TradeApplication app;
	
	private String accountString;
	
	private SessionID sessionID;
	
	private ArrayList<Message> actionMsgQuene = new ArrayList<Message>();
	
	@Override
	public boolean startTrader() throws FileNotFoundException, ConfigError, InterruptedException, SessionNotFound, FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType, RejectLogon {
		// TODO Auto-generated method stub
		// INITIALIZE
		
		this.app = new TradeApplication();
		InputStream inputStream = new FileInputStream("quickfix-client.properties");
		SessionSettings settings = new SessionSettings(inputStream);
		MessageStoreFactory storeFactory = new FileStoreFactory(settings);
		LogFactory logFactory = new FileLogFactory(settings);
		MessageFactory messageFactory = new DefaultMessageFactory();
		
		this.init = new SocketInitiator(app, storeFactory, settings, logFactory, messageFactory);
			
//		app.testMarketMessage(app.getSessionID());
		
		this.init.start();
		
		// Parameter Initialize
		this.accountString = Config.getInstance().getAccount();
		
		System.out.println("Trading Bot ==> Waiting for login . . .");
		while(!this.init.isLoggedOn()){
			System.out.println(".");
			Thread.sleep(200);
		}
		
		this.sessionID = app.getSessionID();
		System.out.println("Trading Bot ==> SessionID: " + this.sessionID + " Login success!");
		
		return false;
	}
	
	
	@Override
	public void scheduler(){
		System.out.println("TradeApplication ==> QUEUE: " + this.actionMsgQuene.size());
		int count = 0;
		while(!this.actionMsgQuene.isEmpty()){
			System.out.println(this.getState());
			
			this.waitUntilFree();
			
			System.out.println("Trading Bot ==> Sending new action state: " + this.getState());
			Message msg = this.actionMsgQuene.get(0);
			this.processer(msg);
			this.actionMsgQuene.remove(0);
			System.out.println("Trading Bot ==> Sending new action state: " + this.getState());
			count ++;
			System.out.println("Count: " + count);
		}
		System.out.println("Trading Bot ==> Finished Quene: " + count);
		this.waitUntilFree();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Help Method to Execute Message
	 * @param msg
	 */
	private void processer(Message msg){
		try {
			Session.sendToTarget(msg, this.sessionID);
		} catch (SessionNotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public boolean buyLimit(double price, int number) throws InterruptedException {
		System.out.println("Trading Bot ==> In BuyLimit");
		String uid = UUID.randomUUID().toString();
		this.regenAccountString();
		Message msg = MessageProvider.createNewOrderSingle(this.accountString, uid, Side.BUY, OrdType.LIMIT, price, number, "XBTCNY", '0');
		this.actionMsgQuene.add(msg);
	
		return false;
	}

	@Override
	public boolean sellLimit(double price, int number) {
		System.out.println("Trading Bot ==> In SellLimit");
		String uid = UUID.randomUUID().toString();
		this.regenAccountString();
		Message msg = MessageProvider.createNewOrderSingle(this.accountString, uid, Side.SELL, OrdType.LIMIT, price, number, "XBTCNY", '0');
		this.actionMsgQuene.add(msg);
		
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean buyStop(double price, int number) {
		System.out.println("Trading Bot ==> In buyStop");
		String uid = UUID.randomUUID().toString();
		this.regenAccountString();
		Message msg = MessageProvider.createNewOrderSingle(this.accountString, uid, Side.BUY, OrdType.STOP, price, number, "XBTCNY", '0');
		this.actionMsgQuene.add(msg);
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sellStop(double price, int number) {
		System.out.println("Trading Bot ==> In SellStop");
		String uid = UUID.randomUUID().toString();
		this.regenAccountString();
		Message msg = MessageProvider.createNewOrderSingle(this.accountString, uid, Side.SELL, OrdType.STOP, price, number, "XBTCNY", '0');
		this.actionMsgQuene.add(msg);
		this.regenAccountString();
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean buyMarket(double price, int number) {
		System.out.println("Trading Bot ==> In BuyMarket");
		String uid = UUID.randomUUID().toString();
		this.regenAccountString();
		Message msg = MessageProvider.createNewOrderSingle(this.accountString, uid, Side.BUY, OrdType.MARKET, price, number, "XBTCNY", '0');
		this.actionMsgQuene.add(msg);
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sellMarket(double price, int number) {
		System.out.println("Trading Bot ==> In SellMarket");
		String uid = UUID.randomUUID().toString();
		this.regenAccountString();
		Message msg = MessageProvider.createNewOrderSingle(this.accountString, uid, Side.SELL, OrdType.MARKET, price, number, "XBTCNY", '0');
		this.actionMsgQuene.add(msg);
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean updateOrderList(){
		this.app.clearMyOrderList(); // Clear Previous Order List and Ready for New One
		
		System.out.println("Trading Bot ==> In UpdateOrderList");
		String uid = UUID.randomUUID().toString();
		this.regenAccountString();
		Message msg = MessageProvider.createOrderMassStatusRequest(this.accountString, "XBTCNY", "reqID1298699096");
		this.actionMsgQuene.add(msg);
		
		return true;
	}
	
	@Override
	public boolean updateMarketData(){
		Message msg = MessageProvider.createMarketDataRequest("XBTCNY", SubscriptionRequestType.SNAPSHOT, "req111");
		this.actionMsgQuene.add(msg);
		return true;
	}
	
	public boolean addCancelOrder(Order order){
//		System.out.println("Trading Bot ==> In AddCancelOrder");
		String clOrdID = UUID.randomUUID().toString();
		this.regenAccountString();
		Message msg = MessageProvider.createOrderCancelRequest(Config.getInstance().getAccount(), clOrdID, order.getSymbol(), order.getOrderId());
		this.actionMsgQuene.add(msg);

		return false;
	}
	

	@Override
	public boolean stopTrader() {
		this.regenAccountString();
		// TODO Auto-generated method stub
		this.init.stop();	
		System.out.println("Trading Bot ==> Trader Stopped!");
		return false;
	}
	
	
	
	/**
	 * 
	 * @return Get Current State
	 */
	
	public String getState(){
		return this.app.getState();
	}
	
	public void actionStart(){
		this.app.actionStart();
	}
	
	/**
	 * Regenerate Account String 
	 */
	private void regenAccountString(){
		this.accountString = Config.getInstance().getAccount();
	}
	
	private void waitUntilFree(){
		while(!this.getState().equals("Free")){
			// Sleep 20 for another Free state check
			System.out.println("Trading Bot ==> scheduler Busy, Not Free");
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Free");
	}
	
	/**
	 * 
	 * Action Msg Queue  
	 */
	public ArrayList<Message> getMessageList(){
		return this.actionMsgQuene;
	}
	
	
	/**
	 * CALL THIS METHOD ONLY AFTER YOU CALLED updateMarketData()
	 * @return Current MarketData
	 */
	@Override
	public MarketData getMktData(){
		return this.app.getMarketData();
	}
	
	/**
	 * CALL THIS METHOD ONLY AFTER YOU CALLED upateOrderList()
	 * @return
	 */
	@Override
	public ArrayList<Order> getOrderList(){
		return this.app.getOrderList();
	}
	
	/**
	 * CANCEL ALL CURRENT ORDERS
	 * BETTER FOR TEST PURPOSE
	 * USE AS FOR YOUR OWN RISK !!!
	 */
	public void cancelAllOrder(){
		System.out.println("Cancelling All Orders");
		
		for(int i = 0; i < this.getOrderList().size(); i ++){
			this.addCancelOrder(this.getOrderList().get(i));
		}
	}
	
	
	

}
