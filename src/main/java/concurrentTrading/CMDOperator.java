package concurrentTrading;

import java.util.Scanner;

public class CMDOperator {
	
	private String name;
	
	private SimpleTrader trader;
	
	/* ------- STATUS PARAMETER --------- */
	
	private String mode;
	
	
	public CMDOperator(String name, SimpleTrader trader){
		this.name = name;
		
		this.printLog("start!");
		
		this.printLog("Initializing Trader...");
		
		this.trader = trader;
		
		this.printLog("Trader Start Success!");
		
		this.initialize();
		
		this.run();
	}
	
	private void run(){
		Scanner scan = new Scanner(System.in);
		String input = "";
		this.mode = "test";
		this.printLog("ENTER INPUT, -help for help info");
		while(scan.hasNext()){
			input = scan.nextLine().trim().toLowerCase();

			if(input.startsWith("help")){
				System.out.println(this.getHelp());
			}else if(input.startsWith("status")){
				System.out.println(this.getStatus());
			}else if(input.startsWith("mode")){
				this.changeMode(input);
				// mode adjustment
				if(this.mode.equals("auto1")){
					
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
	
	
	
	private void initialize(){
		this.mode = "test";
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
			if(lscan.hasNext()){
				value = lscan.next();
				// changing mode
				if(value.startsWith("test")){
					this.mode = "test";
				}else if(value.startsWith("auto1")){
					this.mode = "auto1";
				}
				this.printLog("mode change to: " + this.mode);
			}else{
				this.printLog("mode: " + this.mode);
			}
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
	
	
	
}
