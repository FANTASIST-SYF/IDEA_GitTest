package com.example.annotation;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        List<Instrument> instruments = creator();
        for (Instrument instrument : instruments) {
            log.info("创建对象：{}", instrument);
        }
        instrument = instruments.get(0);
        instrument2 = instruments.get(1);
    }

    public List<Instrument> creator() {
        Class<InstrumentCreator> clazz = InstrumentCreator.class;
        Field[] fields = clazz.getDeclaredFields();
        List<Instrument> list = new ArrayList<>();
        for (Field field : fields) {
            log.info("变量名称：{}", field.getName());
            if (field.isAnnotationPresent(MyField.class)) {
                MyField myField = field.getAnnotation(MyField.class);
                list.add(new Instrument(myField.address(), myField.instrumentName()));
            }
        }

        return list;
    }
}
