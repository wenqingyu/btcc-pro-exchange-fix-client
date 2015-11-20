package com.btcc.trading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btcc.MessageProvider;
import com.btcc.fix.message.FAccountInfoResponse;

import quickfix.*;
import quickfix.field.ClOrdID;
import quickfix.field.LastRptRequested;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntries;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.Heartbeat;
import quickfix.fix44.Logon;
import quickfix.fix44.MarketDataSnapshotFullRefresh;
import quickfix.fix44.SecurityList;

public class TradeApplication implements quickfix.Application {
	//if you request marketdata you can set PARTNER and SECRET_KEY as "1"
	public static final String PARTNER = "your partner ";
	public static final String SECRET_KEY = "your securityKey";

	private Message messageOnLogon;
	private SessionID sessionId;
	private static final Logger log = LoggerFactory.getLogger(TradeApplication.class);
	
	private MarketData marketData;
	
	private ArrayList<Order> myOrderList = new ArrayList<Order>();
	
	
	
	private static String state;
	
	private static final String FREE = "Free";
	
	private static final String PROCESSING = "Processing";
	

	
	
	
	public void fromAdmin(quickfix.Message msg, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		String msgType = msg.getHeader().getString(35);
		if((!msgType.equals(Logon.MSGTYPE)) && (!msgType.equals(Heartbeat.MSGTYPE))){
			log.info("receivedType:" + msgType);
			log.info(sessionID+"------ fromAdmin--------"+msg.toString());
	
		}
	}

	public void fromApp(quickfix.Message msg, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		System.out.println("TradeApplication ==> FromAPP Start");

		String msgType = msg.getHeader().getString(35);
		if((!msgType.equals(Logon.MSGTYPE)) && (!msgType.equals(Heartbeat.MSGTYPE))){
//			log.info("receivedType:" + msgType);
//			log.info("        " + sessionID + "------ fromApp---------" + msg.toString());

			String[] fds = msg.toString().split("\u0001");
			for(String fd : fds)
			{
//				log.info(fd);
			}
			
			this.messageTranslation(msg, sessionID);
			System.out.println("TradeApplication ==> FromAPP End");
			
			
		}
	}

	public void onCreate(SessionID sessionID) {
		try {

			this.state = this.FREE;
			Session.lookupSession(sessionID).reset();
			this.sessionId = sessionID;
				
		} catch (IOException e) {
			e.printStackTrace();
		}
//		quickfix.Message message = OKMarketDataRequest.create24HTickerRequest();
//		Session.lookupSession(sessionID).send(message);
		log.info(sessionID+"------ onCreate Session-------"+sessionID);
	
	}
	
	
	public void onLogon(final SessionID sessionID) {
	
		this.state = this.FREE;
		this.sessionId = sessionID;
		if(messageOnLogon != null)
		{
			new Thread(()->{
				try {
					Session.sendToTarget(messageOnLogon, sessionID);
				} catch (SessionNotFound sessionNotFound) {
					sessionNotFound.printStackTrace();
				}
			}).start();
		}
		log.info(sessionID+"------ onLogon-------"+sessionID);
	}

	public void onLogout(SessionID sessionID) {
		this.sessionId = null;
		log.info(sessionID+"------ onLogout -------"+sessionID);
	}

	public void toAdmin(quickfix.Message msg, SessionID sessionID) {
//		msg.setField(new StringField(553, PARTNER));
//		msg.setField(new StringField(554, SECRET_KEY));
		log.info(sessionID+"------ toAdmin---------"+msg.toString());
	}

	public void toApp(quickfix.Message msg, SessionID sessionID) throws DoNotSend {
		this.state = this.PROCESSING;
		log.info("        " + sessionID+"------ toApp-----------"+msg.toString());
	}

	
	/* -------------  All the methods above are implementing / overriding original Application Class  ------------*/
	
	/**
	 * General Response Message Distributor
	 * 
	 * @param message
	 * @param sessionId
	 * @throws FieldNotFound
	 * ALL MESSAGE WILL GO INTO CORRESPONDING DECODING METHOD
	 */
	private void messageTranslation(Message message, SessionID sessionId) throws FieldNotFound {
		
		/*
		 * Always Remember to Set Free State Switch in Each Message Decoder
		 */
		System.out.println("Message Translation");
        String msgType = message.getHeader().getString(MsgType.FIELD);
        if(msgType.equals(SecurityList.MSGTYPE)){
        	System.out.println("Security List:");
//            onMessage((SecurityList)message, sessionId);
        }else if(msgType.equals(MarketDataSnapshotFullRefresh.MSGTYPE)){
            this.marketDataDecode((MarketDataSnapshotFullRefresh)message, sessionId);
        	System.out.println("Market Data:");
        	
        }else if(msgType.equals(FAccountInfoResponse.MSGTYPE)){
        	System.out.println("FAccountInfoResponse:");
//            onMessageAccountInfoResponse(message, sessionId);
        }else if(msgType.equals(ExecutionReport.MSGTYPE)){
        	System.out.println("Execution Report:");
        	this.executionResponseDecode((ExecutionReport)message);
        }
        
        System.out.println("Response Type: " + ExecutionReport.MSGTYPE);
        this.state = this.FREE;
		
    }
	
	/**
	 * MarketData Decoder
	 * # 1
	 * @param marketDataSnapshotFullRefresh
	 * @param sessionId
	 */
	private void marketDataDecode(MarketDataSnapshotFullRefresh marketDataSnapshotFullRefresh, SessionID sessionId) {
        try {
//            String symbol = marketDataSnapshotFullRefresh.getString(Symbol.FIELD);
            this.marketData = new MarketData();

            List<MarketData.OrderEntry> bidOrders = new ArrayList<>();
            List<MarketData.OrderEntry> askOrders = new ArrayList<>();
            List<Group> mDEntries = marketDataSnapshotFullRefresh.getGroups(NoMDEntries.FIELD);
            for (Group mDEntry : mDEntries)
            {
                MDEntryType mDEntryTypeField = new MDEntryType();
                mDEntry.getField(mDEntryTypeField);
                if(mDEntryTypeField.getValue() == MDEntryType.BID)
                {
                    double price = mDEntry.getDouble(MDEntryPx.FIELD);
                    double amount = mDEntry.getDouble(MDEntrySize.FIELD);
                    bidOrders.add(new MarketData.OrderEntry(price, amount));
                }else if(mDEntryTypeField.getValue() == MDEntryType.OFFER){
                    double price = mDEntry.getDouble(MDEntryPx.FIELD);
                    double amount = mDEntry.getDouble(MDEntrySize.FIELD);
                    askOrders.add(new MarketData.OrderEntry(price, amount));
                }else if(mDEntryTypeField.getValue() == MDEntryType.TRADE){
                	this.marketData.setLastPrice(mDEntry.getDouble(MDEntryPx.FIELD));
                }else if(mDEntryTypeField.getValue() == MDEntryType.TRADING_SESSION_HIGH_PRICE){
                	this.marketData.setHighPrice(mDEntry.getDouble(MDEntryPx.FIELD));
                }else if(mDEntryTypeField.getValue() == MDEntryType.TRADING_SESSION_LOW_PRICE){
                	this.marketData.setLowPrice(mDEntry.getDouble(MDEntryPx.FIELD));
                }else if(mDEntryTypeField.getValue() == MDEntryType.TRADE_VOLUME){
                	this.marketData.setVolume(mDEntry.getDouble(MDEntrySize.FIELD));
                }else if(mDEntryTypeField.getValue() == MDEntryType.CLOSING_PRICE){
                	this.marketData.setPrevClosePrice(mDEntry.getDouble(MDEntryPx.FIELD));
                }
            }
            this.marketData.refreshBidOrders(bidOrders);
            this.marketData.refreshAskOrders(askOrders);
            this.state = this.FREE;
            System.out.println("HIGH PRICE: " + this.marketData.getHighPrice());
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        }
    }
	
	/**
	 * Handle all the execution reponse
	 * buy / sell / cancel / mass order
	 * @param executionReport
	 * @throws FieldNotFound 
	 */
	private void executionResponseDecode(ExecutionReport executionReport) throws FieldNotFound{

		String clOrdID = getClOrdIDField(executionReport);
		
		if(clOrdID == null){return; } // Execution Report is Empty
		
		try {
			
			Order order = new Order(executionReport);
			// 1. Hold Case if Order being Rejected
			if(order.getOrdRejReason() != 0){
				System.out.println("TradeApplication ==> Order Request Rejected By: " + order.getOrdRejReason());
				this.state = this.FREE;
				return;
			}

			this.myOrderList.add(order);
			
		} catch (FieldNotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("TradeApplication ==> myOrderList length: " + this.myOrderList.size());
		this.state = this.FREE;
		
	}

	
	/**
	 * 
	 * @return get Updated MarketData
	 */
	public MarketData getMarketData(){
		return this.marketData;
	}
	
	/**
	 * Call it only after updated Order List
	 * @return get Current Order List
	 */
	public ArrayList<Order> getOrderList(){
		return this.myOrderList;
	}
	
	/**
	 * 
	 * @return get Current Session ID
	 */
	public SessionID getSessionID(){
		return this.sessionId;
	}
	
	/**
	 * 
	 * @return Current State of Connection
	 */
	public String getState(){
		return this.state;
	}
	
	/**
	 * Switch State to Sending
	 */
	public void actionStart(){
		this.state = this.PROCESSING;
	}
	
	/**
	 * Clear My Order List
	 */
	public void clearMyOrderList(){
		this.myOrderList = new ArrayList<Order>();
	}
	
	
	
	/* --------------------  Decode Help Method  --------------- */
	/**
	 * Get CLOrderID Filed from Message
	 * @param message
	 * @return
	 */
	private String getClOrdIDField(Message message)
    {
        ClOrdID clOrdID = new ClOrdID();
        try {
            message.getField(clOrdID);
        } catch (FieldNotFound fieldNotFound) {
            clOrdID = null;
        }
        return clOrdID.getValue();
    }
	
}
