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

public class sample {

	public static void main(String[] args) throws FileNotFoundException, ConfigError, InterruptedException, SessionNotFound, FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType, RejectLogon {
		// TODO Auto-generated method stub
		
		System.out.println("Start!");
		ProexTrader bot = new ProexTrader();
		
		System.out.println("Bot: " + bot.startTrader());

		
		// 1. buy / sell order (limit, market, stop)
		bot.sellLimit(2500, 2);
		bot.buyLimit(1500, 2);
		bot.buyMarket(2000, 12); // for mkt order, price is not used, just fill up a int number
		bot.sellMarket(200, 12); // for mkt order, price is not used, just fill up a int number
		bot.buyStop(2000, 12);
		bot.sellStop(123, 23);
		
		bot.scheduler();
		/* Once you called buy/sell instruction, this instruction will be push into a execution queue
		 *  and wait until bot.scheduler called, all the instructions in the queue will be executed one by one */
		
		// 2. update mkt data, get personal order list
		// data updating also follow the instruction queue approach
		
		bot.updateMarketData();
		bot.updateOrderList();
		// updating mktData and OrderList
		bot.scheduler();
		// retrieve mktData and OrderList from order
		// play with them in your own strategy
		ArrayList<Order> orderList = bot.getOrderList();
		MarketData mktData = bot.getMktData();
		
		// NOTICE: IN THESE TWO EXECUTION, IT MIGHT TAKE A WHILE TO DIGEST FIX RESPONSE
		// SCHEDULER HAS A BUILD IN WAITTING FUNCTION TO PREVENT BOT FROM RESPONSE CONJESTION
		// TO HAVE A STABLE PERFORMANCE, IT IS BETTER TO HAVE ADDITIONAL COUPLE SECONDS WAIT AFTER CALL 
		// THESE TWO INSTRUCTION
		
		/* At beginning mktData and orderList is empty in bot, you need to call updateMktData and updateOrderList instruction
		 * and execute them first by calling bot.shceduler(), then you can get fresh mktData and orderList  */
		
		
		// 3. Cancel Order
		// order can be cancelled with adding cancel order instruction into instruction queue
		// order id is required, it is contain in Order object
		
		 // assume we will cancel the first two orders from fresh order list we got in last step
		bot.addCancelOrder(orderList.get(0));
		bot.addCancelOrder(orderList.get(1));
		
		// Having bot update orderlist at end of execution queue
		bot.updateOrderList();
		bot.scheduler();
		
		// get fresh order list after you canceled two orders
		orderList = bot.getOrderList();
		
		
		Thread.sleep(3000);
		
		// By calling bot.stopTrader(), bot will force quit session to essure connection is over
		// To ensure your last execution being finished, have a resonable wait in Tread.sleep() in a wise action

		bot.stopTrader();
		System.out.println("End");
	}

}
