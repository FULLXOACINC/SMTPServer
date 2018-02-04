package smtp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SmtpMessage {
    private Map<String, List<String>> headers;
    private StringBuilder body;

    public SmtpMessage() {
        headers = new LinkedHashMap<>();
        body = new StringBuilder();
    }

    public void store(SmtpResponse response, String params) {
        if (params != null) {
            if (SmtpState.DATA_HDR.equals(response.getNextState())) {
                int headerNameEnd = params.indexOf(':');
                if (headerNameEnd >= 0) {
                    String name = params.substring(0, headerNameEnd).trim();
                    String value = params.substring(headerNameEnd + 1).trim();
                    addHeader(name, value);
                }
            } else {
                if (SmtpState.DATA_BODY == response.getNextState()) {
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
}
