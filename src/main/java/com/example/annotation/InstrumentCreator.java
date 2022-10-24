package com.example.annotation;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

//@Slf4j
public class InstrumentCreator {
    public static final Logger log = LoggerFactory.getLogger(InstrumentCreator.class);

    @MyField(address = 1, instrumentName = "TIC101")
    private final Instrument instrument;
    @MyField(address = 2, instrumentName = "TIC102")
    private final Instrument instrument2;

    public static void main(String[] args) {
        new InstrumentCreator();
    }

    public InstrumentCreator() {
        Instrument[] instruments = creator();
        instrument = instruments[1];
        instrument2 = instruments[2];
        log.info("创建对象1：{}", instrument);
        log.info("创建对象2：{}", instrument2);
    }

    public Instrument[] creator() {
        Class<InstrumentCreator> clazz = InstrumentCreator.class;
        Field[] fields = clazz.getDeclaredFields();
        Instrument[] instruments = new Instrument[fields.length];
        for (int i = 0; i < instruments.length; i++) {
            log.info("变量名称：{}", fields[i].getName());
            if (fields[i].isAnnotationPresent(MyField.class)) {
                MyField myField = fields[i].getAnnotation(MyField.class);
                instruments[i] = new Instrument(myField.address(), myField.instrumentName());
            }
        }

        return instruments;
    }
}
