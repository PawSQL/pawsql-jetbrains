# PawSQL Advisor 组件文档

## 核心组件详解

### 1. 配置系统 (Configuration System)

#### 1.1 PawSettingState
- **职责**: 配置持久化管理
- **关键属性**:
  ```java
  - dbType: 数据库类型
  - dbHost: 数据库主机
  - dbPort: 数据库端口
  - user: 用户名
  - passwd: 密码
  - defaultDB: 默认数据库
  - queryType: 查询类型
  - validate: 是否验证
  - analyze: 是否分析
  - rewrite: 是否重写
  ```
- **存储位置**: PawSQLSettingsPlugin.xml
- **访问方式**: 通过PersistentStateComponent接口

#### 1.2 PawSettingPage
- **职责**: 配置界面UI
- **主要功能**:
  - 数据库连接配置
  - 优化参数设置
  - 许可证管理
- **UI组件**:
  - 表单输入
  - 选项复选框
  - 保存按钮

### 2. 动作系统 (Action System)

#### 2.1 RunSelectedAction
- **职责**: 处理选中SQL的优化
- **主要流程**:
  1. 许可证验证
  2. SQL文本获取
  3. 优化分析
  4. 结果展示
- **错误处理**:
  - 许可证验证失败
  - SQL语法错误
  - 数据库连接错误

#### 2.2 RunAction
- **职责**: 常规SQL优化
- **特点**:
  - 支持批量处理
  - 进度显示
  - 异步执行

### 3. 工具窗口 (Tool Window)

#### 3.1 PawWindowFactory
- **职责**: 创建和管理工具窗口
- **位置**: 底部工具栏
- **功能**:
  - 显示优化结果
  - 提供操作按钮
  - 支持结果导出

### 4. SQL优化引擎 (SQL Optimization Engine)

#### 4.1 优化器组件
- **索引分析**:
  ```java
  - 冗余索引检测
  - 缺失索引建议
  - 索引使用分析
  ```
- **查询重写**:
  ```java
  - 语法优化
  - 性能优化
  - 等价变换
  ```

#### 4.2 数据库适配器
- **支持的数据库**:
  - MySQL适配器
  - PostgreSQL适配器
  - Oracle适配器
  - MariaDB适配器

### 5. 许可证系统 (License System)

#### 5.1 CheckLicense
- **职责**: 许可证验证
- **功能**:
  - 许可证状态检查
  - 在线激活
  - 试用期管理

### 6. 工具类 (Utilities)

#### 6.1 数据库工具
- **连接管理**:
  ```java
  - 连接池维护
  - 连接状态监控
  - 自动重连
  ```
- **查询执行**:
  ```java
  - 预处理语句
  - 结果集处理
  - 错误处理
  ```

## UI组件

### 1. WorkspaceDialog
工作空间管理对话框，用于创建、选择和管理工作空间。

#### 主要功能
- 显示工作空间列表
- 创建新工作空间
- 编辑工作空间信息
- 选择当前工作空间

#### 关键接口
```java
public class WorkspaceDialog extends DialogWrapper {
    // 构造函数
    public WorkspaceDialog(Project project);
    
    // 获取选中的工作空间
    public WorkspaceInfo getSelectedWorkspace();
}
```

### 2. OptimizationResultDialog
优化结果展示对话框，用于显示SQL优化结果和历史记录。

#### 主要功能
- 显示优化历史记录
- 展示优化结果详情
- 支持Markdown格式显示
- 提供结果对比功能

#### 关键接口
```java
public class OptimizationResultDialog extends DialogWrapper {
    // 构造函数
    public OptimizationResultDialog(Project project, String sqlFilePath, 
                                  OptimizeResultHandler resultHandler);
}
```

### 3. ConfigDialog
配置管理对话框，用于管理插件配置。

#### 主要功能
- 服务器配置
- 认证信息管理
- 优化参数设置
- 报告语言设置

#### 关键接口
```java
public class ConfigDialog extends DialogWrapper {
    // 构造函数
    public ConfigDialog(Project project);
}
```

## 服务组件

### 1. OptimizationService
SQL优化核心服务，负责处理SQL优化请求。

#### 主要功能
- SQL语句优化
- 执行计划分析
- 索引建议生成
- 优化结果处理

#### 关键接口
```java
public class OptimizationService {
    // 优化SQL
    public void optimizeSQL(String filePath, String sql, WorkspaceInfo workspace);
    
    // 获取工作空间列表
    public List<WorkspaceInfo> getWorkspaces();
    
    // 创建工作空间
    public WorkspaceInfo createWorkspace(String name, String description);
    
    // 更新工作空间
    public WorkspaceInfo updateWorkspace(WorkspaceInfo workspace);
    
    // 删除工作空间
    public void deleteWorkspace(String workspaceId);
}
```

### 2. OptimizeResultHandler
优化结果处理服务，负责管理和展示优化结果。

#### 主要功能
- 保存优化结果
- 加载历史记录
- 生成结果报告
- 清理历史记录

#### 关键接口
```java
public class OptimizeResultHandler {
    // 保存优化结果
    public void saveOptimizationResult(String filePath, String result);
    
    // 加载优化结果
    public String loadOptimizationResult(String filePath);
    
    // 列出优化结果
    public List<OptimizationResultInfo> listOptimizationResults(String filePath);
    
    // 删除优化结果
    public void deleteOptimizationResult(String filePath);
    
    // 清理历史记录
    public void cleanupOldResults(int maxResults);
}
```

### 3. WorkspaceService
工作空间管理服务，负责工作空间的CRUD操作。

#### 主要功能
- 工作空间管理
- 缓存管理
- 权限控制
- 同步更新

#### 关键接口
```java
public class WorkspaceService {
    // 获取工作空间
    public List<WorkspaceInfo> getWorkspaces();
    
    // 创建工作空间
    public WorkspaceInfo createWorkspace(String name, String description);
    
    // 更新工作空间
    public WorkspaceInfo updateWorkspace(WorkspaceInfo workspace);
    
    // 删除工作空间
    public void deleteWorkspace(String workspaceId);
    
    // 刷新工作空间列表
    public void refreshWorkspaces();
}
```

## 数据访问组件

### 1. PawFileManager
文件管理器，负责处理本地文件操作。

#### 主要功能
- 创建目录
- 保存文件
- 读取文件
- 删除文件
- 清理过期文件

#### 关键接口
```java
public class PawFileManager {
    // 创建目录
    public void createDirectories(Path path);
    
    // 保存文件
    public void saveFile(Path path, String content);
    
    // 读取文件
    public String readFile(Path path);
    
    // 删除文件
    public void deleteFile(Path path);
    
    // 清理目录
    public void cleanupDirectory(Path directory, int maxFiles);
}
```

### 2. PawHttpClient
HTTP客户端，负责与PawSQL API通信。

#### 主要功能
- 发送HTTP请求
- 处理API响应
- 错误处理
- 重试机制

#### 关键接口
```java
public class PawHttpClient {
    // 发送GET请求
    public <T> T get(String path, Class<T> responseType);
    
    // 发送POST请求
    public <T> T post(String path, Object request, Class<T> responseType);
    
    // 发送PUT请求
    public <T> T put(String path, Object request, Class<T> responseType);
    
    // 发送DELETE请求
    public void delete(String path);
}
```

### 3. ConfigLoader
配置加载器，负责管理插件配置。

#### 主要功能
- 加载配置
- 保存配置
- 配置验证
- 默认值处理

#### 关键接口
```java
public class ConfigLoader {
    // 加载配置
    public PawSqlConfig loadConfig();
    
    // 保存配置
    public void saveConfig(PawSqlConfig config);
    
    // 重置配置
    public void resetConfig();
    
    // 验证配置
    public boolean validateConfig(PawSqlConfig config);
}
```

## 事件处理组件

### 1. Action类
处理用户交互事件的动作类。

#### OptimizeSqlAction
处理SQL优化请求。

```java
public class OptimizeSqlAction extends AnAction {
    // 执行动作
    public void actionPerformed(AnActionEvent e);
    
    // 更新动作状态
    public void update(AnActionEvent e);
}
```

#### ViewHistoryAction
处理查看历史记录请求。

```java
public class ViewHistoryAction extends AnAction {
    // 执行动作
    public void actionPerformed(AnActionEvent e);
    
    // 更新动作状态
    public void update(AnActionEvent e);
}
```

#### ConfigAction
处理配置管理请求。

```java
public class ConfigAction extends AnAction {
    // 执行动作
    public void actionPerformed(AnActionEvent e);
    
    // 更新动作状态
    public void update(AnActionEvent e);
}
```

#### WorkspaceAction
处理工作空间管理请求。

```java
public class WorkspaceAction extends AnAction {
    // 执行动作
    public void actionPerformed(AnActionEvent e);
    
    // 更新动作状态
    public void update(AnActionEvent e);
}
```

## 组件交互

### 1. 配置流程
```mermaid
graph LR
    A[用户输入] --> B[PawSettingPage]
    B --> C[验证输入]
    C --> D[PawSettingState]
    D --> E[配置文件]
```

### 2. 优化流程
```mermaid
graph LR
    A[用户选择SQL] --> B[RunSelectedAction]
    B --> C[许可证验证]
    C --> D[SQL优化引擎]
    D --> E[结果展示]
```

## 扩展点

### 1. 数据库支持
- 添加新数据库适配器
- 实现特定优化策略
- 配置连接参数

### 2. 优化策略
- 自定义优化规则
- 新增分析维度
- 优化建议模板

### 3. UI定制
- 自定义结果展示
- 添加新的工具窗口
- 扩展设置页面
