package com.btcc.trading;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhenning on 15/9/8.
 */
public class MarketData {

    List<OrderEntry> bidOrders = new ArrayList<>();
    List<OrderEntry> askOrders = new ArrayList<>();

    double lastPrice;
    double openPrice;
    double prevClosePrice;
    double volume;
    double highPrice;
    double lowPrice;
    double BPI;
    double lowestAsk;
    double highestBid;
    

    public List<OrderEntry> getAskOrders() {
        return askOrders;
    }

    public List<OrderEntry> getBidOrders() {
        return bidOrders;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public double getPrevClosePrice() {
        return prevClosePrice;
    }

    public double getVolume() {
        return volume;
    }
    
    public double getBPI(){
    	return this.BPI;
    }
    
    public double getHighestBid(){
    	return this.highestBid;
    }
    
    public double getLowestAsk(){
    	return this.lowestAsk;
    }
    
    public void setBPI(double bpi){
    	this.BPI = bpi;
    }
    
    public void setHighestBid(double bid){
    	this.highestBid = bid;
    }
    
    public void setLowestAsk(double ask){
    	this.lowestAsk = ask;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public void setPrevClosePrice(double prevClosePrice) {
        this.prevClosePrice = prevClosePrice;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public boolean refreshBidOrders(List<OrderEntry> bidOrders)
    {
        this.bidOrders.clear();
        return this.bidOrders.addAll(bidOrders);
    }

    public boolean refreshAskOrders(List<OrderEntry> askOrders)
    {
        this.askOrders.clear();
        return this.askOrders.addAll(askOrders);
    }

    public String toString(){
    	String s = "BPI: " + this.BPI +
    			"\nhighPrice: " + this.highPrice +
    			"\nlowPrice: " + this.lowPrice +
    			"\nvolume: " + this.volume +
    			"\nopenPrice: " + this.openPrice +
    			"\nlastClosePrice: " + this.prevClosePrice +
    			"\nhighestBid: " + this.highestBid +
    			"\nlowestAsk: " + this.lowestAsk;
    	
		return s;
    	
    }
    

    public static class OrderEntry {
        double price;
        double amount;

        public OrderEntry(double price, double amount)
        {
            this.price = price;
            this.amount = amount;
        }

        public String toString()
        {
            return String.format("MARKET DATA: %f  %f", price, amount);
        }
    }


}
