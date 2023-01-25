package util;

import communication.Torr2.NodeId;

public class Constants {

    public static final String clientHost = "127.0.0.1";
    public static final String hubIp = "127.0.0.1";
    public static final int hubPort = 5000;
    public static final NodeId hubId = NodeId.newBuilder().setHost(hubIp).setPort(hubPort).build();
}
