package by.zhuk.smtpserver.smtp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SmtpMail {
    private Map<String, List<String>> headers;
    private String sender;
    private String sendType;
    private List<String> receivers;
    private StringBuilder body;

    public SmtpMail() {
        receivers = new ArrayList<>();
        headers = new LinkedHashMap<>();
        body = new StringBuilder();
    }

    public String getSender() {
        return sender;
    }

    public String getSendType() {
        return sendType;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public StringBuilder getBody() {
        return body;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSendType(String type) {
        this.sendType = type;
    }

    public void addReceiver(String receiver) {
        receivers.add(receiver);
    }

    public void store(SmtpState state, String params) {
        if (params != null) {
            if (SmtpState.DATA_HDR.equals(state)) {
                int headerNameEnd = params.indexOf(':');
                if (headerNameEnd >= 0) {
                    String name = params.substring(0, headerNameEnd).trim();
                    String value = params.substring(headerNameEnd + 1).trim();
                    addHeader(name, value);
                }
            } else {
                if (SmtpState.DATA_BODY == state) {
                    body.append(params);
                }
            }
        }
    }

    private void addHeader(String name, String value) {
        List<String> valueList = headers.get(name);
        if (valueList == null) {
            valueList = new ArrayList<>();
            headers.put(name, valueList);
        }
        valueList.add(value);
    }

    public void clear() {
        headers = new LinkedHashMap<>();
        body = new StringBuilder();
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        for (Map.Entry<String, List<String>> stringListEntry : headers.entrySet()) {
            for (String value : stringListEntry.getValue()) {
                msg.append(stringListEntry.getKey());
                msg.append(": ");
                msg.append(value);
                msg.append('\n');
            }
        }
        msg.append('\n');
        msg.append(body);
        msg.append('\n');

        return msg.toString();
    }

    public String findMailId() {
        String messageId = null;
        for (Map.Entry<String, List<String>> stringListEntry : headers.entrySet()) {
            for (String value : stringListEntry.getValue()) {
                if (stringListEntry.getKey().equals("Message-ID")) {
                    return value;
                }
            }
        }
        return messageId;
    }
}
