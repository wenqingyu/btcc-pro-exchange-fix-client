package com.btcc.fix.message;

import quickfix.FieldNotFound;
import quickfix.fix44.Message;

/**
 * Created by zhenning on 15/8/31.
 */
public class AccountInfoRequest extends Message {
    public static final String MSGTYPE = "U1000";
    public AccountInfoRequest() {
        super();
        getHeader().setField(new quickfix.field.MsgType(MSGTYPE));
    }

    public quickfix.field.Account getAccount() throws FieldNotFound {
        return get(new quickfix.field.Account());
    }

    public quickfix.field.Account get(quickfix.field.Account value) throws FieldNotFound {
        getField(value);
        return value;
    }

    public AccReqID get(AccReqID value) throws FieldNotFound {
        getField(value);
        return value;
    }

    public AccReqID getAccReqID() throws FieldNotFound {
        AccReqID value = new AccReqID();
        getField(value);

        return value;
    }

    public void set(AccReqID value) {
        setField(value);
    }

    public void set(quickfix.field.Account value) {
        setField(value);
    }
}
