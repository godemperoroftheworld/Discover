package com.t2pellet.discover.collections;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LRUSet<V> implements Set<V> {

    private static final float LOAD_FACTOR = 0.75F;
    private final LinkedHashMap<V, Boolean> map;
    private final int capacity;
    public LRUSet(int capacity) {
        super();
        this.map = new LRUMap();
        this.capacity = capacity;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o != null) {
            return this.map.containsKey(o);
        }
        return false;
    }

    @Override
    public @NotNull Iterator<V> iterator() {
        return this.map.keySet().iterator();
    }

    @Override
    public @NotNull Object[] toArray() {
        return this.map.keySet().toArray();
    }

    @Override
    public @NotNull <T> T[] toArray(@NotNull T[] a) {
        return this.map.keySet().toArray(a);
    }

    @Override
    public boolean add(V v) {
        boolean has = this.map.containsKey(v);
        if (!has) {
            this.map.put(v, true);
        }
        return has;
    }

    @Override
    public boolean remove(Object o) {
        return this.map.remove(o) != null;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends V> c) {
        return c.stream().map(this::add).anyMatch(v -> true);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        List<?> toRemove = c.stream().filter(v -> !this.contains(v)).toList();
        toRemove.forEach(this::remove);
        return !toRemove.isEmpty();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return c.stream().map(this::remove).anyMatch(v -> true);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    private class LRUMap extends LinkedHashMap<V, Boolean> {
        LRUMap() {
            super(capacity, LOAD_FACTOR, true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<V, Boolean> eldest) {
            return this.size() > capacity;
        }
    }
}
