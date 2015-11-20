# BTCC Pro Exchange FIX API Trading Engine

## Introduction
This Repository include multiple branches, 
**trade-engine** is a FIX wrapper for BTCC Pro-exchange FIX API
**unit-test** is FIX api performance routine test

If you are a developer who wants to improve this client-engine or you simply want to implement your strategy with FIX Engine, **trade-engine** branch is a good one to start with. We will keep maintaining on it. If you want to contribute to this branch, we will happy to see you folk.

This FIX engine is build on top of open source java library [quickfix/j](http://www.quickfixj.org/) , check their github repository for more detail [https://github.com/quickfix-j/quickfixj](https://github.com/quickfix-j/quickfixj).

## Documentation
There are currently two documents for BTCC Pro-exchange FIX API
#### [Pro Exchange Market Data API Documentation](https://www.btcc.com/apidocs/fix-pro-exchange-market-api)
This document include general Market Data
* price data (bid / ask)
* market depth (level 1 order book)
check MarketData Class later for more detail


#### [Pro Exchange Trade API Documentation](https://www.btcc.com/apidocs/fix-pro-exchange-trade-api)
This document include all the trading instructions you are allowed to through FIX API
* Buy / Sell
* Update Personal Account Info
* Cancel Order
* AccountInfo


## Installation
checkout repository first

`git clone https://github.com/BTCChina/btcc-fix-client.git`

switch to **trade-engine** branch

`git fetch`

`git checkout trade-engine`

## Configuration
**go the **quickfix-client.properties** to configure your trade engine**
Give your bot a unique name like

`SenderCompID=CLIENT-WenqingYu`

It is important to keep this name unique, since two session with same name cannot connect to server with same name

Then fill up your access key and secret key at bottom

``AccessKey=6446fa83-803e-42f0-986b-3q4273e18107``

``SecretKey=89146e5a-0344-4af2-8d16-23c681641219``


## Start FIX Trade
Using FIX API have many benefits on professional trading. Standardize request and response format is one. Also, since there is only one hand-shake confirmation need to start a session, it saves precious time from hand-shake confirm in each execution. Our FIX API support clinet-side send many requests at very short period of time gap, and the response will send back asynchronously, as long as your client could handle it otherwise it will queued up and cause packet miss or congestion.


This Trade Client Engine is a single process design follows a simple rule, first-in-first-out, and execute one by one. This might not the best design, but is a basic one to start with.

You can add all the instructions into an instruction queue in bot
then call the bot.scheduler to execute these instructions one by one and handle the responses

Here is the sample code:


`System.out.println("Start!");


#### Initialize ProexTrader Bot
		ProexTrader bot = new ProexTrader();
		
#### Start Bot
		System.out.println("Bot: " + bot.startTrader());

		
#### 1. buy / sell order (limit, market, stop)
		bot.sellLimit(2500, 2);
		bot.buyLimit(1500, 2);
		bot.buyMarket(2000, 12); // for mkt order, price is not used, just fill up a int number
		bot.sellMarket(200, 12); // for mkt order, price is not used, just fill up a int number
		bot.buyStop(2000, 12);
		bot.sellStop(123, 23);
		
		bot.scheduler();
		/* Once you called buy/sell instruction, this instruction will be push into a execution queue
		 *  and wait until bot.scheduler called, all the instructions in the queue will be executed one by one */
		
#### 2. update mkt data, get personal order list
		// data updating also follow the instruction queue approach
	
		bot.updateMarketData();
		bot.updateOrderList();
		// updating mktData and OrderList
		bot.scheduler();
		// retrieve mktData and OrderList from order
		// play with them in your own strategy
		ArrayList<Order> orderList = bot.getOrderList();
		MarketData mktData = bot.getMktData();

 NOTICE: IN THESE TWO EXECUTION, IT MIGHT TAKE A WHILE TO DIGEST FIX RESPONSE SCHEDULER HAS A BUILD IN WAITTING FUNCTION TO PREVENT BOT FROM RESPONSE CONGESTION TO HAVE A STABLE PERFORMANCE, IT IS BETTER TO HAVE ADDITIONAL COUPLE SECONDS WAIT AFTER CALL THESE TWO INSTRUCTIONs 
		
At beginning mktData and orderList is empty in bot, you need to call updateMktData and updateOrderList instruction and execute them first by calling `bot.shceduler()`, then you can get fresh mktData and orderList  
		
#### 3. Cancel Order
 order can be cancelled with adding cancel order instruction into instruction queue
 order id is required, it is contain in Order object
		
assume we will cancel the first two orders from fresh order list we got in last step

	        bot.addCancelOrder(orderList.get(0));
		bot.addCancelOrder(orderList.get(1));
	
	
Having bot update orderlist at end of execution queue

		bot.updateOrderList();
		bot.scheduler();
		

get fresh order list after you canceled two orders
 
	        orderList = bot.getOrderList();	
		Thread.sleep(3000);
		
 By calling `bot.stopTrader()`, bot will force quit session to ensure connection is over
 To ensure your last execution being finished, have a reasonable wait in Tread.sleep() in a wise action

                bot.stopTrader();
		System.out.println("End");
`

## FAQ

#### The program blocked on the login state  

Sometimes due to last program did not stop properly, then the bot.stopTrader() haven't been executed before program stopped. Kill last process manually to kill FIX session then try to run it again, if it still not logon, check if your internet on and access-key is generated correctly.

#### How to generate Access-key pair

Pro-exchange using same access key pair as Spot change. You can generate from API in Spot exchange.

#### What if I have question about this bot or FIX API?

Feel free to send email to ask support@btcc.com at anytime.



 
 
