package com.btcc.trading;

import java.text.SimpleDateFormat;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.LastRptRequested;
import quickfix.field.LeavesQty;
import quickfix.field.OrdRejReason;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Text;
import quickfix.field.TransactTime;
import quickfix.fix44.ExecutionReport;

public class Order
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String orderId;
        char side;
        String symbol;
        String price;
        String avgPrice;
        String leavesQty;
        String cumQty;
        char ordStatus;
        char ordType;
        String date;
        boolean lastRptRequested;
        
        String massStatusReqID;

        int ordRejReason;
        String ordRejText;

        public Order(ExecutionReport executionReport) throws FieldNotFound {
            this.orderId = executionReport.getString(OrderID.FIELD);
            this.side = executionReport.getChar(Side.FIELD);
            this.symbol = executionReport.getString(Symbol.FIELD);
            try{
                this.price = executionReport.getString(Price.FIELD);
            }catch (FieldNotFound e){}

            try{
                this.ordType = executionReport.getChar(OrdType.FIELD);
            }catch (FieldNotFound e){}
            
            
     
            try{
                this.lastRptRequested = executionReport.getBoolean(LastRptRequested.FIELD);
            }catch (FieldNotFound e){}
            try{
                this.date = dateFormat.format(executionReport.getUtcTimeStamp(TransactTime.FIELD));
            }catch (FieldNotFound e){}
            
            try{
                this.massStatusReqID = executionReport.getString(584);
            }catch (FieldNotFound e){}

            this.avgPrice = executionReport.getString(AvgPx.FIELD);
            this.cumQty = executionReport.getString(CumQty.FIELD);
            this.leavesQty = executionReport.getString(LeavesQty.FIELD);
            this.ordStatus = executionReport.getChar(OrdStatus.FIELD);
            
            
            this.ordRejReason = getField(executionReport, OrdRejReason.FIELD, 0);
            
            

            this.ordRejText = getField(executionReport, Text.FIELD, "");

        }

        public String getAvgPrice() {
            return avgPrice;
        }

        public String getCumQty() {
            return cumQty;
        }

        public String getDate() {
            return date;
        }

        public String getLeavesQty() {
            return leavesQty;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getOrdStatus() {
            String retValue = null;
            switch (ordStatus){
                case OrdStatus.NEW:
                    retValue = "NEW";
                    break;
                case OrdStatus.PARTIALLY_FILLED:
                    retValue = "PARTIALLY_FILLED";
                    break;
                case OrdStatus.FILLED:
                    retValue = "FILLED";
                    break;
                case OrdStatus.DONE_FOR_DAY:
                    retValue = "DONE_FOR_DAY";
                    break;
                case OrdStatus.CANCELED:
                    retValue = "CANCELED";
                    break;
                case OrdStatus.REPLACED:
                    retValue = "REPLACED";
                    break;
                case OrdStatus.PENDING_CANCEL:
                    retValue = "PENDING_CANCEL";
                    break;
                case OrdStatus.STOPPED:
                    retValue = "STOPPED";
                    break;
                case OrdStatus.REJECTED:
                    retValue = "REJECTED";
                    break;
                case OrdStatus.SUSPENDED:
                    retValue = "SUSPENDED";
                    break;
                case OrdStatus.PENDING_NEW:
                    retValue = "PENDING_NEW";
                    break;
                case OrdStatus.CALCULATED:
                    retValue = "CALCULATED";
                    break;
                case OrdStatus.EXPIRED:
                    retValue = "EXPIRED";
                    break;
                case OrdStatus.ACCEPTED_FOR_BIDDING:
                    retValue = "ACCEPTED_FOR_BIDDING";
                    break;
                case OrdStatus.PENDING_REPLACE:
                    retValue = "PENDING_REPLACE";
                    break;
            }
            return retValue;
        }

        public String getSide() {
            return side == Side.BUY ? "Buy" : "Sell";
        }

        public String getSymbol() {
            return symbol;
        }

        public boolean getLastRptRequested()
        {
            return lastRptRequested;
        }

        public String getOrdType()
        {
            String retValue = null;
            if(ordType == OrdType.LIMIT){
                retValue = "limit";
            }else if(ordType == OrdType.MARKET){
                retValue = "market";
            }else if(ordType == OrdType.STOP){
                retValue = "stop";
            }

            return retValue;
        }

        public String getPrice()
        {
            return price;
        }

        public String getFilledTotal()
        {
            return String.format("%s/%d", cumQty, Integer.parseInt(cumQty) + Integer.parseInt(leavesQty));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Order order = (Order) o;

            return orderId.equals(order.orderId);

        }

        @Override
        public int hashCode() {
            return orderId.hashCode();
        }

        public int getOrdRejReason()
        {
            return ordRejReason;
        }

        public String getOrdRejText()
        {
            return ordRejText;
        }
        
        public String getField(Message message, int field, String defaultValue)
        {
            String value = defaultValue;
            try {
                value = message.getString(field);
            } catch (FieldNotFound fieldNotFound) {

            }
            return value;
        }

        public char getField(Message message, int field, char defaultValue)
        {
            char value = defaultValue;
            try {
            	
                value = message.getChar(field);
            } catch (FieldNotFound fieldNotFound) {
            	
            }
            
            return value;
        }
        
        public int getField(Message message, int field, int defaultValue)
        {
            int value = defaultValue;
            try {
                value = message.getInt(field);
            } catch (FieldNotFound fieldNotFound) {
            	
            }
            
            return value;
        }

        public String getField(Group group, int field)
        {
            String value = "";
            try {
                value = group.getString(field);
            } catch (FieldNotFound fieldNotFound) {

            }
            return value;
        }
    }