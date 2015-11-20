package com.btcc.trading.strategy;

import com.btcc.trading.ProexTrader;

public class demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ProexTrader bot = new ProexTrader();
		
		CallMeMaybe s = new CallMeMaybe(bot, 5000);
		s.setPhoneNumber("147258369");
		s.startRoutine(5000);
		
	}

}
