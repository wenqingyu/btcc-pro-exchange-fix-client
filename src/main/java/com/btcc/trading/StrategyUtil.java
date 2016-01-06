package com.btcc.trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StrategyUtil {

	private ProexTrader bot;
	
	private ArrayList<Order> ordList;
	
	public StrategyUtil(ProexTrader bot) {
		// TODO Auto-generated constructor stub
		this.bot = bot;
		this.initialize();
		
	}
	
	public void printOrderPrice(ArrayList<Order> ordList){
		for(int i = 0; i < ordList.size(); i ++){
			Order order = ordList.get(i);
			System.out.println(order.getPrice());
		}
		
	}
	
	/**
	 * get order List by orderType
	 * @param orderType
	 * @return
	 */
	public ArrayList<Order> getSelectedOrders(String orderType){
//		System.out.println("");
		ArrayList<Order> selectedOrderList = new ArrayList<Order>();
		this.ordList = bot.getOrderList();
		
		// Iterate order List
		for(int i = 0; i < this.ordList.size(); i ++){
			Order order = this.ordList.get(i);
			System.out.println("---------------------- Order Status: " + order.getOrdStatus());
			if(order.getOrdStatus().equals(orderType)){
				System.out.println("------------- find match order!");
				selectedOrderList.add(order);
			}
		}
		
		if(orderType.equals("New")){
			// sort by price price
			Collections.sort(selectedOrderList, new Comparator<Order>() {
				@Override
				public int compare(Order o1, Order o2) {
					if(Integer.parseInt(o1.getPrice()) > Integer.parseInt(o2.getPrice()) ){
						return 1;
					}
					return 0;
				}
			});
		}
		
		return selectedOrderList;
		
	}
	
	
	
	
	
	/**
	 *  Initialize
	 */
	public void initialize(){
		this.bot.updateOrderList();
		this.bot.updateBPI();
		this.bot.updateMarketData();
		this.bot.scheduler();
		this.ordList = this.bot.getOrderList();
	}
	
	/**
	 * Mkt Data Update
	 */
	public void mktDataUpdate(){
		this.bot.updateBPI();
		this.bot.updateMarketData();
		this.bot.scheduler();

	}
	
	

	
	
}

