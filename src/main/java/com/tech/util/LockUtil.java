package com.tech.util;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 单机按 key 互斥工具（同一 JVM 内有效，多实例部署需改用分布式锁）。
 * <p>
 * 内部使用固定 1024 个锁槽位，将 key 哈希到槽位上实现串行化。
 * 不同 key 可能映射到同一槽位导致短暂排队，但不影响正确性。
 * 如需严格 per-key 互斥，改用 Redisson 等分布式锁。
 * <pre>{@code
 * // 抢锁成功才执行，失败返回 false
 * if (!LockUtil.tryRun(userId, () -> updateBalance(userId, amount))) {
 *     throw new BizException(ErrorCode.xxx);
 * }
 *
 * // 抢锁失败直接抛 IllegalStateException
 * LockUtil.run(userId, () -> updateBalance(userId, amount));
 * }</pre>
 */
public final class LockUtil {

    private static final int SLOT_COUNT = 1024;
    private static final ReentrantLock[] LOCKS = new ReentrantLock[SLOT_COUNT];

    static {
        for (int i = 0; i < SLOT_COUNT; i++) {
            LOCKS[i] = new ReentrantLock();
        }
    }

    private LockUtil() {
    }

    private static ReentrantLock getLock(String key) {
        int idx = (key.hashCode() & Integer.MAX_VALUE) % SLOT_COUNT;
        return LOCKS[idx];
    }

    public static boolean tryLock(Long lockId) {
        Objects.requireNonNull(lockId, "lockId");
        return tryLock(String.valueOf(lockId));
    }

    /**
     * 尝试加锁，成功返回 {@code true}，失败返回 {@code false}。
     * 须与 {@link #unlock(String)} 成对调用，且在同一线程内释放。
     */
    public static boolean tryLock(String lockId) {
        Objects.requireNonNull(lockId, "lockId");
        return getLock(lockId).tryLock();
    }

    public static boolean tryLock(Long lockId, long timeout, TimeUnit unit) {
        Objects.requireNonNull(lockId, "lockId");
        return tryLock(String.valueOf(lockId), timeout, unit);
    }

    /**
     * 尝试加锁，在指定时间内等待，成功返回 {@code true}，超时返回 {@code false}。
     */
    public static boolean tryLock(String lockId, long timeout, TimeUnit unit) {
        Objects.requireNonNull(lockId, "lockId");
        Objects.requireNonNull(unit, "unit");
        try {
            return getLock(lockId).tryLock(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public static void unlock(Long lockId) {
        Objects.requireNonNull(lockId, "lockId");
        unlock(String.valueOf(lockId));
    }

    /**
     * 释放锁，须与 {@link #tryLock(String)} 成对、且在同一线程调用。
     */
    public static void unlock(String lockId) {
        Objects.requireNonNull(lockId, "lockId");
        getLock(lockId).unlock();
    }

    public static boolean tryRun(Long key, Runnable task) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(task, "task");
        return tryRun(String.valueOf(key), task);
    }

    /**
     * 尝试加锁并执行任务，成功返回 {@code true}，抢锁失败不执行任务并返回 {@code false}。
     */
    public static boolean tryRun(String key, Runnable task) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(task, "task");
        ReentrantLock lock = getLock(key);
        if (!lock.tryLock()) {
            return false;
        }
        try {
            task.run();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public static boolean tryRun(Long key, Runnable task, long timeout, TimeUnit unit) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(task, "task");
        return tryRun(String.valueOf(key), task, timeout, unit);
    }

    /**
     * 尝试加锁并执行任务，在指定时间内等待锁，成功返回 {@code true}，超时返回 {@code false}。
     */
    public static boolean tryRun(String key, Runnable task, long timeout, TimeUnit unit) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(task, "task");
        Objects.requireNonNull(unit, "unit");
        ReentrantLock lock = getLock(key);
        boolean acquired;
        try {
            acquired = lock.tryLock(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        if (!acquired) {
            return false;
        }
        try {
            task.run();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public static void run(Long key, Runnable task) {
        Objects.requireNonNull(key, "key");
        run(String.valueOf(key), task);
    }

    /**
     * 加锁并执行任务，抢锁失败抛出 {@link IllegalStateException}。
     */
    public static void run(String key, Runnable task) {
        if (!tryRun(key, task)) {
            throw new IllegalStateException("Failed to acquire lock: " + key);
        }
    }
}
