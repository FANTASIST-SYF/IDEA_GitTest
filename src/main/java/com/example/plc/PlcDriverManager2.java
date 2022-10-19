package com.example.plc;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriver;
import org.apache.plc4x.java.api.authentication.PlcAuthentication;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public class PlcDriverManager2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlcDriverManager.class);

    protected ClassLoader classLoader;

    private Map<String, PlcDriver> driverMap;

    public PlcDriverManager2() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public PlcDriverManager2(ClassLoader classLoader) {
        LOGGER.info("Instantiating new PLC Driver Manager with class loader {}", classLoader);
        this.classLoader = classLoader;
        driverMap = new HashMap<>();
        ServiceLoader<PlcDriver> plcDriverLoader = ServiceLoader.load(PlcDriver.class, classLoader);
        LOGGER.info("Registering available drivers...");
        for (PlcDriver driver : plcDriverLoader) {
            if (driverMap.containsKey(driver.getProtocolCode())) {
                throw new IllegalStateException(
                        "Multiple driver implementations available for protocol code '" +
                                driver.getProtocolCode() + "'");
            }
            LOGGER.info("Registering driver for Protocol {} ({})", driver.getProtocolCode(), driver.getProtocolName());
            driverMap.put(driver.getProtocolCode(), driver);
        }
        LOGGER.info("driver map = {}", driverMap);
    }

    /**
     * Connects to a PLC using the given plc connection string.
     *
     * @param url plc connection string.
     * @return PlcConnection object.
     * @throws PlcConnectionException an exception if the connection attempt failed.
     */
    public PlcConnection getConnection(String url) throws PlcConnectionException {
        PlcDriver driver = getDriverForUrl(url);
        PlcConnection connection = driver.getConnection(url);
        connection.connect();
        return connection;
    }

    /**
     * Connects to a PLC using the given plc connection string using given authentication credentials.
     *
     * @param url            plc connection string.
     * @param authentication authentication credentials.
     * @return PlcConnection object.
     * @throws PlcConnectionException an exception if the connection attempt failed.
     */
    public PlcConnection getConnection(String url, PlcAuthentication authentication) throws PlcConnectionException {
        PlcDriver driver = getDriverForUrl(url);
        PlcConnection connection = driver.getConnection(url, authentication);
        connection.connect();
        return connection;
    }

    /**
     * Returns the codes of all of the drivers which are currently registered at the PlcDriverManager
     * @return Set of driver codes for all drivers registered
     */
    public Set<String> listDrivers() {
        return driverMap.keySet();
    }

    /**
     * Returns suitable driver for protocol or throws an Exception.
     * @param protocolCode protocol code identifying the driver
     * @return Driver instance for the given protocol
     * @throws PlcConnectionException If no Suitable Driver can be found
     */
    public PlcDriver getDriver(String protocolCode) throws PlcConnectionException {
        PlcDriver driver = driverMap.get(protocolCode);
        if (driver == null) {
            throw new PlcConnectionException("Unable to find driver for protocol '" + protocolCode + "'");
        }
        return driver;
    }

    /**
     * Returns suitable driver for a given plc4x connection url or throws an Exception.
     * @param url Uri to use
     * @return Driver instance for the given url
     * @throws PlcConnectionException If no Suitable Driver can be found
     */
    public PlcDriver getDriverForUrl(String url) throws PlcConnectionException {
        try {
            URI connectionUri = new URI(url);
            String protocol = connectionUri.getScheme();
            return getDriver(protocol);
        } catch (URISyntaxException e) {
            throw new PlcConnectionException("Invalid plc4j connection string '" + url + "'", e);
        }
    }

    public static void main(String[] args) {
        new PlcDriverManager2();
    }
}
