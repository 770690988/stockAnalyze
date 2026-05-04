# Java 服务资源限制指南

> 适用场景：服务器内存有限，多服务共存，需要限制 `java -jar` 进程的资源占用。

---

## 原始部署命令

```bash
nohup java -jar stockAnalyze-xxx.jar > stockAnalyze.out &
```

---

## 方案一：限制堆内存（最重要）

```bash
nohup java -Xms128m -Xmx256m -jar stockAnalyze-xxx.jar > stockAnalyze.out &
```

| 参数 | 说明 |
|------|------|
| `-Xms128m` | 初始堆内存 128MB |
| `-Xmx256m` | 最大堆内存 256MB（最关键的限制） |

---

## 方案二：全面限制内存（推荐）

```bash
nohup java \
  -Xms64m \
  -Xmx256m \
  -XX:MetaspaceSize=64m \
  -XX:MaxMetaspaceSize=128m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -jar stockAnalyze-xxx.jar > stockAnalyze.out &
```

| 参数 | 说明 |
|------|------|
| `-XX:MaxMetaspaceSize` | 限制类元数据区，防止无限增长 |
| `-XX:+UseG1GC` | G1 垃圾回收器，内存碎片少，更适合受限环境 |

---

## 方案三：配合 `taskset` 限制 CPU 核心

```bash
# 只使用 CPU 核心 0 和 1
taskset -c 0,1 java -Xmx256m -jar stockAnalyze-xxx.jar > stockAnalyze.out &
```

---

## 方案四：用 `cgroup`（通过 systemd）彻底隔离资源

创建文件 `/etc/systemd/system/stockanalyze.service`：

```ini
[Unit]
Description=Stock Analyze Service

[Service]
ExecStart=/usr/bin/java -Xmx256m -jar /path/to/stockAnalyze-xxx.jar
MemoryMax=300M
CPUQuota=50%
Restart=on-failure
StandardOutput=append:/path/to/stockAnalyze.out

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
systemctl daemon-reload
systemctl start stockanalyze
```

---

## 如何计算合适的 `-Xmx` 值

### 公式

```
可用内存 = 总内存 - 其他服务占用 - 系统保留（约 15%）
Xmx ≈ 可用内存 × 60%~70%
```

### 实际操作命令

```bash
# 查看当前内存使用
free -h

# 查看各进程占用
ps aux --sort=-%mem | head -20

# 查看现有 Java 进程实际使用（需要 JDK 工具）
jstat -gc <pid> 1000 5
```

### 举例

> 服务器 2GB 总内存，其他服务占 800MB，系统保留 300MB，剩余 900MB：
>
> `Xmx = 900MB × 65% ≈ 600MB` → 设置 `-Xmx512m`

---

## 快速推荐配置

| 服务器总内存 | 推荐配置 |
|---|---|
| 512MB | `-Xms32m -Xmx128m` |
| 1GB | `-Xms64m -Xmx256m` |
| 2GB | `-Xms128m -Xmx512m` |
| 4GB | `-Xms256m -Xmx1g` |

> **建议**：先运行 `free -h` 查看当前内存情况，再选择合适的参数。



/etc/systemd/system