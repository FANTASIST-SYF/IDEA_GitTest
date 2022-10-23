package com.example.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class InstrumentCreator {
    public static final Logger LOGGER = LoggerFactory.getLogger(InstrumentCreator.class);

    @MyField(address = 1, instrumentName = "TIC101")
    private final Instrument instrument;
    @MyField(address = 2, instrumentName = "TIC102")
    private final Instrument instrument2;

    public static void main(String[] args) {
        new InstrumentCreator();
    }

    public InstrumentCreator() {
        Instrument[] instruments = creator();
        instrument = instruments[0];
        instrument2 = instruments[1];
        LOGGER.info("创建对象1：{}", instrument);
        LOGGER.info("创建对象2：{}", instrument2);
        System.out.println(instrument);
        System.out.println(instrument2);
    }

    public static Instrument[] creator() {
        Class<InstrumentCreator> clazz = InstrumentCreator.class;
        Field[] fields = clazz.getDeclaredFields();
        Instrument[] instruments = new Instrument[fields.length];
        for (int i = 0; i < instruments.length; i++) {
            if (fields[i].isAnnotationPresent(MyField.class)) {
                MyField myField = fields[i].getAnnotation(MyField.class);
                instruments[i] = new Instrument(myField.address(), myField.instrumentName());
            }
        }
//        for (Field field : clazz.getDeclaredFields()) {
//            if (field.isAnnotationPresent(MyField.class)) {
//                MyField myField = field.getAnnotation(MyField.class);
//                return new Instrument(myField.address(), myField.instrumentName());
//            }
//        }

        return instruments;
    }
}
