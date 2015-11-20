package com.btcc.javafx;

import com.btcc.Config;
import com.btcc.GenAccountString;
import com.btcc.MessageProvider;
import com.btcc.fix.message.AccountInfoRequest;
import com.btcc.fix.message.FAccountInfoResponse;
import com.btcc.trading.MarketData;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.*;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.field.*;
import quickfix.fix44.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhenning on 15/9/8.
 */
public class MainController {
    static final Logger logger = LoggerFactory.getLogger(MainController.class);

    SessionID fixSessionId;

    ConcurrentHashMap<String, MarketData> marketDatas = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Message> cacheFixRequests = new ConcurrentHashMap<>();//uuid->message

    ConcurrentHashMap<String, List<Order>> cacheOrdersResponse = new ConcurrentHashMap<>();//uuid->message
    ScheduledService<Void> refreshMarketDataScheduledService;
    ScheduledService<Void> refreshTradeDataScheduledService;
    final List<String> symbolValues = new Vector<>();

    @FXML
    ChoiceBox<String> choiceBoxSymbols;

    @FXML ListView listViewBidOrders;
    @FXML ListView listViewAskOrders;

    @FXML Label labelLastPrice;
    @FXML Label labelOpenPrice;
    @FXML Label labelClosePrevPrice;
    @FXML Label labelHighPrice;
    @FXML Label labelLowPrice;
    @FXML Label labelVolumePrice;

    @FXML Label labelSumOfDeposit;  //8003
    @FXML Label labelCash;          //8004
    @FXML Label labelTotalProfit;   //8005
    @FXML Label labelTotalSize;     //8006
    @FXML Label labelTotalInitMarginRequired;           //8007
    @FXML Label labelTotalMaintenanceMarginRequired;    //8009
    @FXML Label labelUsableMargin;  //8010
    @FXML Label labelRemainEquity;  //8011

    @FXML Label labelSymbol1;
    @FXML Label labelTotalSellSize1;
    @FXML Label labelTotalBuySize1;
    @FXML Label labelOpenSize1;
    @FXML Label labelAvgPx1;
    @FXML Label labelProfit1;
    @FXML Label labelMarketValue1;
    @FXML Label labelUnchargedFee1;
    @FXML Label labelInitMarginRequired1;
    @FXML Label labelMaintenanceMarginRequired1;
    @FXML Label labelInitMarginFactor1;
    @FXML Label labelMaintenanceMarginFactor1;

    @FXML Label labelSymbol2;
    @FXML Label labelTotalSellSize2;
    @FXML Label labelTotalBuySize2;
    @FXML Label labelOpenSize2;
    @FXML Label labelAvgPx2;
    @FXML Label labelProfit2;
    @FXML Label labelMarketValue2;
    @FXML Label labelUnchargedFee2;
    @FXML Label labelInitMarginRequired2;
    @FXML Label labelMaintenanceMarginRequired2;
    @FXML Label labelInitMarginFactor2;
    @FXML Label labelMaintenanceMarginFactor2;

    @FXML private TextField usernameText;
    @FXML private TextField passwordText;

    @FXML private RadioButton radioLimitOrder;
    @FXML private RadioButton radioMarketOrder;
    @FXML private RadioButton radioStopOrder;

    @FXML private CheckBox checkBoxDay;

    @FXML private TextField priceText;
    @FXML private TextField amountText;

    @FXML private TableView<Order> tableViewOrders;
    @FXML private TableColumn<Order, String> orderId;
    @FXML private TableColumn<Order, String> orderDate;
    @FXML private TableColumn<Order, String> orderSymbol;
    @FXML private TableColumn<Order, String> orderType;
    @FXML private TableColumn<Order, String> orderBuySell;
    @FXML private TableColumn<Order, String> orderFilledTotal;
    @FXML private TableColumn<Order, String> orderPrice;
    @FXML private TableColumn<Order, String> orderStatus;
    @FXML private TableColumn<Order, String> orderAction;


    public void initialize()
    {
        radioMarketOrder.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue)
            {
                priceText.setDisable(true);
            }else {
                priceText.setDisable(false);
            }
        });

        choiceBoxSymbols.valueProperty().addListener((observable, oldSymbol, newSymbol) -> {
            if(newSymbol != null){
                MarketData curMarketData = marketDatas.get(newSymbol);
                if (curMarketData != null) {
                    refreshUI(newSymbol, curMarketData);
                }
            }
        });

        orderId.setCellValueFactory(new PropertyValueFactory<Order, String>("orderId"));
        orderDate.setCellValueFactory(new PropertyValueFactory<Order, String>("date"));
        orderSymbol.setCellValueFactory(new PropertyValueFactory<Order, String>("symbol"));
        orderType.setCellValueFactory(new PropertyValueFactory<Order, String>("ordType"));
        orderBuySell.setCellValueFactory(new PropertyValueFactory<Order, String>("side"));
        orderFilledTotal.setCellValueFactory(new PropertyValueFactory<Order, String>("filledTotal"));
        orderPrice.setCellValueFactory(new PropertyValueFactory<Order, String>("price"));
        orderStatus.setCellValueFactory(new PropertyValueFactory<Order, String>("ordStatus"));

        orderAction.setCellValueFactory(new PropertyValueFactory<Order, String>("leavesQty"));
        orderAction.setCellFactory(new Callback<TableColumn<Order, String>, TableCell<Order, String>>() {

            class CancelOrderButtonCell extends TableCell<Order, String> {
                final Button addButton = new Button("Cancel");

                public CancelOrderButtonCell()
                {
                    addButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            TableRow tableRow = getTableRow();
                            Order order = (Order) tableRow.getTableView().getItems().get(tableRow.getIndex());
                            cancelOrder(order);
                        }
                    });
                }

                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        if(Integer.parseInt(item) != 0){
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                            setGraphic(addButton);
                            return;
                        }
                    }
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(null);
                    return;
                }
            }
            @Override
            public TableCell<Order, String> call(TableColumn<Order, String> param) {
                return new CancelOrderButtonCell();
            }
        });


        new Thread(()->{
            try {
                InputStream inputStream = new FileInputStream("quickfix-client.properties");
                SessionSettings settings = new SessionSettings(inputStream);
                MessageStoreFactory storeFactory = new FileStoreFactory(settings);
                LogFactory logFactory = new FileLogFactory(settings);
                MessageFactory messageFactory = new DefaultMessageFactory();
                Initiator initiator = new SocketInitiator(new Application() {
                    @Override
                    public void onCreate(SessionID sessionId) {
                        logger.info("fix session onCreate {}", sessionId);
                        fixSessionId = sessionId;
                    }

                    @Override
                    public void onLogon(SessionID sessionId) {
                        logger.info("fix session onLogon {}", sessionId);

                        new Thread(()->{
                            onFixSessionLogon(sessionId);
                        }).start();
                    }

                    @Override
                    public void onLogout(SessionID sessionId) {
                        logger.info("fix session onLogout {}", sessionId);
                        fixSessionId = null;
                    }

                    @Override
                    public void toAdmin(Message message, SessionID sessionId) {
                        logger.info("fix session toAdmin {}, {}", message, sessionId);
                    }

                    @Override
                    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
                        logger.info("fix session fromAdmin {}, {}", message, sessionId);
                    }

                    @Override
                    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
                        logger.info("fix session toApp {}, {}", message, sessionId);
                    }

                    @Override
                    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
                        onMessage(message, sessionId);
                    }
                }, storeFactory, settings, logFactory, messageFactory);
                initiator.block();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ConfigError configError) {
                configError.printStackTrace();
            }
        }).start();
    }

    private void onFixSessionLogon(SessionID sessionId) {
        try {
            Message securityListRequest = MessageProvider.createSecurityListRequest(UUID.randomUUID().toString());
            Session.sendToTarget(securityListRequest, sessionId);
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    private void onMessage(Message message, SessionID sessionId) throws FieldNotFound {
        logger.info("fix session fromApp {}, {}", message, sessionId);
        String msgType = message.getHeader().getString(MsgType.FIELD);
        if(msgType.equals(SecurityList.MSGTYPE)){
            onMessage((SecurityList)message, sessionId);
        }else if(msgType.equals(MarketDataSnapshotFullRefresh.MSGTYPE)){
            onMessage((MarketDataSnapshotFullRefresh)message, sessionId);
        }else if(msgType.equals(FAccountInfoResponse.MSGTYPE)){
            onMessageAccountInfoResponse(message, sessionId);
        }else if(msgType.equals(ExecutionReport.MSGTYPE)){
            onMessage((ExecutionReport) message, sessionId);
        }
    }

    public void onMessage(ExecutionReport executionReport, SessionID sessionId) throws FieldNotFound {
        String clOrdID = getClOrdIDField(executionReport);
        if(clOrdID != null)
        {
            logger.info("onMessage clOrdID is {}", clOrdID);
            Message cacheMessage = cacheFixRequests.get(clOrdID);
            if(cacheMessage != null)
            {
                logger.info("onMessage cacheMessage is {}", cacheMessage.toString());

                String msgTypeCacheMsg = cacheMessage.getHeader().getString(MsgType.FIELD);
                if(msgTypeCacheMsg.equals(OrderMassStatusRequest.MSGTYPE)){

                    OrderMassStatusRequest orderMassStatusRequest = (OrderMassStatusRequest)cacheMessage;
                    List<Order> orders = cacheOrdersResponse.get(clOrdID);
                    if(orders == null){
                        orders = new ArrayList<Order>();
                        cacheOrdersResponse.put(clOrdID, orders);
                    }
                    Order nOrder = new Order(executionReport);
                    orders.add(nOrder);
                    if(nOrder.getLastRptRequested())
                    {
                        tableViewOrders.getItems().clear();
                        tableViewOrders.getItems().addAll(orders);
                        cacheOrdersResponse.remove(clOrdID);
                    }
                }else if(msgTypeCacheMsg.equals(NewOrderSingle.MSGTYPE)){
                    logger.debug("NewOrderSingle response {}", executionReport);
                    Order newOrder = new Order(executionReport);
                    if(newOrder.getOrdRejReason() > 0 || !newOrder.getOrdRejText().isEmpty())
                    {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("NewOrderSingle error");
                        alert.setHeaderText(null);
                        alert.setContentText("error code is " + newOrder.getOrdRejText());

                        alert.showAndWait();
                    }else {
                        tableViewOrders.getItems().remove(newOrder);
                        tableViewOrders.getItems().add(0, newOrder);
                    }
                }else if(msgTypeCacheMsg.equals(OrderCancelRequest.MSGTYPE)){
                    logger.debug("OrderCancelRequest response {}", executionReport);
                    tableViewOrders.getItems().remove(new Order(executionReport));
                }
            }
        }
    }

    public void onMessage(MarketDataSnapshotFullRefresh marketDataSnapshotFullRefresh, SessionID sessionId) {
        try {
            String symbol = marketDataSnapshotFullRefresh.getString(Symbol.FIELD);
            if(!this.marketDatas.containsKey(symbol))
            {
                this.marketDatas.put(symbol, new MarketData());
            }

            MarketData curMarketData = this.marketDatas.get(symbol);

            List<MarketData.OrderEntry> bidOrders = new ArrayList<>();
            List<MarketData.OrderEntry> askOrders = new ArrayList<>();
            List<Group> mDEntries = marketDataSnapshotFullRefresh.getGroups(NoMDEntries.FIELD);
            for (Group mDEntry : mDEntries)
            {
                MDEntryType mDEntryTypeField = new MDEntryType();
                mDEntry.getField(mDEntryTypeField);
                if(mDEntryTypeField.getValue() == MDEntryType.BID)
                {
                    double price = mDEntry.getDouble(MDEntryPx.FIELD);
                    double amount = mDEntry.getDouble(MDEntrySize.FIELD);
                    bidOrders.add(new MarketData.OrderEntry(price, amount));
                }else if(mDEntryTypeField.getValue() == MDEntryType.OFFER){
                    double price = mDEntry.getDouble(MDEntryPx.FIELD);
                    double amount = mDEntry.getDouble(MDEntrySize.FIELD);
                    askOrders.add(new MarketData.OrderEntry(price, amount));
                }else if(mDEntryTypeField.getValue() == MDEntryType.TRADE){
                    curMarketData.setLastPrice(mDEntry.getDouble(MDEntryPx.FIELD));
                }else if(mDEntryTypeField.getValue() == MDEntryType.TRADING_SESSION_HIGH_PRICE){
                    curMarketData.setHighPrice(mDEntry.getDouble(MDEntryPx.FIELD));
                }else if(mDEntryTypeField.getValue() == MDEntryType.TRADING_SESSION_LOW_PRICE){
                    curMarketData.setLowPrice(mDEntry.getDouble(MDEntryPx.FIELD));
                }else if(mDEntryTypeField.getValue() == MDEntryType.TRADE_VOLUME){
                    curMarketData.setVolume(mDEntry.getDouble(MDEntrySize.FIELD));
                }else if(mDEntryTypeField.getValue() == MDEntryType.CLOSING_PRICE){
                    curMarketData.setPrevClosePrice(mDEntry.getDouble(MDEntryPx.FIELD));
                }
            }
            curMarketData.refreshBidOrders(bidOrders);
            curMarketData.refreshAskOrders(askOrders);

            refreshUI(symbol, curMarketData);
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        }
    }

    public void onMessage(SecurityList securityList, final SessionID sessionId) throws FieldNotFound {
        symbolValues.clear();
        List<Group> groups = securityList.getGroups(NoRelatedSym.FIELD);
        for (Group group : groups)
        {
            Symbol symbol = new Symbol();
            group.getField(symbol);

            String symbolValue = symbol.getValue();
            symbolValues.add(symbolValue);
            logger.info("symbol is {}", symbolValue);
        }

        if(refreshMarketDataScheduledService == null)
        {
            refreshMarketDataScheduledService = new ScheduledService<Void>() {
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        protected Void call() {
                            try {
                                for(String symbolValue : symbolValues)
                                {
                                    String mDReqID = UUID.randomUUID().toString();
                                    MarketDataRequest marketDataRequest = MessageProvider.createMarketDataRequest(symbolValue, SubscriptionRequestType.SNAPSHOT, mDReqID);
                                    Session.sendToTarget(marketDataRequest, fixSessionId);
                                }
                            } catch (SessionNotFound sessionNotFound) {
                                sessionNotFound.printStackTrace();
                            }
                            return null;
                        }
                    };
                }
            };
            refreshMarketDataScheduledService.setPeriod(Duration.seconds(5));
            refreshMarketDataScheduledService.start();
        }

        Platform.runLater(() -> {
            choiceBoxSymbols.getItems().clear();
            for (String symbolValue : symbolValues) {
                choiceBoxSymbols.getItems().add(symbolValue);
            }
            choiceBoxSymbols.setValue(symbolValues.get(0));
        });
    }

    static String getField(Message message, int field, String defaultValue)
    {
        String value = defaultValue;
        try {
            value = message.getString(field);
        } catch (FieldNotFound fieldNotFound) {

        }
        return value;
    }

    static char getField(Message message, int field, char defaultValue)
    {
        char value = defaultValue;
        try {
            value = message.getChar(field);
        } catch (FieldNotFound fieldNotFound) {

        }
        return value;
    }

    static String getField(Group group, int field)
    {
        String value = "";
        try {
            value = group.getString(field);
        } catch (FieldNotFound fieldNotFound) {

        }
        return value;
    }

    void onMessageAccountInfoResponse(final Message accountInfoResponse, SessionID sessionId) {
        Platform.runLater(()->{
            try {

                Message testMes = accountInfoResponse;
                labelSumOfDeposit.setText(accountInfoResponse.getString(8003));         //SumOfDeposit
                labelCash.setText(accountInfoResponse.getString(8004));                 //Cash
                labelTotalProfit.setText(accountInfoResponse.getString(8005));          //TotalProfit
                labelTotalSize.setText(accountInfoResponse.getString(8006));            //TotalSize
                labelTotalInitMarginRequired.setText(accountInfoResponse.getString(8007));     //TotalInitMarginRequired
                labelTotalMaintenanceMarginRequired.setText(accountInfoResponse.getString(8009)); //TotalMaintenanceMarginRequired
                labelUsableMargin.setText(accountInfoResponse.getString(8010));         //UsableMargin
                labelRemainEquity.setText(accountInfoResponse.getString(8011));         //RemainEquity

                Group group1 = accountInfoResponse.getGroup(1, 9001);
                labelSymbol1.setText(group1.getString(Symbol.FIELD));
                labelTotalSellSize1.setText(getField(group1, 8012));
                labelTotalBuySize1.setText(getField(group1, 8013));
                labelOpenSize1.setText(group1.getString(8014));
                labelAvgPx1.setText(group1.getString(AvgPx.FIELD));
                labelProfit1.setText(group1.getString(8015));
                labelMarketValue1.setText(group1.getString(8016));
                labelUnchargedFee1.setText(group1.getString(8017));
                labelInitMarginRequired1.setText(group1.getString(8018));
                labelMaintenanceMarginRequired1.setText(group1.getString(8019));
                labelInitMarginFactor1.setText(group1.getString(8020));
                labelMaintenanceMarginFactor1.setText(group1.getString(8021));

                Group group2 = accountInfoResponse.getGroup(2, 9001);
                labelSymbol2.setText(group2.getString(Symbol.FIELD));
                labelTotalSellSize2.setText(getField(group2, 8012));
                labelTotalBuySize2.setText(getField(group2, 8013));
                labelOpenSize2.setText(group2.getString(8014));
                labelAvgPx2.setText(group2.getString(AvgPx.FIELD));
                labelProfit2.setText(group2.getString(8015));
                labelMarketValue2.setText(group2.getString(8016));
                labelUnchargedFee2.setText(group2.getString(8017));
                labelInitMarginRequired2.setText(group2.getString(8018));
                labelMaintenanceMarginRequired2.setText(group2.getString(8019));
                labelInitMarginFactor2.setText(group2.getString(8020));
                labelMaintenanceMarginFactor2.setText(group2.getString(8021));

            } catch (FieldNotFound fieldNotFound) {
                fieldNotFound.printStackTrace();
            }
        });
    }

    private void cancelOrder(Order order) {
        String clOrdID = UUID.randomUUID().toString();
        logger.debug("cancelOrder clOrdID is {}", clOrdID);
        OrderCancelRequest fixMessage = MessageProvider.createOrderCancelRequest(Config.getInstance().getAccount(), clOrdID, order.getSymbol(), order.getOrderId());
        try {
            cacheFixRequests.put(clOrdID, fixMessage);
            Session.sendToTarget(fixMessage, fixSessionId);
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    void refreshUI(String symbol, final MarketData marketData)
    {
        String curSymbol = choiceBoxSymbols.getValue();
        if(symbol.equals(curSymbol))
        {
            Platform.runLater(() -> {
                ObservableList<MarketData.OrderEntry> bidOrderObservableList = listViewBidOrders.getItems();
                bidOrderObservableList.clear();
                bidOrderObservableList.addAll(marketData.getBidOrders());

                ObservableList<MarketData.OrderEntry> askOrderObservableList = listViewAskOrders.getItems();
                askOrderObservableList.clear();
                askOrderObservableList.addAll(marketData.getAskOrders());

                labelLastPrice.textProperty().setValue(String.valueOf(marketData.getLastPrice()));
                labelOpenPrice.textProperty().setValue(String.valueOf(marketData.getOpenPrice()));
                labelClosePrevPrice.textProperty().setValue(String.valueOf(marketData.getPrevClosePrice()));
                labelHighPrice.textProperty().setValue(String.valueOf(marketData.getHighPrice()));
                labelLowPrice.textProperty().setValue(String.valueOf(marketData.getLowPrice()));
                labelVolumePrice.textProperty().setValue(String.valueOf(marketData.getVolume()));
            });
        }
    }

    @FXML
    public void getOrders(ActionEvent event) {
        if(fixSessionId != null){
            String uuidString = UUID.randomUUID().toString();
            Message orderMassStatusRequest = MessageProvider.createOrderMassStatusRequest(Config.getInstance().getAccount(), "ANY", uuidString);
            logger.debug("getOrders message is {}", orderMassStatusRequest);
            cacheFixRequests.put(uuidString, orderMassStatusRequest);
            try {
                Session.sendToTarget(orderMassStatusRequest, fixSessionId);
            } catch (SessionNotFound sessionNotFound) {
                sessionNotFound.printStackTrace();
            }
        }
    }

    @FXML
    void placeSellOrder(ActionEvent event) {
        placeOrder(Side.SELL);
    }

    @FXML
    void placeBuyOrder(ActionEvent event) {
        placeOrder(Side.BUY);
    }

    void placeOrder(char side) {
        try{
            String account = Config.getInstance().getAccount();
            String clOrdID = UUID.randomUUID().toString();
            char ordertype;
            if(radioMarketOrder.isSelected())
            {
                ordertype = OrdType.MARKET;
            }else if(radioStopOrder.isSelected()){
                ordertype = OrdType.STOP;
            }else {
                ordertype = OrdType.LIMIT;
            }

            double price = -1;
            try{
                price = Double.parseDouble(priceText.getText());
            }catch (NumberFormatException e){}

            double amount = Double.parseDouble(amountText.getText());
            String symbol = choiceBoxSymbols.getValue();
            char timeInForce = checkBoxDay.isSelected() ? TimeInForce.DAY : TimeInForce.GOOD_TILL_CANCEL;
            NewOrderSingle message = MessageProvider.createNewOrderSingle(account, clOrdID, side, ordertype, price, amount, symbol, timeInForce);
            cacheFixRequests.put(clOrdID, message);
            logger.debug("placeorder clOrdID {}", clOrdID);
            if(fixSessionId != null)
            {
                Session.sendToTarget(message, fixSessionId);
            }
        }catch (NumberFormatException e){
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    public String getClOrdIDField(Message message)
    {
        ClOrdID clOrdID = new ClOrdID();
        try {
            message.getField(clOrdID);
        } catch (FieldNotFound fieldNotFound) {
            clOrdID = null;
        }
        return clOrdID.getValue();
    }

    public void onClickStartButton(ActionEvent actionEvent) {

//        if(refreshTradeDataScheduledService == null)
//        {
//            refreshTradeDataScheduledService = new ScheduledService<Void>() {
//                protected Task<Void> createTask() {
//                    return new Task<Void>() {
//                        protected Void call() {
//                            try {
//                                AccountInfoRequest accountInfoRequest = MessageProvider.createAccountInfoRequest(getAccount(), UUID.randomUUID().toString());
//                                Session.sendToTarget(accountInfoRequest, fixSessionId);
//
//                                getOrders(null);
//                            } catch (SessionNotFound sessionNotFound) {
//                                sessionNotFound.printStackTrace();
//                            }
//                            return null;
//                        }
//                    };
//                }
//            };
//            refreshTradeDataScheduledService.setPeriod(Duration.seconds(5));
//            refreshTradeDataScheduledService.start();
//        }
        try {
            if(fixSessionId != null){
                AccountInfoRequest accountInfoRequest = MessageProvider.createAccountInfoRequest(Config.getInstance().getAccount(), UUID.randomUUID().toString());
                Session.sendToTarget(accountInfoRequest, fixSessionId);

                getOrders(null);
            }
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    public static class Order
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
        
        

        char ordRejReason;
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

            this.avgPrice = executionReport.getString(AvgPx.FIELD);
            this.cumQty = executionReport.getString(CumQty.FIELD);
            this.leavesQty = executionReport.getString(LeavesQty.FIELD);
            this.ordStatus = executionReport.getChar(OrdStatus.FIELD);

            this.ordRejReason = getField(executionReport, OrdRejReason.FIELD, (char) 0);
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

        public char getOrdRejReason()
        {
            return ordRejReason;
        }

        public String getOrdRejText()
        {
            return ordRejText;
        }
    }

}
