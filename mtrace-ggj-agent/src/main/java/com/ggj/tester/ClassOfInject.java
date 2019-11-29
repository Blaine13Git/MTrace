package com.ggj.tester;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassOfInject {

    private static ClassOfInject classOfInject;

    public static ClassOfInject getInstance() {
        if (classOfInject == null) {
            return classOfInject = new ClassOfInject();
        } else {
            return classOfInject;
        }
    }


}
