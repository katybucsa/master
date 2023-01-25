package api;

import main.CommunicationProtocol.Message;
import process.AppSystem;

import java.util.Optional;

public abstract class Abstraction {

    public String abstractionId;
    protected AppSystem appSystem;

    public Abstraction(String abstractionId, AppSystem appSystem) {

        this.abstractionId = abstractionId;
        this.appSystem = appSystem;
        registerChildren();
    }

    public abstract void handle(Message message);

    public abstract void registerChildren();

    protected String getChildAbstractionId(String child, String register) {

        if (!"".equals(register))
            return abstractionId + "." + child + "[" + register + "]";
        return abstractionId + "." + child;
    }

    protected String getParentAbstractionId(String abstractionId) {

        return abstractionId.substring(0, abstractionId.lastIndexOf('.'));
    }
}
