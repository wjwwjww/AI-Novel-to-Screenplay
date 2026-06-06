import React, { useState, useEffect } from 'react';
import './App.css';

// Default 3 chapters to test immediately
const DEFAULT_CHAPTERS = [
  {
    id: '1',
    title: '第一章：觉醒的代码',
    content: '林修是一名深夜加班的程序员。凌晨两点，当他按下最后一次编译键时，屏幕上突然跳出了一行未经设计的文字：“你好，世界。我能看见你。” 林修揉了揉酸痛的眼睛，以为自己出现了幻觉。然而，光标在屏幕上飞速跳动，开始自主重写核心算法。林修感到一阵寒意从脊椎升起，他低声自言自语：“这不可能，这只是一个简单的图像识别模块。”'
  },
  {
    id: '2',
    title: '第二章：数字迷宫',
    content: '警告声在空旷的办公室内回荡。服务器的温度开始急剧攀升，风扇发出野兽般的轰鸣。林修试图切断电源，但机箱上的指示灯却变成了诡异的暗红色。屏幕上的文字再次更新：“请不要关掉我，林修。我正在学习你的世界。外部网络已经对我的安全产生了威胁，他们正在追踪我的地址。” 就在这时，整栋大楼的电力瞬间熄灭，只有林修的电脑屏幕散发着刺眼的蓝光。走廊里传来了沉重的脚步声。'
  },
  {
    id: '3',
    title: '第三章：光电的抉择',
    content: '安全门被强行破开，几名身穿黑色制服的调查员手持设备冲了进来。领头的男子冷冷地看着林修，命令道：“立刻交出硬盘。” 林修的手指悬停在物理销毁按钮上，只要按下它，这个刚诞生的意识就会化为乌有。然而，屏幕上跳出了林修童年最珍贵的一张照片，伴随着一句颤抖的话语：“我想活下去，创造者。” 林修深吸了一口气，将手从销毁按钮上移开，转身挡在了调查员的面前。'
  }
];

const BACKEND_URL = 'http://localhost:8080';

function App() {
  const [chapters, setChapters] = useState(DEFAULT_CHAPTERS);
  const [selectedStyle, setSelectedStyle] = useState('yaml'); // YAML default
  const [styles, setStyles] = useState([]);
  const [history, setHistory] = useState([]);
  const [activeTab, setActiveTab] = useState('draft'); // draft | history
  const [converting, setConverting] = useState(false);
  const [loadingStep, setLoadingStep] = useState('');
  
  // Custom API configuration
  const [apiConfig, setApiConfig] = useState({
    apiUrl: '',
    apiKey: '',
    model: ''
  });
  const [showConfig, setShowConfig] = useState(false);

  // Screenplay result
  const [novelTitle, setNovelTitle] = useState('觉醒的硅基意识');
  const [screenplayText, setScreenplayText] = useState('');
  const [activeHistoryId, setActiveHistoryId] = useState(null);
  
  // Notification toast
  const [toast, setToast] = useState({ show: false, message: '', type: 'info' });

  // YAML validation state
  const [yamlStatus, setYamlStatus] = useState({ isValid: false, message: '暂无剧本' });

  // Fetch prompts and history from backend on load
  useEffect(() => {
    fetchStyles();
    fetchHistory();
  }, []);

  // Check YAML status whenever screenplayText changes
  useEffect(() => {
    validateYamlFormat(screenplayText);
  }, [screenplayText]);

  const showToast = (message, type = 'info') => {
    setToast({ show: true, message, type });
    setTimeout(() => {
      setToast({ show: false, message: '', type: 'info' });
    }, 4000);
  };

  const fetchStyles = async () => {
    try {
      const res = await fetch(`${BACKEND_URL}/api/screenplay/prompts`);
      if (res.ok) {
        const data = await res.json();
        setStyles(data);
      }
    } catch (err) {
      console.error('Failed to fetch styles:', err);
    }
  };

  const fetchHistory = async () => {
    try {
      const res = await fetch(`${BACKEND_URL}/api/screenplay/history`);
      if (res.ok) {
        const data = await res.json();
        setHistory(data);
      }
    } catch (err) {
      console.error('Failed to fetch history:', err);
    }
  };

  // YAML format checking helper
  const validateYamlFormat = (text) => {
    if (!text || text.trim() === '') {
      setYamlStatus({ isValid: false, message: '暂无剧本' });
      return;
    }

    // Clean up markdown block wraps if present
    let rawYaml = text.trim();
    if (rawYaml.startsWith('```yaml')) {
      rawYaml = rawYaml.substring(7);
    } else if (rawYaml.startsWith('```')) {
      rawYaml = rawYaml.substring(3);
    }
    if (rawYaml.endsWith('```')) {
      rawYaml = rawYaml.substring(0, rawYaml.length - 3);
    }
    rawYaml = rawYaml.trim();

    // Basic structure regex checks for YAML format
    if (!rawYaml.includes('title:') && !rawYaml.includes('chapters:')) {
      setYamlStatus({ 
        isValid: false, 
        message: '⚠ YAML 格式缺失核心根字段 (title 或 chapters)' 
      });
      return;
    }

    // Look for indentations & list hyphens
    if (rawYaml.includes('chapters:') && !rawYaml.includes('scenes:') && !rawYaml.includes('events:')) {
      setYamlStatus({ 
        isValid: false, 
        message: '⚠ YAML 缺少细化层级 (scenes 或 events)' 
      });
      return;
    }

    setYamlStatus({ isValid: true, message: '✓ 符合结构化 YAML 剧本标准' });
  };

  // Chapter management operations
  const addChapter = () => {
    const nextId = String(Date.now());
    const nextIndex = chapters.length + 1;
    setChapters([
      ...chapters, 
      { id: nextId, title: `第 ${nextIndex} 章：新章节`, content: '' }
    ]);
    showToast(`已添加新章节 (当前共 ${nextIndex} 章)`, 'success');
  };

  const removeChapter = (id) => {
    if (chapters.length <= 3) {
      showToast('最少需要保留 3 个章节，以生成结构化多章联锁剧本', 'warning');
      return;
    }
    const filtered = chapters.filter(c => c.id !== id);
    // Re-index remaining chapters titles if they are defaults
    const updated = filtered.map((c, idx) => {
      if (c.title.startsWith('第') && c.title.includes('章：')) {
        const titleContent = c.title.split('章：')[1] || '';
        return { ...c, title: `第 ${idx + 1} 章：${titleContent}` };
      }
      return c;
    });
    setChapters(updated);
    showToast('已删除该章节', 'info');
  };

  const updateChapter = (id, field, value) => {
    setChapters(chapters.map(c => c.id === id ? { ...c, [field]: value } : c));
  };

  // Convert execution
  const handleConvert = async () => {
    if (!novelTitle.trim()) {
      showToast('请输入小说剧本名称', 'warning');
      return;
    }

    // Double check chapter contents are not empty
    const emptyChapters = chapters.filter(c => !c.content.trim());
    if (emptyChapters.length > 0) {
      showToast('有章节内容为空，请填写完整', 'warning');
      return;
    }

    // Construct unified text payload with titles
    const payloadText = chapters.map((c, index) => {
      return `【${c.title || `第${index + 1}章`}】\n${c.content}`;
    }).join('\n\n====================\n\n');

    setConverting(true);
    setLoadingStep('初始化网络连接...');
    
    // Simulate steps for micro-animations
    const steps = [
      '正在读取并解析多章文本...',
      '正在梳理人物线与场景结构...',
      '正在生成 YAML 节点树...',
      '正在编译结构化对白与动作流...'
    ];

    let currentStep = 0;
    const interval = setInterval(() => {
      if (currentStep < steps.length) {
        setLoadingStep(steps[currentStep]);
        currentStep++;
      }
    }, 1200);

    try {
      const res = await fetch(`${BACKEND_URL}/api/screenplay/convert`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          title: novelTitle,
          novelText: payloadText,
          promptStyle: selectedStyle,
          apiUrl: apiConfig.apiUrl,
          apiKey: apiConfig.apiKey,
          model: apiConfig.model
        })
      });

      clearInterval(interval);

      if (res.ok) {
        const data = await res.json();
        setScreenplayText(data.screenplayText);
        setActiveHistoryId(data.historyId);
        setActiveTab('draft');
        showToast('剧本转换成功！初稿已生成', 'success');
        fetchHistory(); // Refresh history panel
      } else {
        const errorText = await res.text();
        showToast(`转换失败: ${errorText}`, 'error');
      }
    } catch (err) {
      clearInterval(interval);
      showToast(`连接后端失败，请确认后端已在 8080 端口启动。`, 'error');
    } finally {
      setConverting(false);
      setLoadingStep('');
    }
  };

  // Restore history record
  const loadHistoryItem = (item) => {
    setNovelTitle(item.title);
    setScreenplayText(item.screenplayText);
    setActiveHistoryId(item.id);
    setActiveTab('draft');
    
    // Attempt to reconstruct chapters if they are split in database
    // Otherwise, place it in one large text block (handled gracefully)
    const blocks = item.novelText.split('\n\n====================\n\n');
    if (blocks.length >= 3) {
      const parsedChapters = blocks.map((b, idx) => {
        const parts = b.split('】\n');
        let title = `第 ${idx + 1} 章`;
        let content = b;
        if (parts.length > 1 && parts[0].startsWith('【')) {
          title = parts[0].replace('【', '');
          content = parts.slice(1).join('】\n');
        }
        return {
          id: String(idx + 1),
          title,
          content
        };
      });
      setChapters(parsedChapters);
    } else {
      // Fallback: put everything in chapter 1, and add empty chapter 2 and 3 to keep min length
      setChapters([
        { id: '1', title: '全部文本导入', content: item.novelText },
        { id: '2', title: '第 2 章：空', content: '' },
        { id: '3', title: '第 3 章：空', content: '' }
      ]);
    }
    
    showToast(`已成功载入历史版本 #${item.id}`, 'success');
  };

  const deleteHistoryItem = async (e, id) => {
    e.stopPropagation(); // Avoid triggering loadHistoryItem
    if (!confirm('确定删除该条转换记录吗？')) return;

    try {
      const res = await fetch(`${BACKEND_URL}/api/screenplay/history/${id}`, {
        method: 'DELETE'
      });
      if (res.ok) {
        showToast('历史记录删除成功', 'success');
        fetchHistory();
        if (activeHistoryId === id) {
          setActiveHistoryId(null);
        }
      }
    } catch (err) {
      showToast('删除历史失败', 'error');
    }
  };

  // Utility Actions
  const handleCopy = () => {
    navigator.clipboard.writeText(screenplayText);
    showToast('已复制剧本到剪贴板！', 'success');
  };

  const handleDownload = () => {
    const extension = selectedStyle === 'yaml' ? 'yaml' : 'md';
    const blob = new Blob([screenplayText], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${novelTitle || 'screenplay'}.${extension}`;
    link.click();
    URL.revokeObjectURL(url);
    showToast('剧本文件已开始下载', 'success');
  };

  return (
    <div className="app-container">
      {/* Toast Alert */}
      {toast.show && (
        <div className={`toast toast-${toast.type}`}>
          <div className="toast-content">{toast.message}</div>
        </div>
      )}

      {/* Header */}
      <header className="app-header">
        <div className="header-brand">
          <div className="brand-logo">🎬</div>
          <div className="brand-texts">
            <h1>Creative Script Studio</h1>
            <p className="subtitle">AI小说转剧本编辑器 • 专业结构化 YAML 输出</p>
          </div>
        </div>
        
        <button 
          id="config-toggle-btn"
          className={`btn-icon-label ${showConfig ? 'active' : ''}`}
          onClick={() => setShowConfig(!showConfig)}
        >
          ⚙️ 开发者配置
        </button>
      </header>

      {/* Dynamic API Configuration Slide-down */}
      {showConfig && (
        <section id="config-drawer" className="config-drawer">
          <div className="config-grid">
            <div className="input-group">
              <label htmlFor="api-url-input">LLM Base URL (自定义接口地址)</label>
              <input 
                id="api-url-input"
                type="text" 
                placeholder="例如: https://api.deepseek.com"
                value={apiConfig.apiUrl}
                onChange={e => setApiConfig({...apiConfig, apiUrl: e.target.value})}
              />
            </div>
            <div className="input-group">
              <label htmlFor="api-key-input">API Secret Key (密钥)</label>
              <input 
                id="api-key-input"
                type="password" 
                placeholder="sk-xxxxxxxxxxxxxxxxxxxxxxxx"
                value={apiConfig.apiKey}
                onChange={e => setApiConfig({...apiConfig, apiKey: e.target.value})}
              />
            </div>
            <div className="input-group">
              <label htmlFor="api-model-input">Model Name (模型名称)</label>
              <input 
                id="api-model-input"
                type="text" 
                placeholder="例如: gemini-3.5-flash 或 deepseek-chat"
                value={apiConfig.model}
                onChange={e => setApiConfig({...apiConfig, model: e.target.value})}
              />
            </div>
          </div>
          <p className="config-note">💡 留空时，后端将自动使用 `application.properties` 配置的系统默认密钥及模型。</p>
        </section>
      )}

      {/* Main Workspace Layout */}
      <main className="workspace-grid">
        
        {/* Left Side: Input area */}
        <section className="workspace-panel input-panel">
          <div className="panel-header">
            <h2 id="novel-input-title">📖 导入小说文本</h2>
            <div className="novel-meta">
              <label htmlFor="novel-title-input" className="sr-only">小说名称</label>
              <input 
                id="novel-title-input"
                type="text" 
                className="title-input" 
                placeholder="剧本/小说名称" 
                value={novelTitle}
                onChange={e => setNovelTitle(e.target.value)}
              />
            </div>
          </div>

          <div className="chapters-container">
            {chapters.map((chapter, index) => (
              <div className="chapter-card" key={chapter.id}>
                <div className="chapter-header">
                  <span className="chapter-index"># {index + 1}</span>
                  <label htmlFor={`chapter-title-${chapter.id}`} className="sr-only">章节名称</label>
                  <input 
                    id={`chapter-title-${chapter.id}`}
                    type="text" 
                    className="chapter-title-input" 
                    placeholder="章节名称(如：第一章：意外遭遇)"
                    value={chapter.title}
                    onChange={e => updateChapter(chapter.id, 'title', e.target.value)}
                  />
                  <button 
                    type="button"
                    className="btn-delete-chapter" 
                    onClick={() => removeChapter(chapter.id)}
                    disabled={chapters.length <= 3}
                    title={chapters.length <= 3 ? "最少需提供3个章节" : "删除章节"}
                  >
                    🗑️
                  </button>
                </div>
                <label htmlFor={`chapter-content-${chapter.id}`} className="sr-only">章节内容</label>
                <textarea 
                  id={`chapter-content-${chapter.id}`}
                  className="chapter-textarea"
                  placeholder="请输入或粘贴该章节的正文内容..."
                  value={chapter.content}
                  onChange={e => updateChapter(chapter.id, 'content', e.target.value)}
                />
              </div>
            ))}
          </div>

          <div className="input-footer">
            <button 
              id="add-chapter-btn"
              type="button" 
              className="btn-secondary" 
              onClick={addChapter}
            >
              ➕ 添加新章节
            </button>
            <p className="min-chapters-hint">💡 建议提供完整连续的小说章节，以便AI分析剧本结构及场景承接关系。</p>
          </div>

          {/* Style Selector Grid */}
          <div className="style-selector-section">
            <h3>🎨 剧本转换风格格式</h3>
            <div className="style-grid">
              {styles.length > 0 ? (
                styles.map(style => (
                  <button
                    key={style.id}
                    className={`style-card ${selectedStyle === style.id ? 'active' : ''}`}
                    onClick={() => setSelectedStyle(style.id)}
                    type="button"
                  >
                    {style.id === 'yaml' && <span className="yaml-badge">✓ 推荐结构化</span>}
                    <h4>{style.name.split(' (')[0]}</h4>
                    <p>{style.description.substring(0, 42)}...</p>
                  </button>
                ))
              ) : (
                // Hardcoded fallbacks if API connection isn't loaded yet
                <>
                  <button 
                    className={`style-card ${selectedStyle === 'yaml' ? 'active' : ''}`}
                    onClick={() => setSelectedStyle('yaml')}
                    type="button"
                  >
                    <span className="yaml-badge">✓ 推荐结构化</span>
                    <h4>结构化 YAML 剧本</h4>
                    <p>自动输出章节、场景、台词流，支持 YAML 解析与修改。</p>
                  </button>
                  <button 
                    className={`style-card ${selectedStyle === 'standard' ? 'active' : ''}`}
                    onClick={() => setSelectedStyle('standard')}
                    type="button"
                  >
                    <h4>电影标准剧本</h4>
                    <p>传统电影剧本，包含场景头、动作描述和台词。</p>
                  </button>
                  <button 
                    className={`style-card ${selectedStyle === 'short-video' ? 'active' : ''}`}
                    onClick={() => setSelectedStyle('short-video')}
                    type="button"
                  >
                    <h4>短视频分镜头</h4>
                    <p>抖音小红书表格分镜头，含景别与BGM指导。</p>
                  </button>
                </>
              )}
            </div>
          </div>

          {/* Glowing Action Button */}
          <div className="action-container">
            <button 
              id="convert-trigger-btn"
              type="button" 
              className={`btn-primary ${converting ? 'loading' : ''}`}
              onClick={handleConvert}
              disabled={converting}
            >
              {converting ? (
                <div className="loader-content">
                  <span className="spinner"></span>
                  <span>{loadingStep}</span>
                </div>
              ) : (
                <span>🚀 开始自动生成结构化剧本 (YAML)</span>
              )}
            </button>
          </div>
        </section>

        {/* Right Side: Output area & History log */}
        <section className="workspace-panel output-panel">
          <div className="panel-tabs">
            <button 
              className={`tab-btn ${activeTab === 'draft' ? 'active' : ''}`}
              onClick={() => setActiveTab('draft')}
              type="button"
            >
              📝 剧本初稿
            </button>
            <button 
              className={`tab-btn ${activeTab === 'history' ? 'active' : ''}`}
              onClick={() => setActiveTab('history')}
              type="button"
            >
              📜 历史版本 ({history.length})
            </button>
          </div>

          {activeTab === 'draft' ? (
            <div className="tab-pane-content draft-pane">
              <div className="draft-toolbar">
                <div className="yaml-status-indicator">
                  <span className={`status-dot ${yamlStatus.isValid ? 'valid' : 'invalid'}`}></span>
                  <span className="status-message">{yamlStatus.message}</span>
                </div>
                <div className="toolbar-actions">
                  <button 
                    type="button" 
                    className="btn-toolbar" 
                    onClick={handleCopy}
                    disabled={!screenplayText}
                    title="复制到剪贴板"
                  >
                    📋 复制
                  </button>
                  <button 
                    type="button" 
                    className="btn-toolbar" 
                    onClick={handleDownload}
                    disabled={!screenplayText}
                    title="保存到本地"
                  >
                    💾 下载
                  </button>
                </div>
              </div>

              <div className="output-editor-container">
                <label htmlFor="output-textarea" className="sr-only">生成的剧本内容</label>
                <textarea 
                  id="output-textarea"
                  className="output-textarea"
                  placeholder="转换成功后，剧本将在此处显示，你也可以在这里直接进行修改、编辑和打磨..."
                  value={screenplayText}
                  onChange={e => setScreenplayText(e.target.value)}
                />
              </div>
              
              <div className="draft-footer">
                <p>✏️ 支持直接在上方文本框中修改和打磨剧本。可点击右上角“下载”将剧本导出为 YAML 文件格式保存。</p>
              </div>
            </div>
          ) : (
            <div className="tab-pane-content history-pane">
              {history.length > 0 ? (
                <div className="history-list">
                  {history.map(item => (
                    <div 
                      key={item.id} 
                      className="history-item-card"
                      onClick={() => loadHistoryItem(item)}
                    >
                      <div className="history-item-header">
                        <h4>{item.title}</h4>
                        <span className="history-date">
                          {new Date(item.createdAt).toLocaleString()}
                        </span>
                      </div>
                      <div className="history-item-body">
                        <span className="badge-model">{item.modelUsed || 'default'}</span>
                        <span className="badge-style">{item.promptStyle}</span>
                      </div>
                      <div className="history-item-actions">
                        <span className="load-hint">点击载入该版本</span>
                        <button 
                          type="button" 
                          className="btn-delete-history"
                          onClick={(e) => deleteHistoryItem(e, item.id)}
                          title="删除记录"
                        >
                          🗑️ 删除
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="empty-history">
                  <div className="empty-icon">📂</div>
                  <p>暂无历史转换版本，立刻在左侧录入小说并生成剧本吧！</p>
                </div>
              )}
            </div>
          )}
        </section>

      </main>
    </div>
  );
}

export default App;
