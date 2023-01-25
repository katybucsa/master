package util;

import main.CommunicationProtocol.ProcessId;

public class Constants {

    public static final String clientHost = "127.0.0.1";
    public static final String hubIp = "127.0.0.1";
    public static final int hubPort = 5000;
    public static final String toAbstractionId = "app";
    public static final String systemId = "sys-1";
    public static final ProcessId hubProcessId = ProcessId.newBuilder().setHost(hubIp).setPort(hubPort).setOwner("hub").build();
    public static final String APP = "app";
    public static final String NNAR = "nnar";
    public static final String BEB = "beb";
    public static final String PL = "pl";
    public static final String HUB = "hub";
    public static final String EPFD = "epfd";
    public static final String ELD = "eld";
    public static final String UC = "uc";
    public static final String EC = "ec";
    public static final String EP = "ep";

}
