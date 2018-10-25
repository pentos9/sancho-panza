package com.spacex.panza.protocol.reply;


import java.util.ArrayList;
import java.util.List;

public class ArrayOutputTest {
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        ArrayOutput arrayOutput = ArrayOutput.newArray();
        List<Result> results = doBuildResults();

        for (Result result : results) {
            if (result.isEmpty()) {
                continue;
            }

            ArrayOutput row = new ArrayOutput();
            row.append(StringOutput.of(result.getRow()));

            for (KeyValue kv : result.list()) {
                ArrayOutput item = ArrayOutput.newArray();
                item.append(StringOutput.of("family"));
                item.append(StringOutput.of(kv.getFamily()));
                item.append(StringOutput.of("qualifier"));
                item.append(StringOutput.of(kv.getQualifier()));
                item.append(StringOutput.of("value"));
                item.append(StringOutput.of(kv.getValue()));
                item.append(StringOutput.of("timestamp"));
                item.append(StringOutput.of(kv.getTimestamp()));
                row.append(item);
            }

            arrayOutput.append(row);
        }

        System.out.println(arrayOutput);

    }

    public static List<Result> doBuildResults() {
        List<Result> results = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Result result = new Result(String.valueOf(i));
            for (int j = 0; j < 5; j++) {
                KeyValue keyValue = new KeyValue("family" + j, "qualifier" + j, j);
                result.add(keyValue);
            }

            results.add(result);

        }
        return results;
    }


    private static class Result {
        private String row;
        List<KeyValue> keyValues = new ArrayList<>();

        public Result(String row) {
            this.row = row;
        }

        public String getRow() {
            return row;
        }

        public List<KeyValue> list() {
            return keyValues;
        }

        public boolean isEmpty() {
            return keyValues.isEmpty();
        }

        public void add(KeyValue keyValue) {
            if (keyValue == null) {
                return;
            }

            keyValues.add(keyValue);
        }
    }


    private static class KeyValue {
        private String family;
        private String qualifier;
        private long value;
        private long timestamp;

        public KeyValue(String family, String qualifier, long value) {
            this(family, qualifier, value, System.currentTimeMillis());
        }

        public KeyValue(String family, String qualifier, long value, long timestamp) {
            this.family = family;
            this.qualifier = qualifier;
            this.value = value;
            this.timestamp = timestamp;
        }

        public String getFamily() {
            return family;
        }

        public void setFamily(String family) {
            this.family = family;
        }

        public String getQualifier() {
            return qualifier;
        }

        public void setQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
