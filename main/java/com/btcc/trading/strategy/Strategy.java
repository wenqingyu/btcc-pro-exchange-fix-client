package com.btcc.trading.strategy;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.btcc.trading.AccountInfo;
import com.btcc.trading.MarketData;
import com.btcc.trading.Order;
import com.btcc.trading.ProexTrader;
import com.btcc.trading.Trader;

import quickfix.ConfigError;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.RejectLogon;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;

/**
 * 
 * @author thomasyu
 *
 *	If you want to implement a new strategy, abstract class [Strategy] is a good template to start with.
 *	To ensure the performance and data's accuracy, [Strategy] following a concept called Periodical Execution.
 *	To minimize "wasted" time gaps between each execution, we put a sort of executions into an execution quene,
 *	and pass entire exe quene into trader processor, and then the executions in the quene will be executed one
 *	by one but with very small latency. 
 *
 *	
 */

public abstract class Strategy {
	
	protected String logPrefix = "Strategy <" + Strategy.class.getName() + "> ==> ";
	
	protected AccountInfo accountInfo;

	protected MarketData mktData;
	
	protected ArrayList<Order> myOrderList;
	
	protected Trader bot;
	
	public Strategy(ProexTrader trader, int freq){
		this.printLog("Booting . . .");
		this.bot = trader;
		try {
			if(this.bot.startTrader()){
				this.printLog("Start Success :)");
			}else{
				this.printLog("Start Failed :(");
			}
		} catch (FileNotFoundException | ConfigError | InterruptedException | SessionNotFound | FieldNotFound
				| IncorrectDataFormat | IncorrectTagValue | UnsupportedMessageType | RejectLogon e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * routine is a endless market watcher
	 * it is running every freq (ms)
	 * also acting as a execution distributor if there is any initiative action need to be done
	 * @param freq
	 * if routine has no initiative action such as buy / sell / cancel to be done
	 * it will wait freq (ms) time for next routine inspection
	 */
	protected void startRoutine(int freq){

		this.startRoutine(freq);
	}

	protected void updateMktData(){
		this.bot.updateMarketData();
	}
	
	protected void updateMyOrderList(){
		this.bot.updateOrderList();
	}
	
	protected void getAccountInfo(){
		
	}

	protected void printLog(String info){
		System.out.println(this.logPrefix + info);
	}
	
	
}
