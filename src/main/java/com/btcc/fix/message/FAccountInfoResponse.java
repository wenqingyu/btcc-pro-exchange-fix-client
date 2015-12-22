package com.btcc.fix.message;

import quickfix.DoubleField;
import quickfix.Group;
import quickfix.Message;

/**
 * Created by zhenning on 15/8/31.
 */
public class FAccountInfoResponse extends Message {
    public static final String MSGTYPE = "U2001";
    public FAccountInfoResponse() {
        super();
        getHeader().setField(new quickfix.field.MsgType(MSGTYPE));
    }

    public static class Amount extends DoubleField {

        public static final int FIELD = 8001;

        public Amount() {
            super(FIELD);
        }

        public Amount(double data) {
            super(FIELD, data);
        }
    }

    public static class UserID extends DoubleField {

        public static final int FIELD = 8002;

        public UserID() {
            super(FIELD);
        }

        public UserID(double data) {
            super(FIELD, data);
        }
    }

    public static class SumOfDeposit extends DoubleField {

        public static final int FIELD = 8003;

        public SumOfDeposit() {
            super(FIELD);
        }

        public SumOfDeposit(double data) {
            super(FIELD, data);
        }
    }

    public static class Cash extends DoubleField {

        public static final int FIELD = 8004;

        public Cash() {
            super(FIELD);
        }

        public Cash(double data) {
            super(FIELD, data);
        }
    }

    public static class TotalProfit extends DoubleField {

        public static final int FIELD = 8005;

        public TotalProfit() {
            super(FIELD);
        }

        public TotalProfit(double data) {
            super(FIELD, data);
        }
    }

    public static class TotalSize extends DoubleField {

        public static final int FIELD = 8006;

        public TotalSize() {
            super(FIELD);
        }

        public TotalSize(double data) {
            super(FIELD, data);
        }
    }

    public static class TotalInitMarginRequired extends DoubleField {

        public static final int FIELD = 8007;

        public TotalInitMarginRequired() {
            super(FIELD);
        }

        public TotalInitMarginRequired(double data) {
            super(FIELD, data);
        }
    }

    public static class TotalMaintenanceMarginRequired extends DoubleField {

        public static final int FIELD = 8009;

        public TotalMaintenanceMarginRequired() {
            super(FIELD);
        }

        public TotalMaintenanceMarginRequired(double data) {
            super(FIELD, data);
        }
    }

    public static class UsableMargin extends DoubleField {

        public static final int FIELD = 8010;

        public UsableMargin() {
            super(FIELD);
        }

        public UsableMargin(double data) {
            super(FIELD, data);
        }
    }

    public static class RemainEquity extends DoubleField {

        public static final int FIELD = 8011;

        public RemainEquity() {
            super(FIELD);
        }

        public RemainEquity(double data) {
            super(FIELD, data);
        }
    }

    public static class TotalSellSize extends DoubleField {

        public static final int FIELD = 8012;

        public TotalSellSize() {
            super(FIELD);
        }

        public TotalSellSize(double data) {
            super(FIELD, data);
        }
    }

    public static class TotalBuySize extends DoubleField {

        public static final int FIELD = 8013;

        public TotalBuySize() {
            super(FIELD);
        }

        public TotalBuySize(double data) {
            super(FIELD, data);
        }
    }

    public static class OpenSize extends DoubleField {

        public static final int FIELD = 8014;

        public OpenSize() {
            super(FIELD);
        }

        public OpenSize(double data) {
            super(FIELD, data);
        }
    }

    public static class Profit extends DoubleField {

        public static final int FIELD = 8015;

        public Profit() {
            super(FIELD);
        }

        public Profit(double data) {
            super(FIELD, data);
        }
    }

    public static class MarketValue extends DoubleField {

        public static final int FIELD = 8016;

        public MarketValue() {
            super(FIELD);
        }

        public MarketValue(double data) {
            super(FIELD, data);
        }
    }

    public static class UnchargedFee extends DoubleField {

        public static final int FIELD = 8017;

        public UnchargedFee() {
            super(FIELD);
        }

        public UnchargedFee(double data) {
            super(FIELD, data);
        }
    }

    public static class InitMarginRequired extends DoubleField {

        public static final int FIELD = 8018;

        public InitMarginRequired() {
            super(FIELD);
        }

        public InitMarginRequired(double data) {
            super(FIELD, data);
        }
    }

    public static class MaintenanceMarginRequired extends DoubleField {

        public static final int FIELD = 8019;

        public MaintenanceMarginRequired() {
            super(FIELD);
        }

        public MaintenanceMarginRequired(double data) {
            super(FIELD, data);
        }
    }

    public static class InitMarginFactor extends DoubleField {

        public static final int FIELD = 8020;

        public InitMarginFactor() {
            super(FIELD);
        }

        public InitMarginFactor(double data) {
            super(FIELD, data);
        }
    }

    public static class MaintenanceMarginFactor extends DoubleField {

        public static final int FIELD = 8021;

        public MaintenanceMarginFactor() {
            super(FIELD);
        }

        public MaintenanceMarginFactor(double data) {
            super(FIELD, data);
        }
    }

    public static class ContractList extends Group {

        public static final int FIELD = 9001;

        public ContractList() {
            super(FIELD, 55, new int[]{55,8012,8013,8014,6,8015,8016,8017,8018,8019,8020,8021,0});
        }
    }
}
