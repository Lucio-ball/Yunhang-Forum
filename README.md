# ☁️ Yunhang Forum (云航论坛)

![Java](https://img.shields.io/badge/Language-Java_25+-orange?style=flat-square)
![UI Framework](https://img.shields.io/badge/UI_Framework-JavaFX-blue?style=flat-square)
![Build](https://img.shields.io/badge/Build-Maven-green?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-lightgrey?style=flat-square)

> **北航学生专属的桌面端信息交流平台**
>
> breaking information barriers, connecting every BUAer.

## 📖 项目简介 (Introduction)
**云航论坛** 是一个基于 Java 面向对象思想设计的桌面端应用程序。它旨在打破校园信息差，为同学们提供一个纯净、高效的交流空间。
本项目是《面向对象程序设计》课程大作业，核心逻辑完全采用 Java 原生实现，体现了封装、继承、多态及多种设计模式的应用。

## 🛠 技术栈 (Tech Stack)
*   **开发语言**：Java 25 (LTS)
*   **GUI 框架**：JavaFX (推荐) / Swing
*   **数据存储**：JSON / CSV (本地文件存储) / SQLite (可选)
*   **构建工具**：Maven
*   **开发工具**：IntelliJ IDEA
*   **版本控制**：Git & GitHub

## 📂 项目结构 (Folder Structure)
```text
Yunhang-Forum/
├── src/
│   ├── main/
│   │   ├── java/com/yunhang/forum/
│   │   │   ├── model/          # 实体类 (User, Post...)
│   │   │   ├── view/           # UI 界面代码 (FXML/Swing Panels)
│   │   │   ├── controller/     # 业务逻辑控制
│   │   │   └── util/           # 工具类 (FileIO, DateUtil)
│   │   └── resources/          # 配置文件、图片素材、FXML布局文件
│   └── test/                   # 单元测试 (JUnit 5)
├── data/                       # 本地存储的数据文件 (JSON/CSV)
├── docs/                       # 开发文档、类图、需求说明书
├── pom.xml                     # Maven 依赖配置
├── .gitignore                  # Git 忽略规则
└── README.md                   # 项目说明书
```

## 🚀 本地运行指南 (Getting Started)

### 环境要求

*   **JDK**: 17 或更高版本
*   **IDE**: IntelliJ IDEA (推荐 Community 或 Ultimate)

### 快速启动

1. **克隆项目**

   ```bash
   git clone https://github.com/yourusername/Yunhang-Forum.git
   ```

2. **导入 IDEA**

   *   打开 IDEA -> `Open` -> 选择项目根目录。
   *   等待 Maven 自动下载依赖（右下角进度条）。

3. **运行**

   *   找到 `src/main/java/com/yunhang/forum/MainApp.java`。
   *   点击绿色播放键 `Run`。

## 🤝 协作工作流 (Workflow)

我们采用简化的 **GitHub Flow** 模式。

### 1. 分支策略

*   **`main`**: 🛡️ **受保护的主分支**。永远保持可编译、可运行的状态。禁止直接 Push。
*   **`feature/xxx`**: ✨ **功能分支**。所有开发都在此分支进行。
    *   命名示例: `feature/login-ui`, `feature/post-logic`

### 2. 开发流程

1.  **拉取最新代码**: `git checkout main` -> `git pull`
2.  **创建分支**: `git checkout -b feature/你的功能名`
3.  **提交代码**: `git commit -m "type: 你的描述"`
4.  **推送分支**: `git push origin feature/你的功能名`
5.  **发起 PR**: 在 GitHub 页面创建 Pull Request，请求合并入 `main`。
6.  **Code Review**: 等待至少一名队友 Review 并批准。
7.  **合并**: 此时才可 Merge 进主分支。

### 3. Commit 消息规范

请严格遵守 `<type>: <subject>` 格式：

*   `feat`: ✨ 新增功能 (feature)
*   `fix`: 🐛 修复 Bug
*   `docs`: 📚 文档变更
*   `style`: 💎 代码格式调整 (不影响逻辑)
*   `refactor`: ♻️ 代码重构
*   `test`: ✅ 测试用例
