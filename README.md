# Creative Script Studio (AI小说转剧本编辑器)🎬

Creative Script Studio 是一款基于 AI（大语言模型）构建的智能小说转剧本编辑器。它旨在帮助小说作家、编剧、短视频创作者及动漫导演快速将传统叙述性小说文本转换为各种专业格式的剧本。

本项目包含完整的**前端 React 交互界面**和**后端 Spring Boot 服务**，支持将处理结果持久化存储到本地数据库中，以便后续追溯和打磨。

---

## 🚀 核心功能亮点

*   **📖 多章节文本管理**：支持录入与管理多个连续章节（推荐 3 章及以上），方便 AI 进行跨章节的故事脉络梳理及场景承接。
*   **🎨 五大剧本转换风格格式**：
    1.  **电影标准剧本 (Standard Movie Script)**：标准的电影、电视剧场景格式，包含场景标题（Slugline）、场景描述（Action）、角色名、神态动作括号说明（Parenthetical）和台词（Dialogue）。
    2.  **短视频/抖音脚本 (Short Video Script)**：采用专业的 Markdown 表格输出，包含镜号、景别与画面描述、声音与台词、音效与 BGM、估算时长，节奏紧凑。
    3.  **动漫/有声剧脚本 (Anime & Audio Drama)**：突出二次元画面表现力、动作张力、音效标记（SFX）和声优（CV）情绪配音指示。
    4.  **导演分镜脚本 (Director's Storyboard)**：提供极强的视觉画面感和镜头运动设计（固定、推、拉、摇、移），包含光影灯光色彩说明。
    5.  **结构化 YAML 剧本 (Structured YAML Script)**：**核心首创推荐格式**。将剧本转换为高规范度的结构化 YAML 格式，自动区分章节、场景、地点、出场角色、事件流（动作与台词），支持程序化读取和二次开发解析。
*   **⚙️ 开发者自定义配置**：前端提供了直观的“开发者配置”面板，支持动态输入并覆盖 LLM 的 API URL Base、API Key 及 Model 名称，无缝对接 OpenAI、DeepSeek、SiliconFlow 等任意兼容 OpenAI 接口标准的平台。
*   **📜 历史版本持久化管理**：后端使用 H2 嵌入式数据库对生成的剧本历史进行持久化存储。支持随时在前端一键查看、载入特定历史版本、删除无用记录或清空历史。
*   **🛠️ 实时校验与导出工具**：
    *   **YAML 格式状态检测**：前端对生成的 YAML 剧本进行实时格式校验，确保结构正确。
    *   **一键复制**：快速将生成的剧本内容复制到剪贴板。
    *   **一键下载**：根据选择的生成风格，自动识别文件格式并导出为本地文件（`.yaml` 或 `.md`）。

---

## 🛠️ 技术栈架构

本项目采用前后端分离的现代化架构搭建：

### 前端 (Frontend)
*   **框架**：React 18
*   **构建工具**：Vite
*   **样式/设计**：极简高级的现代暗黑风 Vanilla CSS 样式设计，支持 Toast 吐司提示、微动画效果和响应式布局。

### 后端 (Backend)
*   **框架**：Spring Boot 4.0.6
*   **数据库**：H2 Database（持久化文件模式，自动在项目根目录生成 `./db/noveldb`）
*   **ORM**：Spring Data JPA (Hibernate)
*   **工具**：Java HttpClient (原生异步请求)、Lombok、Jackson

---

## 📁 项目目录结构

```text
AI-Novel-to-Screenplay/
├── backend/                  # 后端 Spring Boot 工程
│   ├── src/main/java/        # 后端核心源码
│   │   └── com/complier/backend/
│   │       ├── controller/   # REST 控制层 (ScreenplayController)
│   │       ├── entity/       # JPA 实体类 (PromptTemplate, ScreenplayHistory)
│   │       ├── dto/          # 数据传输对象 (ConvertRequest, ConvertResponse)
│   │       ├── repository/   # 数据库交互接口 (ScreenplayHistoryRepository)
│   │       ├── registry/     # 剧本转换提示词注册表 (PromptRegistry)
│   │       └── service/      # AI 请求业务层 (AiService)
│   └── src/main/resources/
│       └── application.properties # 后端配置文件（配置 H2 数据库、端口、默认 API 密钥与模型）
│
└── frontend/                 # 前端 React 工程
    ├── src/
    │   ├── App.jsx           # 前端核心业务与 UI 组件
    │   ├── App.css           # UI 样式文件（暗黑拟物风格设计与动画）
    │   └── main.jsx          # 程序入口
    ├── index.html
    └── package.json          # 前端依赖配置
```

---

## 📥 快速开始与运行指南

### 1. 后端配置与启动

1.  用 IDE（如 IntelliJ IDEA）打开 `backend` 目录，或者在终端中进入 `backend` 目录。
2.  若要配置默认的大模型，可以在 `backend/src/main/resources/application.properties` 中填入你的 API Key（或留空在前端运行时手动输入）：
    ```properties
    # 默认 API 配置 (支持 OpenAI/DeepSeek 等 OpenAI 兼容格式)
    ai.api.url=https://api.deepseek.com
    ai.api.key=your-api-key-here
    ai.model=deepseek-chat
    ```
3.  运行启动命令：
    ```bash
    # 使用 Maven 启动后端
    ./mvnw spring-boot:run
    ```
4.  后端服务默认在 `http://localhost:8080` 端口运行。
5.  **数据库可视化调试**：后端启动后，可浏览器访问 `http://localhost:8080/h2-console` 查看 H2 数据库。
    *   JDBC URL: `jdbc:h2:file:./db/noveldb`
    *   用户名: `sa`，密码留空，点击 Connect 即可登录。

### 2. 前端配置与启动

1.  在终端中进入 `frontend` 目录。
2.  安装依赖项：
    ```bash
    npm install
    ```
3.  启动开发服务器：
    ```bash
    npm run dev
    ```
4.  根据控制台输出的地址（通常为 `http://localhost:5173`）在浏览器中打开。

---

## 🔗 后端 API 接口设计

所有的 API 前缀均为 `/api/screenplay`：

| 请求方式 | 路径 | 描述 | 请求体/参数 |
| :--- | :--- | :--- | :--- |
| **GET** | `/prompts` | 获取系统预设的 5 种剧本转换风格模板列表 | 无 |
| **POST** | `/convert` | 触发小说到剧本的转换。若请求体携带了自定义配置，则会覆盖系统默认配置 | `ConvertRequest` JSON 格式 |
| **GET** | `/history` | 获取所有转换过的历史剧本记录（按时间降序排列） | 无 |
| **GET** | `/history/{id}` | 获取特定 ID 的剧本历史详细内容 | 路径参数：`id` |
| **DELETE**| `/history/{id}` | 删除特定 ID 的历史剧本记录 | 路径参数：`id` |
| **DELETE**| `/history/clear` | 清空所有历史剧本记录 | 无 |

---

## 💡 使用小贴士

*   **输入优化**：如果想要使用 **YAML 格式**，为了获得最佳结构化效果，输入的小说文本请保持章节完整性，并使用“第 X 章”或“【第一章】”明确标示分隔。
*   **模型切换**：可以在前端的“⚙️ 开发者配置”中动态切换为不同的模型。例如，如果你想使用 Gemini 3.5 或者 DeepSeek 或者是 SiliconFlow 的大模型，直接填入对应的 Base URL 接口地址和 API 密钥即可。
