package com.btcc;

import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.*;
import quickfix.fix44.Heartbeat;
import quickfix.fix44.Logon;

public class FixApplication implements quickfix.Application {
	//if you request marketdata you can set PARTNER and SECRET_KEY as "1"
	public static final String PARTNER = "your partner ";
	public static final String SECRET_KEY = "your securityKey";

	private Message messageOnLogon;
	private static final Logger log = LoggerFactory.getLogger(FixApplication.class);
	
	public void fromAdmin(quickfix.Message msg, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		String msgType = msg.getHeader().getString(35);
		if((!msgType.equals(Logon.MSGTYPE)) && (!msgType.equals(Heartbeat.MSGTYPE))){
			log.info("receivedType:" + msgType);
			log.info(sessionID+"------ fromAdmin--------"+msg.toString());
		}
	}

	public void fromApp(quickfix.Message msg, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {

		String msgType = msg.getHeader().getString(35);
		if((!msgType.equals(Logon.MSGTYPE)) && (!msgType.equals(Heartbeat.MSGTYPE))){
			log.info("receivedType:" + msgType);
			log.info("        " + sessionID + "------ fromApp---------" + msg.toString());

			String[] fds = msg.toString().split("\u0001");
			for(String fd : fds)
			{
				log.info(fd);
			}
		}
	}

	public void onCreate(SessionID sessionID) {
		try {
			//there should invoke reset()
			Session.lookupSession(sessionID).reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		quickfix.Message message = OKMarketDataRequest.create24HTickerRequest();
//		Session.lookupSession(sessionID).send(message);
		log.info(sessionID+"------ onCreate Session-------"+sessionID);
	
	}
	
	
	public void onLogon(final SessionID sessionID) {

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
		log.info(sessionID+"------ onLogout -------"+sessionID);
	}

	public void toAdmin(quickfix.Message msg, SessionID sessionID) {
//		msg.setField(new StringField(553, PARTNER));
//		msg.setField(new StringField(554, SECRET_KEY));
		log.info(sessionID+"------ toAdmin---------"+msg.toString());
	}

	public void toApp(quickfix.Message msg, SessionID sessionID) throws DoNotSend {
		log.info("        " + sessionID+"------ toApp-----------"+msg.toString());
	}

	void testMarketMessage(final SessionID sessionID)
	{
//		quickfix.Message message;
//		message = BTCCMarketDataRequest.createIncrementalTickerRequest("XBTCQ5");
//		Session.lookupSession(sessionID).send(message);
	}
	
	private void runMethod(final SessionID sessionID) {
		testMarketMessage(sessionID);
	}

	public void setMessageOnLogon(Message messageOnLogon)
	{
		this.messageOnLogon = messageOnLogon;
	}
}
