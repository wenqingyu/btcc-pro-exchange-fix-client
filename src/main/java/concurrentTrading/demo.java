package concurrentTrading;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.UUID;

import com.btcc.Config;
import com.btcc.MessageProvider;

import quickfix.ConfigError;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.OrdType;
import quickfix.field.Side;

public class demo {

	public static void main(String[] args) throws FileNotFoundException, ConfigError, InterruptedException, SessionNotFound, FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType, RejectLogon {
		// TODO Auto-generated method stub

		SimpleTrader trader = new SimpleTrader();
		trader.startTrader();
		CMDOperator op = new CMDOperator("Thomas", trader);
		

	}

}
