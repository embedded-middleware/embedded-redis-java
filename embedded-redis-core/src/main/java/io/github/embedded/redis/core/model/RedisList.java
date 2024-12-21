package io.github.embedded.redis.core.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RedisList {

    private final LinkedList<RedisVal> list;

    public RedisList() {
        this.list = new LinkedList<>();
    }

    public void push(byte[] value) {
        list.addFirst(new RedisVal(value));
    }

    public void rpush(byte[] value) {
        list.addLast(new RedisVal(value));
    }

    public int len() {
        return list.size();
    }

    public List<byte[]> range(int start, int stop) {
        int end;
        if (stop < 0) {
            end = list.size() + stop;
        } else {
            end = Math.min(stop + 1, list.size());
        }
        List<byte[]> range = new ArrayList<>();
        for (int i = start; i < end; i++) {
            range.add(list.get(i).getContent());
        }
        return range;
    }
}
