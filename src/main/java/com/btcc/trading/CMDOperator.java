package com.btcc.trading;

import java.util.Scanner;

public class CMDOperator {
	
	private String name;
	
	private ProexTrader trader;
	
	/* ------- STATUS PARAMETER --------- */
	
	private String mode;
	
	private StrategyUtil util;
	
	private Scanner scan;
	
	
	public CMDOperator(String name, ProexTrader trader, StrategyUtil util) throws InterruptedException{
		this.name = name;
		
		this.util = util;
		
		this.printLog("start!");
		
		this.printLog("Initializing Trader...");
		
		this.trader = trader;
		
		this.printLog("Trader Start Success!");
		
		this.util.initialize();
		
		this.run();
	}
	
	private void run() throws InterruptedException{
		
		this.scan = new Scanner(System.in);
		
		String input = "";
		this.mode = "pivotbot"; // DEFAULT MODE
		this.printLog("ENTER INPUT, -help for help info");
		while(scan.hasNext()){
			input = scan.nextLine().trim().toLowerCase();

			if(input.startsWith("help")){
				System.out.println(this.getHelp());
			}else if(input.startsWith("status")){
				System.out.println(this.getStatus());
			}else if(input.startsWith("mode")){
				// mode check and switch
				this.changeMode(input);
			}else if(input.startsWith("run")){
				if(this.mode.equals("pivotbot")){
					this.printLog("Start PivotBot Strategy . . . ");
					this.pivotBot();
					
				}
			}
			else if(input.startsWith("quit")){
				this.quit();
				this.printLog("bye bye!");
				break;
			}else{
				this.illegalInput();
			}
			
		}
		
		scan.close();
		return ;
	}
	
	
	/**
	 * change mode when input line start with mode
	 * @param inputLine
	 */
	private void changeMode(String inputLine){
		Scanner lscan = new Scanner(inputLine);
		
		String key = "";
		String value = "";
		if(lscan.hasNext()){
			key = lscan.next().trim();
			// If has second value, change to this mode
			if(lscan.hasNext()){
				value = lscan.next();
				// changing mode
				if(value.startsWith("test")){
					this.mode = "test";
				}else if(value.startsWith("pivotbot".toLowerCase())){ // MODE OPTION, ADD MORE HERE
					this.mode = "pivotbot";
				}else{
					this.printLog("invalid Mode!");
				}
				this.printLog("mode change to: " + this.mode);
			}
			// Print Current mode
			String modeStr = "mode => [" + this.mode + "] {test, pivotBot}"; // MODE PRINT
			this.printLog(modeStr);
		}
		lscan.close();
	}
	
	
	
	private void illegalInput(){
		this.printLog("Illegal Input!");
	}
	
	private String getHelp(){
		String output = "\n"
				+ "\n- help: get help infomation"
				+ "\n- status: get current statu parameters, try it out!"
				+ "\n- mode: get current mode and show option"
				+ "\n- mode xxx: swith to mode (xxx) ex: mode pivotBot"
				+ "\n  run: start bot"
				+ "\n- quit: quit CMD program"
				+ "\n";
		return output;
	}
	
	/**
	 * Get CMD Status Parameter
	 * @return
	 */
	private String getStatus(){
		String output = "\nMode: " + this.mode; 
		return output;
	}
	
	private void quit(){
		this.trader.stopTrader();
		
	}
	
	private void printLog(String input){
		System.out.println("OPERATOR (" + this.name + "): " + input);
	}
	
	/* ----------------- pivotBot Strategy ------------------- */
	
	private double pivot;
	
	private double c; // calibration
	
	private double currentBPI;
	
	/* ---  --- */
	private int upperBoundDepth;
	
	private int lowerBoundDepth;
	
	private double upperBoundHeight;
	
	private double lowerBoundHeight;
	
	private double waveTolerance;
	
	private boolean isAutoReload;
	
	
	private void pivotBot() throws InterruptedException{
		this.printLog("In PivotBot Process");
		this.pivotBotConfig();
		this.pivotBotProcess();
		
	}
	
	/**
	 * 1. Config: set params
	 */
	private void pivotBotConfig()
	{
		this.printLog("Start Pivot Bot configuration: ");
		String input;
		
		/* ----------- calibration ------------- */
		this.printLog("Enter Calibration Value: \n(the offeset from current BPI, + for higher price, - for lower price)");
		while(this.scan.hasNextLine()){
			try{
				this.c = Double.valueOf(this.scan.nextLine());
				this.printLog("Calibration = " + this.c);
				break;
			}catch(NumberFormatException e){
				this.printLog("Wrong number format, please enter (+/-) with number only!");
			}
			this.printLog("Enter Calibration Value: \n(the offeset from current BPI, + for higher price, - for lower price)");
			
		}
		
		/* ----------- waveTolerance ------------- */
		this.printLog("Enter Wave Tolerance Value: \n(the tolerance for BPI fluctuation, 1 => 1%, 2.5 => 2.5%)");
		while(this.scan.hasNextLine()){
			try{
				this.waveTolerance = Math.abs(Double.valueOf(this.scan.nextLine())/100);
				this.printLog("WaveTolerance = " + this.waveTolerance*100 + "%");
				break;
			}catch(NumberFormatException e){
				this.printLog("Wrong number format, please enter positive number only!");
			}
			this.printLog("Enter Wave Tolerance Value: \n(the tolerance for BPI fluctuation, 1 => 1%, 2.5 => 2.5%)");
			
		}
		
		/* ----------- upper / lower bound Depth ------------- */
		this.printLog("Enter upper / lower bound Depth Value: "
				+ "\n  enter one value (5): upper = lower = 5"
				+ "\n  enter two value (5 4): upper = 5, lower = 4");
		while(this.scan.hasNextLine()){
			input = this.scan.nextLine();
			String[] datas = input.split("[ ]+");
			try{
				if(datas.length == 1){ // 1 input value
					this.upperBoundDepth = Integer.parseInt(datas[0]);
					this.lowerBoundDepth =this.upperBoundDepth;
					this.printLog("Upper / lower Bound Depth = " + this.upperBoundDepth);
					break;
				}else if(datas.length > 1){ // 2 input values
					this.upperBoundDepth = Integer.parseInt(datas[0]);
					this.lowerBoundDepth = Integer.parseInt(datas[1]);
					this.printLog("Upper Bound Height = " + this.upperBoundDepth);
					this.printLog("lower Bound Height: " + this.lowerBoundDepth);
					break;
				}else{
					this.printLog("Format Illegal!");
				}
				
			}catch(NumberFormatException e){
				this.printLog("Wrong number format, please enter (+/-) with number only!");
			}
			this.printLog("Enter upper / lower bound Depth Value: "
					+ "\n  enter one value (5): upper = lower = 5"
					+ "\n  enter two value (5 4): upper = 5, lower = 4");
		}
		
		/* ----------- upper / lower bound Height ------------- */
		this.printLog("Enter upper / lower bound Height Value: "
				+ "\n  enter one value (2.5): upper = lower = 2.4"
				+ "\n  enter two value (2.5 3): upper = 2.5, lower = 3");
		while(this.scan.hasNextLine()){
			input = this.scan.nextLine();
			String[] datas = input.split("[ ]+");
			try{
				if(datas.length == 1){ // 1 input value
					this.upperBoundHeight = Double.valueOf(datas[0]);
					this.lowerBoundHeight = this.upperBoundHeight;
					this.printLog("Upper / lower Bound Height = " + this.upperBoundHeight);
					break;
				}else if(datas.length > 1){ // 2 input values
					this.upperBoundHeight = Double.valueOf(datas[0]);
					this.lowerBoundHeight = Double.valueOf(datas[1]);
					this.printLog("Upper Bound Height = " + this.upperBoundHeight);
					this.printLog("lower Bound Height: " + this.lowerBoundHeight);
					break;
				}else{
					this.printLog("Format Illegal!");
				}
				
			}catch(NumberFormatException e){
				this.printLog("Wrong number format, please enter (+/-) with number only!");
			}
			this.printLog("Enter upper / lower bound Height Value: "
					+ "\n  enter one value (2.5): upper = lower = 2.4"
					+ "\n  enter two value (2.5 3): upper = 2.5, lower = 3");
		}
		
		/* ----------- isAutoReload ? ------------- */
		this.printLog("Do you need Reload Pending Order once some of them filled(y/n)?");
		while(this.scan.hasNextLine()){

			if(this.scan.nextLine().toLowerCase().startsWith("y")){
				this.isAutoReload = true;
				this.printLog("Is Auto Reload = " + this.isAutoReload);
				break;
			}else if(this.scan.nextLine().toLowerCase().startsWith("n")){
				this.isAutoReload = false;
				this.printLog("Is Auto Reload = " + this.isAutoReload);
				break;
			}else{
				this.printLog("Illegal Input");
				this.printLog("Do you need Reload Pending Order once some of them filled(y/n)?");
			}
			
			
		}

		
		System.out.println("\n----------- Configure Data --------------\n");
		this.printLog("Calibration = " + this.c);
		this.printLog("WaveTolerance = " + this.waveTolerance*100 + "%");
		this.printLog("Upper Bound Depth = " + this.upperBoundDepth);
		this.printLog("lower Bound Depth: " + this.lowerBoundDepth);
		this.printLog("Upper Bound Height = " + this.upperBoundHeight);
		this.printLog("lower Bound Height: " + this.lowerBoundHeight);
		this.printLog("Auto Reload = " + this.isAutoReload);
		System.out.println("\n----------- Configure Data --------------\n");
		
		this.printLog("Bot Start in ");
		try {
			for(int i = 3; i > 0; i --){
				System.out.print(i + " ");
				Thread.sleep(1000);
				
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 2. Main Process: auto
	 * @throws InterruptedException 
	 */
	private void pivotBotProcess() throws InterruptedException{
		this.util.mktDataUpdate();
		System.out.println(this.trader.getMktData());
		this.currentBPI = this.trader.getMktData().getBPI();
		this.pivotBotMktDataReport();
		
		this.pivotBotCalculate();
		this.pivotBotReload();
		
		while(true){
			
		}
		
		
		
	}
	
	private void pivotBotCalculate(){
		this.util.mktDataUpdate();
		this.printLog("down");
		this.currentBPI = this.trader.getMktData().getBPI();
		this.pivot = this.currentBPI + this.c;
	}
	
	/**
	 * Reload Orders with New Calculations
	 * @throws InterruptedException
	 */
	private void pivotBotReload() throws InterruptedException{
//		 cancel all previous orders
		this.trader.updateOrderList();
		this.trader.scheduler();
		this.trader.cancelAllOrder();
		 
		this.printLog("Ask orders: price -> " + (this.pivot + this.upperBoundHeight) + ", number -> " + this.upperBoundDepth);
		this.printLog("Bid orders: price -> " + (this.pivot - this.lowerBoundHeight) + ", number -> " + this.lowerBoundDepth);
		this.printLog("We are going to do this action in");
		try {
			for(int i = 5; i > 0; i --){
				System.out.print(i + " ");
				Thread.sleep(1000);	
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		this.printLog("In real case, we will place order like this way!");
		this.trader.buyLimit(this.priceRound(this.pivot - this.lowerBoundHeight), this.lowerBoundDepth);
		this.trader.sellLimit(this.priceRound((this.pivot + this.upperBoundHeight)), this.upperBoundDepth);
		this.trader.scheduler();
		
//		this.printLog("Ask orders: price -> " + (this.pivot + this.upperBoundHeight) + ", number -> " + this.upperBoundDepth);
//		this.printLog("Bid orders: price -> " + (this.pivot - this.lowerBoundHeight) + ", number -> " + this.lowerBoundDepth);
//		
		
	}
	
	/*
	 * PRINT MARKET REPORT
	 */
	private void pivotBotMktDataReport(){
		this.printLog("---------- Market Report -----------");
		System.out.println(this.trader.getMktData());
		this.printLog("---------- Market Report End -------");
		
	}
	
	private double priceRound(double input){
		return Math.round(input*10)/10;
	}
}
