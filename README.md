# stockAnalyze 股票分析
## 前端

### 部署
npm 版本 20.20.2

```vue
cd stock_analyze_front
npm run dev
```

### 批量导入流程
```claude
ask1: A股中 (电池材料端)的上市公司帮我整理一下

ask2: 
很好 股票批量导入 JSON 转换规则： 将股票研究文本转换为批量导入 JSON，规则如下：

stockCode：股票代码
addReason：所属小标题/环节（去掉序号，如"上游资源-锂矿锂盐"）
remark：该股票的具体描述
sort：同一 addReason 的股票用同一个值，不同 addReason 依次递增，从 0 开始
不包含 stockName（系统自动获取） 输出格式为纯 JSON 数组，可直接用于批量导入接口。

ans: 
[
  {
    "stockCode": "301358",
    "addReason": "正极材料",
    "remark": "磷酸铁锂龙头，宁德时代、比亚迪核心供应商，2025年净利润同比增幅约94%-136%",
    "sort": 0
  }
  ···
]
```

将ans复制 调用页面的批量导入功能即可

## 后端
### 启动命令
```shell
nohup java -jar stockAnalyze-xxx.jar > stockAnalyze.out &
```

### 查看docker启动的命令配置
```/dockerfile
docker inspect {docker id} | grep "CreateCommand" -A 20
```

```text
"CreateCommand": [
                    "podman",
                    "run",
                    "-d",
                    "--name",
                    "mysql-server",
                    "--memory=512m",
                    "--memory-swap=768m",
                    "--restart=unless-stopped",
                    "-p",
                    "{port}:3306",
                    "-v",
                    "/biubiu/mysql/data:/var/lib/mysql",
                    "-v",
                    "/biubiu/mysql/conf/my.cnf:/etc/mysql/conf.d/my.cnf:ro",
                    "-e",
                    "MYSQL_ROOT_PASSWORD=xxxxx",
                    "localhost/mysql:latest"
               ]
```


优化后采用systemd的方式启动
```service
[Unit]
Description=Stock Analyze Backend
After=network.target

[Service]
Type=simple
ExecStartPre=/bin/bash -c 'until nc -z 127.0.0.1 {mysql的端口}; do echo "等待 MySQL..."; sleep 2; done'
ExecStart=/bin/bash -c 'JAR=$(ls /{yourjarpath}/stockAnalyze-*.jar | sort -V | tail -1) && \
  exec /{yourjavapath}}/java \
  -Xms64m \
  -Xmx384m \
  -XX:MetaspaceSize=32m \
  -XX:MaxMetaspaceSize=64m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/tmp/stockAnalyze-oom.hprof \
  -jar $JAR'
Restart=on-failure
RestartSec=10s
StandardOutput=append:/{yourlogfilepath}/stockAnalyze.out
StandardError=append:/{yourlogfilepath}/stockAnalyze.out

[Install]
WantedBy=multi-user.target
```

启动服务命令

p.s. 确保nc命令可以用
```shell
systemctl daemon-reload
systemctl enable stockanalyze
systemctl start stockanalyze
systemctl status stockanalyze
```

### mysql
#### 连接超限 处理方式
```mysql
-- 查看当前最大连接数限制
SHOW VARIABLES LIKE 'max_connections';

-- 查看当前实际连接数
SHOW STATUS LIKE 'Threads_connected';

-- 看看都是哪些连接占着
SHOW PROCESSLIST;
```