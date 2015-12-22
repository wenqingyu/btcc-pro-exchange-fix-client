package concurrentTrading;

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

	public boolean startTrader() throws FileNotFoundException, ConfigError, InterruptedException, SessionNotFound, FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType, RejectLogon;
	
	public void buyLimit(double price, int number) throws InterruptedException;
	
	public void sellLimit(double price, int number);
	
	public void buyStop(double price, int number);
	
	public void sellStop(double price, int number);
	
	public void buyMarket(double price, int number);
	
	public void sellMarket(double price, int number);
	
	public boolean updateOrderList();
	
	public void addOrder();
	
	public void removeOrder();
	
	public MarketData getMktData();
	
	public ArrayList<Order> getOrderList();

	public boolean stopTrader();

	
}
