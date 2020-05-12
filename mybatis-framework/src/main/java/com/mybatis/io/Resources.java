package com.mybatis.io;

import java.io.InputStream;

public class Resources {


    public static InputStream getResource(String location) {
        return Resources.class.getClassLoader().getResourceAsStream(location);
    }
}
