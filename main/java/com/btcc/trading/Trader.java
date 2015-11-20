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

public interface Trader {

	public void scheduler();
	
	public boolean startTrader() throws FileNotFoundException, ConfigError, InterruptedException, SessionNotFound, FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType, RejectLogon;
	
	public boolean buyLimit(double price, int number) throws InterruptedException;
	
	public boolean sellLimit(double price, int number);
	
	public boolean buyStop(double price, int number);
	
	public boolean sellStop(double price, int number);
	
	public boolean buyMarket(double price, int number);
	
	public boolean sellMarket(double price, int number);
	
	public boolean updateOrderList();
	
	public ArrayList<Order> getOrderList();
	
	public boolean updateMarketData();
	
	public MarketData getMktData();
	
	public void cancelAllOrder();
	
	public boolean stopTrader();

	

	
	
	
}
