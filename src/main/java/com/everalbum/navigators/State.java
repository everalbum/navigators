package com.everalbum.navigators;

import android.support.v4.util.SimpleArrayMap;

/**
 * Immutable map that defines the state of a {@link Navigator} after it has attached a child
 * view/coordinator. Helps facilitate data transfer and communication between coordinators.
 */
public final class State {
    private final SimpleArrayMap<String, Object> map;

    public State() {
        this(new SimpleArrayMap<String, Object>());
    }

    public State(State s) {
        this(new SimpleArrayMap<String, Object>(s.map));
    }

    State(SimpleArrayMap<String, Object> map) {
        this.map = map;
    }

    public String getString(String key) {
        return (String) map.get(key);
    }

    public int getInt(String key) {
        return (int) map.get(key);
    }

    public <T> T getObject(String key) {
        return (T) map.get(key);
    }

    public State putString(String key, String value) {
        State s = new State(this);
        s.map.put(key, value);
        return s;
    }

    public State putInt(String key, int value) {
        State s = new State(this);
        s.map.put(key, value);
        return s;
    }

    public State putObject(String key, Object value) {
        State s = new State(this);
        s.map.put(key, value);
        return s;
    }

    /**
     * Starts a transaction that allows multiple values to be inserted or removed from the map, without
     * having to create a new {@link State} with every change.
     * @return
     */
    public Transaction startTransaction() {
        return new Transaction(map);
    }

    public static class Transaction {
        private final SimpleArrayMap<String, Object> map;

        Transaction(SimpleArrayMap<String, Object> map) {
            this.map = new SimpleArrayMap<>(map);
        }

        public Transaction putString(String key, String value) {
            map.put(key, value);
            return this;
        }

        public Transaction putInt(String key, int value) {
            map.put(key, value);
            return this;
        }

        public Transaction putObject(String key, Object value) {
            map.put(key, value);
            return this;
        }

        public State commit() {
            return new State(map);
        }
    }
}
