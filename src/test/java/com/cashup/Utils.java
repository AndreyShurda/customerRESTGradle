package com.cashup;

public class Utils {
    public static String createURLWithPort(Integer port, String uri) {
        return "http://localhost:" + port + uri;
    }


}
