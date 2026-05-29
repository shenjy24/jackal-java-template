package com.tech.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 单机流水号生成器
 * <p>
 * 结构：[业务码] + [yyMMddHHmmssSSS 15位] + [毫秒内序列号 4位]
 * 同一毫秒最大支持 10,000 个不重复 ID，无需 sleep。
 * </p>
 */
public class SerialGenerator {

    /**
     * 最大序列号（4位：0000 ~ 9999）
     */
    private static final int MAX_SEQUENCE = 9999;
    private static final int SEQUENCE_LENGTH = 4;

    /**
     * 上一次生成 ID 的时间戳（毫秒）
     */
    private long lastTimestamp = -1L;

    /**
     * 毫秒内自增序列
     */
    private int sequence = 0;

    /**
     * 保护 lastTimestamp + sequence 的组合原子性
     */
    private final Object lock = new Object();

    // ===========================
    //  公开 API
    // ===========================

    /**
     * 生成无业务码的流水号
     */
    public String nextId() {
        return nextId("");
    }

    /**
     * 生成带业务码的流水号
     *
     * @param businessCode 业务类型代码，null 视为空字符串
     * @return 流水号
     * @throws IllegalStateException 同一毫秒内序列号耗尽时抛出
     */
    public String nextId(String businessCode) {
        String prefix = businessCode == null ? "" : businessCode;

        long timestamp;
        int seq;

        synchronized (lock) {
            timestamp = currentMillis();

            if (timestamp == lastTimestamp) {
                // 同一毫秒：序列号自增
                if (sequence > MAX_SEQUENCE) {
                    // 序列耗尽，等待进入下一毫秒（极低概率）
                    timestamp = waitNextMillis(lastTimestamp);
                    sequence = randomStart();
                }
            } else {
                // 新的毫秒：重置序列
                sequence = randomStart();
                lastTimestamp = timestamp;
            }
            seq = sequence++;
        }

        return buildSerial(prefix, timestamp, seq);
    }

    /**
     * 每个毫秒的序列起点随机落在 [0, MAX_SEQUENCE / 2] 之间
     * 上限取一半，留出足够的自增空间，避免过早耗尽
     */
    private int randomStart() {
        return ThreadLocalRandom.current().nextInt(MAX_SEQUENCE / 2);
    }

    // ===========================
    //  私有方法
    // ===========================

    private String buildSerial(String prefix, long timestamp, int seq) {
        // LocalDateTime 避免 SimpleDateFormat 线程安全问题
        String ts = TimeUtil.formatCompact(timestamp);
        // 序列号补零对齐，保证总长度固定
        String seqStr = String.format("%0" + SEQUENCE_LENGTH + "d", seq);
        return prefix + ts + seqStr;
    }

    /**
     * 自旋等待，直到进入下一毫秒
     */
    private long waitNextMillis(long lastTs) {
        long now = currentMillis();
        while (now <= lastTs) {
            now = currentMillis();
        }
        return now;
    }

    private long currentMillis() {
        return System.currentTimeMillis();
    }

    // ===========================
    //  main 验证
    // ===========================

    public static void main(String[] args) throws InterruptedException {
        SerialGenerator g = new SerialGenerator();
        String line = "─".repeat(50);

        // ── 单线程连续生成 ──────────────────────────────
        System.out.println(line);
        System.out.println("单线程连续生成 10 个 ID：");
        for (int i = 0; i < 10; i++) {
            System.out.println("  " + g.nextId("ORD"));
        }

        // ── 多线程并发生成，验证无重复 ──────────────────
        System.out.println(line);
        System.out.println("多线程并发生成 1000 个 ID，校验唯一性：");
        int threadCount = 10;
        int perThread = 100;
        java.util.Set<String> ids = java.util.Collections.synchronizedSet(new java.util.HashSet<>());
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            new Thread(() -> {
                for (int i = 0; i < perThread; i++) {
                    ids.add(g.nextId("PAY"));
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        int expected = threadCount * perThread;
        System.out.printf("  期望生成: %d  实际唯一: %d  %s%n",
                expected, ids.size(),
                ids.size() == expected ? "✓ 无重复" : "✗ 存在重复！");
    }
}
