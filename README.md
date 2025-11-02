# Yunhang Forum

## 项目简介
云航论坛（Yunhang Forum）是一个为北航学生提供信息交流的平台。该平台旨在为同学们提供一个方便的在线论坛，用于分享学习资料、讨论学术问题以及组织校园活动。项目基于Java开发，采用面向对象设计原则，结合现代软件开发技术。

## 技术栈 (Tech Stack)
- **语言**：Java
- **框架**：Spring Boot
- **数据库**：MySQL
- **前端**：HTML, CSS, JavaScript
- **构建工具**：Maven / Gradle
- **开发工具**：IntelliJ IDEA
- **版本控制**：GitHub

## 项目结构说明 (Folder Structure)
- `src/`：包含项目源代码和资源文件。
  - `main/java/com/yunhang/forum/`：Java 源代码文件，按照功能模块划分。
  - `main/resources/`：配置文件，如 `application.properties` 等。
  - `test/`：单元测试文件。
- `target/` 和 `build/`：构建输出目录。
- `.gitignore`：用于忽略本地的 IntelliJ IDEA 配置文件、编译产物等。
- `LICENSE`：MIT License 文件。
- `README.md`：项目的自述文件，包含项目介绍和使用说明。

## 本地运行指南 (Getting Started)

### 环境要求：
- Java 版本：JDK 11 或更高
- Maven / Gradle（根据选择的构建工具）

### 配置步骤：
1. 克隆项目：
   ```bash
   git clone https://github.com/yourusername/Yunhang-Forum.git

## 关于提交

### Git 分支模型：
- master：生产分支，永远保持可发布的状态。
- develop：开发分支，所有功能开发都在此分支进行。
- feature/：每个新功能开发都在一个单独的 feature/ 分支上进行，命名格式：feature/功能名。
- bugfix/：bug 修复分支，命名格式：bugfix/bug描述。
- hotfix/：紧急修复分支，直接从 master 分支创建，修复完成后合并回 master 和 develop。

### 提交流程：
1. 创建分支：从 develop 分支创建新的功能分支
```
git checkout develop
git checkout -b feature/your-feature-name
```

2. 提交代码：开发完成后，提交代码到对应分支。
```
git add .
git commit -m "描述你的更改"
git push origin feature/your-feature-name
```

3. 拉取请求：完成功能开发后，向 develop 分支发起拉取请求（PR）。

4. 代码审查：团队成员进行代码审查，提出修改建议。

5. 合并：审查通过后，将分支合并到 develop。

### Commit 消息规范：
- 功能开发：feat: 简要描述功能
- 修复问题：fix: 简要描述修复
- 优化代码：refactor: 简要描述优化
- 文档更新：docs: 简要描述更新
- 其他：chore: 简要描述
