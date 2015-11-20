package com.btcc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

import javafx.stage.Stage;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.Side;
import quickfix.field.SubscriptionRequestType;

public class App
{
    public static void main( String[] args ) throws ConfigError, FileNotFoundException, InterruptedException {
		String accountString = Config.getInstance().getAccount();
//		testMessage(MessageProvider.createSecurityListRequest("req1"));
//		testMessage(MessageProvider.createMarketDataRequest("XBTCNY", SubscriptionRequestType.SNAPSHOT, "req111"));
//		testMessage(MessageProvider.createAccountInfoRequest(accountString, "req123"));
//		testMessage(MessageProvider.createNewOrderSingle(accountString, UUID.randomUUID().toString(), Side.BUY, OrdType.LIMIT, 2770.3, 1, "XBTCNY", '1'));
//		testMessage(MessageProvider.createNewOrderSingle(accountString, UUID.randomUUID().toString(), Side.BUY, OrdType.LIMIT, 2390.3, 1, "XBTCNY", '0'));

//		testMessage(MessageProvider.createOrderCancelRequest(accountString, "clOrdID12333", "XBTCNY", "c164bceb2a83414082bb95ef1ff9eb4d"));
		testMessage(MessageProvider.createOrderMassStatusRequest(accountString, "XBTCNY", "reqID1298699096"));
//		testMessage(MessageProvider.createOrderStatusRequest(accountString, "XBTCNY", "0662ca979f26499691ab37d596848325", "reqID12986"));
	}

	static void testMessage(quickfix.fix44.Message messageOnLogon) throws ConfigError, FileNotFoundException, InterruptedException {
		FixApplication fixapplication = new FixApplication();
		fixapplication.setMessageOnLogon(messageOnLogon);
		InputStream inputStream = new FileInputStream("quickfix-client.properties");
		SessionSettings settings = new SessionSettings(inputStream);
		MessageStoreFactory storeFactory = new FileStoreFactory(settings);
		LogFactory logFactory = new FileLogFactory(settings);
		MessageFactory messageFactory = new DefaultMessageFactory();
		Initiator initiator = new SocketInitiator(fixapplication, storeFactory, settings, logFactory, messageFactory);
		
		initiator.block();
		
		
//		initiator.start();
		
		
		
		
	}
}
