package ca.eastarcti;

import java.util.HashMap;
import java.util.Map;

public class SummaryCounter {

    private Map<String, Counter> counters = new HashMap<>();

    public SummaryCounter() {}

    public void addCounter(String id, String fmt, String description) {
        counters.put(id, new Counter(id, fmt, description));
    }

    public void addCounter(String id, String fmt) {
        counters.put(id, new Counter(id, fmt, ""));
    }

    public void incrementCounter(String id) {
        if (!counters.containsKey(id)) {
            throw new IllegalArgumentException(
                "Counter with id " + id + " does not exist"
            );
        }
        counters.get(id).increment();
    }

    public void incrementCounter(String id, int count) {
        if (!counters.containsKey(id)) {
            throw new IllegalArgumentException(
                "Counter with id " + id + " does not exist"
            );
        }
        counters.get(id).increment(count);
    }

    public void printSummary() {
        System.out.println("Summary:");
        for (Counter counter : counters.values()) {
            System.out.println("\t" + counter);
        }
    }

    private class Counter {

        private int value;
        private String name;
        private String fmt;
        private String description;

        public Counter(String name, String fmt, String description) {
            this.name = name;
            this.fmt = fmt;
            this.description = description;
        }

        public void increment() {
            value++;
        }

        public void increment(int count) {
            value += count;
        }

        public String toString() {
            return String.format(fmt, name, value, description);
        }
    }
}
