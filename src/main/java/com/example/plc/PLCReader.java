package com.example.plc;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.*;
import org.apache.plc4x.java.api.types.PlcResponseCode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PLCReader {
    private PlcConnection connection;

    public PLCReader(String connectionString) {
        try {
            connection = new PlcDriverManager().getConnection(connectionString);
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        }
    }

    public void read() throws Exception {
        if (!connection.getMetadata().canRead())
            return;
        PlcReadRequest.Builder builder = connection.readRequestBuilder();
        builder.addItem("value-1", "holding-register:1[2]");
        builder.addItem("value-2", "holding-register:10[2]");
        PlcReadRequest readRequest = builder.build();
//        CompletableFuture<? extends PlcReadResponse> asyncResponse = readRequest.execute();
//        asyncResponse.whenComplete((response, throwable) -> {
//            if (response != null) {
//                printResponse(response);
//            } else {
//                System.out.println("An error occurred: " + throwable.getMessage());
//                throwable.printStackTrace();
//            }
//        });
        PlcReadResponse readResponse = readRequest.execute().get();
        System.out.println(readResponse.getShort("value-1", 0));
        System.out.println(readResponse.getShort("value-1", 1));
        System.out.println(readResponse.getShort("value-2", 0));
        System.out.println(readResponse.getShort("value-2", 1));
        connection.close();
//        return readResponse.getShort("holding-register");
    }

    public void read(String[] register) throws Exception {
        if (!connection.getMetadata().canRead())
            return;
        PlcReadRequest.Builder builder = connection.readRequestBuilder();
//        for (int i = 0; i < register.length; i++) {
//            builder.addItem("value-" + (i + 1), register[i]);
//        }
        builder.addItem("value-1", "holding-register:1[2]");
        builder.addItem("value-2", "holding-register:10[2]");
        PlcReadRequest readRequest = builder.build();
        CompletableFuture<? extends PlcReadResponse> asyncResponse = readRequest.execute();
        asyncResponse.whenComplete((response, throwable) -> {
            if (response != null) {
                printResponse(response);
            } else {
                System.out.println("An error occurred: " + throwable.getMessage());
                throwable.printStackTrace();
            }
        });
        System.out.println("end");
//        PlcReadResponse readResponse = readRequest.execute().get();
//        System.out.println(readResponse.getShort("value-1", 0));
//        System.out.println(readResponse.getShort("value-1", 1));
        connection.close();
    }

    public void write(short value) throws ExecutionException, InterruptedException {
        if (!connection.getMetadata().canWrite())
            return;
        PlcWriteRequest.Builder builder = connection.writeRequestBuilder();
        builder.addItem("value-1", "holding-register:1", value);
        PlcWriteRequest writeRequest = builder.build();
        PlcWriteResponse writeResponse = writeRequest.execute().get();
        System.out.println(writeResponse.getResponseCode("value-1"));
    }

    public void write(short[] value) throws ExecutionException, InterruptedException {
        if (!connection.getMetadata().canWrite())
            return;
        PlcWriteRequest.Builder builder = connection.writeRequestBuilder();
        final String prefix = "holding-register:";
        final String suffix = "[1]";
        for (int i = 0; i < value.length; i++) {
            builder.addItem("value-" + (i + 1), prefix + (i + 1) + suffix, value[i]);
        }
        PlcWriteRequest writeRequest = builder.build();
        PlcWriteResponse writeResponse = writeRequest.execute().get();
        for (String fieldName : writeResponse.getFieldNames()) {
            if(writeResponse.getResponseCode(fieldName) == PlcResponseCode.OK) {
                System.out.println("Value[" + fieldName + "]: successfully written to device.");
            }
            // Something went wrong, to output an error message instead.
            else {
                System.out.println("Error[" + fieldName + "]: " + writeResponse.getResponseCode(fieldName).name());
            }
        }
    }

    private static void printResponse(PlcReadResponse response) {
        for (String fieldName : response.getFieldNames()) {
            if(response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                int numValues = response.getNumberOfValues(fieldName);
                // If it's just one element, output just one single line.
                if(numValues == 1) {
                    System.out.println("Value[" + fieldName + "]: " + response.getObject(fieldName));
                }
                // If it's more than one element, output each in a single row.
                else {
                    System.out.println("Value[" + fieldName + "]:");
                    for(int i = 0; i < numValues; i++) {
                        System.out.println(" - " + response.getObject(fieldName, i));
                    }
                }
            }
            // Something went wrong, to output an error message instead.
            else {
                System.out.println("Error[" + fieldName + "]: " + response.getResponseCode(fieldName).name());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        PLCReader reader = new PLCReader("modbus:tcp://localhost:502");
        String s = "holding-register:";
        String[] reg = new String[2];
        reg[0] = s + "1[2]";
        reg[1] = s + "10[2]";
//        reader.read(reg);
//        reader.read();
//        reader.write((short) 28);
        short[] value = {100, 113, 126, 139, 147};
        reader.write(value);
    }
}
