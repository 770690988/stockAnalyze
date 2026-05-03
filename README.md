# stockAnalyze 股票分析
## 前端
npm 版本 20.20.2

```vue
cd stock_analyze_front
npm run dev
```


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