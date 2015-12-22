package concurrentTrading.Strategy.SImpleArbitrage;

import java.io.FileNotFoundException;

import concurrentTrading.SimpleTrader;
import quickfix.ConfigError;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.RejectLogon;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;

public class SimpleArbitrage {

	private SimpleTrader trader;
	
	public SimpleArbitrage(){
		
		this.trader = new SimpleTrader();
		try {
			System.out.println("Start Strategy");
			this.trader.startTrader();
			
			this.trader.updateMarketData();
			
//			this.trader.buyLimit(2500, 10);
			
			
			
			
			
			
			
			
			
			
			
		} catch (FileNotFoundException | ConfigError | InterruptedException | SessionNotFound | FieldNotFound
				| IncorrectDataFormat | IncorrectTagValue | UnsupportedMessageType | RejectLogon e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
	
	
}
