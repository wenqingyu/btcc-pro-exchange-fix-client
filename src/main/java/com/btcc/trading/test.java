package com.btcc.trading;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import quickfix.ConfigError;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.RejectLogon;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;

public class test {

	public static void main(String[] args) throws FileNotFoundException, ConfigError, InterruptedException, SessionNotFound, FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType, RejectLogon {
		// TODO Auto-generated method stub

		System.out.println("Start!");
		ProexTrader bot = new ProexTrader();
		System.out.println("Bot: " + bot.startTrader());
		
		bot.updateBPI();
		bot.updateMarketData();
		bot.scheduler();
		
		MarketData mkt = bot.getMktData();
		
		System.out.println(mkt);
		
		
//		StrategyUtil util = new StrategyUtil(bot);
//		
//		ArrayList<Order> pendingList = util.getSelectedOrders("NEW");
//		
//		util.printOrderPrice(pendingList);
//		
//		bot.updateMarketData();
//		
//		bot.scheduler();
//		
//		MarketData mktData = bot.getMktData();
//		
//		System.out.println(mktData);
//		
		
		
		
		// PLACE ORDER TEST
		
//		for(int i = 0; i < 100; i ++){
//			bot.sellLimit(5000 + i, 1);
//		}
//		
//		bot.scheduler();
		
		//
//		bot.updateOrderList();
////		bot.updateMarketData();
//		
//		
//		
//		bot.scheduler();
//		
//		Thread.sleep(2000);
		
//		ArrayList<Order> ordList = bot.getOrderList();
		
//		System.out.println(ordList);
		
//		MarketData mktData = bot.getMktData();
		
//		System.out.println(mktData);

		
		bot.stopTrader();
		System.out.println("End");
	}

}
