package com.btcc.trading;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.Symbol;

public class AccountInfo {
		
	public String SumOfDeposit;
	
	public String Cash;
	
	public String TotalProfit;
	
	public String TotalSize;
	
	public String TotalInitMarginRequired;

	public String TotalMaintenanceMarginRequired;
	
	public String UsableMargin;

	public String RemainEquity;
	
	  
//
//	public String 
//	
//	public String 
//

	
	public AccountInfo(Message actInfoResponse){
		
		
		try{
//		 Message testMes = actInfoResponse;
         this.SumOfDeposit = actInfoResponse.getString(8003);         //SumOfDeposit
         this.Cash = actInfoResponse.getString(8004);                 //Cash
         this.TotalProfit = actInfoResponse.getString(8005);          //TotalProfit
         this.TotalSize = actInfoResponse.getString(8006);            //TotalSize
         this.TotalInitMarginRequired = actInfoResponse.getString(8007);     //TotalInitMarginRequired
         this.TotalMaintenanceMarginRequired = actInfoResponse.getString(8009); //TotalMaintenanceMarginRequired
         this.UsableMargin = actInfoResponse.getString(8010);         //UsableMargin
         this.RemainEquity = actInfoResponse.getString(8011);         //RemainEquity

//         Group group1 = actInfoResponse.getGroup(1, 9001);
//         Symbol1 = group1.getString(Symbol.FIELD);
//         TotalSellSize1 = getField(group1, 8012);
//         TotalBuySize1 = getField(group1, 8013);
//         OpenSize1 = group1.getString(8014);
//         AvgPx1 = group1.getString(AvgPx.FIELD);
//         Profit1 = group1.getString(8015);
//         MarketValue1 = group1.getString(8016);
//         UnchargedFee1 = group1.getString(8017);
//         InitMarginRequired1 = group1.getString(8018);
//         MaintenanceMarginRequired1 = group1.getString(8019);
//         InitMarginFactor1 = group1.getString(8020);
//         MaintenanceMarginFactor1 = group1.getString(8021);
//
//         Group group2 = actInfoResponse.getGroup(2, 9001);
//         Symbol2 = group2.getString(Symbol.FIELD);
//         TotalSellSize2 = getField(group2, 8012);
//         TotalBuySize2 = getField(group2, 8013);
//         OpenSize2 = group2.getString(8014);
//         AvgPx2 = group2.getString(AvgPx.FIELD);
//         Profit2 = group2.getString(8015);
//         MarketValue2 = group2.getString(8016);
//         UnchargedFee2 = group2.getString(8017);
//         InitMarginRequired2 = group2.getString(8018);
//         MaintenanceMarginRequired2 = group2.getString(8019);
//         InitMarginFactor2 = group2.getString(8020);
//         MaintenanceMarginFactor2 = group2.getString(8021);

     }catch (FieldNotFound fieldNotFound) {
         fieldNotFound.printStackTrace();
     }
	
}



}
