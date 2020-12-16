package com.vison.magpie.message;

/**
 *
 * @author vison.cao <visonforcoding@gmail.com>
 */
public class Message<T> {

    private MessageType type;
    private T body;
    private String from;

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
