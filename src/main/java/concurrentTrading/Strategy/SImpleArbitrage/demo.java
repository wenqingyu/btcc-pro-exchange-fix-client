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

public class demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		SimpleArbitrage stre = new SimpleArbitrage();
		
		SimpleTrader trader = new SimpleTrader();
		try {
			trader.startTrader();
//			trader.buyLimit(2400, 11);
//			
			trader.updateMarketData();
//	
			System.out.println(trader.getMktData());
//			
//			
			
			
			
			
			
			
			
			
			
			
		} catch (FileNotFoundException | ConfigError | InterruptedException | SessionNotFound | FieldNotFound
				| IncorrectDataFormat | IncorrectTagValue | UnsupportedMessageType | RejectLogon e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
