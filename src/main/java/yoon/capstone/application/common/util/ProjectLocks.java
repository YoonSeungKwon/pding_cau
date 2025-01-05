package yoon.capstone.application.common.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProjectLocks {

    public static final ConcurrentHashMap<String, Lock> cacheLock = new ConcurrentHashMap<>();

}
