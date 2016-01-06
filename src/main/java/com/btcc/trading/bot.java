package com.btcc.trading;

import java.io.FileNotFoundException;
import quickfix.ConfigError;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.RejectLogon;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;

public class bot {

	public static void main(String[] args) throws FileNotFoundException, ConfigError, InterruptedException, SessionNotFound, FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType, RejectLogon {
		// TODO Auto-generated method stub

		ProexTrader trader = new ProexTrader();
		System.out.println("trader new");
		trader.startTrader();
		System.out.println("trader Start");
		StrategyUtil util = new StrategyUtil(trader);
		System.out.println("trader util");
		CMDOperator op = new CMDOperator("Thomas", trader, util);
		
		
		System.exit(0);
		
		
		
		
		
		
		
	}

}
