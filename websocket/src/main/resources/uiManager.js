class UIManager {
  constructor() {
    this.statusEl = null;
    this.editorEl = null;
    this.refreshBtn = null;
    this.progressContainer = null;
    this.progressBar = null;
    this.progressText = null;
    this.refreshCallback = null;
    this.inputCallback = null;
  }

  // 初始化UI元素
  init() {
    this.statusEl = document.getElementById('status');
    this.editorEl = document.getElementById('editor');
    this.refreshBtn = document.getElementById('refreshBtn');
    this.progressContainer = document.getElementById('progressContainer');
    this.progressBar = document.getElementById('progressBar');
    this.progressText = document.getElementById('progressText');
    
    // 绑定事件监听器
    this.bindEvents();
  }

  // 绑定事件
  bindEvents() {
    if (this.refreshBtn) {
      this.refreshBtn.addEventListener('click', () => {
        if (this.refreshCallback) {
          this.refreshCallback();
        }
      });
    }
    
    if (this.editorEl) {
      this.editorEl.addEventListener('input', () => {
        if (this.inputCallback && !this.editorEl.disabled) {
          this.inputCallback(this.editorEl.value);
        }
      });
    }
  }

  // 设置刷新按钮回调
  setRefreshCallback(callback) {
    this.refreshCallback = callback;
  }

  // 设置输入框回调
  setInputCallback(callback) {
    this.inputCallback = callback;
  }

  // 更新状态文本
  updateStatus(message) {
    if (this.statusEl) {
      this.statusEl.textContent = message;
    }
  }

  // 更新编辑器内容
  updateEditorContent(content) {
    if (this.editorEl) {
      this.editorEl.value = content;
    }
  }

  // 启用/禁用编辑器
  setEditorEnabled(enabled) {
    if (this.editorEl) {
      this.editorEl.disabled = !enabled;
    }
  }

  // 显示/隐藏进度条
  showProgressBar(show) {
    if (this.progressContainer && this.progressText) {
      this.progressContainer.style.display = show ? 'block' : 'none';
      this.progressText.style.display = show ? 'block' : 'none';
    }
  }

  // 更新进度
  updateProgress(current, total) {
    if (this.progressBar && this.progressText) {
      const progress = total > 0 ? Math.floor((current / total) * 100) : 0;
      this.progressBar.style.width = `${progress}%`;
      this.progressText.textContent = `加载进度: ${current}/${total}`;
    }
  }

  // 获取编辑器当前内容
  getEditorContent() {
    return this.editorEl ? this.editorEl.value : '';
  }
}