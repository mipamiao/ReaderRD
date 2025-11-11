class DataProcessor {
  constructor() {
    this.allContent = [];
    this.lastContent = '';
    this.totalPages = 0;
    this.currentPage = 0;
    this.uiManager = null;
  }

  setUIManager(uiManager) {
    this.uiManager = uiManager;
  }

  // 处理PageInfo响应
  handlePageInfoResponse(commands) {
    try {
      // 设置总页数
      this.totalPages = commands[0].contentInfo.pageIds.length;
      this.currentPage = 0;
      this.allContent = [];
      
      // 更新进度显示
      if (this.uiManager) {
        this.uiManager.updateProgress(this.currentPage, this.totalPages);
        this.uiManager.showProgressBar(true);
      }
      
      // 返回pageIds数组以便请求页面内容
      return commands[0].contentInfo.pageIds;
    } catch (error) {
      console.error('处理PageInfo响应出错:', error);
      return [];
    }
  }

  // 处理Page响应
  handlePageResponse(command) {
    if (command && command.data !== undefined) {
      // 存储当前页面的内容
      const startPos = command.index || 0;
      this.allContent[startPos] = command.data;
      
      // 更新当前进度
      this.currentPage++;
      if (this.uiManager) {
        this.uiManager.updateProgress(this.currentPage, this.totalPages);
      }
      
      // 检查是否所有页面都已加载完成
      if (this.currentPage === this.totalPages) {
        return this.mergeAndDisplayContent();
      }
    }
    return null;
  }

  // 合并所有内容并返回
  mergeAndDisplayContent() {
    // 合并所有页面内容
    const mergedContent = this.allContent.filter(Boolean).join('');
    
    // 更新最后内容
    this.lastContent = mergedContent;
    
    return mergedContent;
  }

  // 检测内容变化
  detectChanges(currentContent) {
    const oldContent = this.lastContent;
    
    // 从前往后找到第一个不同的位置
    let startDiff = 0;
    while (startDiff < oldContent.length && startDiff < currentContent.length &&
           oldContent[startDiff] === currentContent[startDiff]) {
      startDiff++;
    }

    // 从后往前找到第一个不同的位置
    let oldEndDiff = oldContent.length - 1;
    let newEndDiff = currentContent.length - 1;

    while (oldEndDiff >= startDiff && newEndDiff >= startDiff &&
           oldContent[oldEndDiff] === currentContent[newEndDiff]) {
      oldEndDiff--;
      newEndDiff--;
    }

    // 判断操作类型并返回变更信息
    if (oldEndDiff < startDiff && newEndDiff >= startDiff) {
      // 插入操作
      const insertPos = startDiff;
      const insertText = currentContent.substring(startDiff, newEndDiff + 1);
      return {
        type: 'UpdatePage_Add',
        position: insertPos,
        length: 0,
        data: insertText
      };
    } else if (oldEndDiff >= startDiff && newEndDiff < startDiff) {
      // 删除操作
      const deletePos = startDiff;
      const deleteLen = oldEndDiff - startDiff + 1;
      return {
        type: 'UpdatePage_Remove',
        position: deletePos,
        length: deleteLen,
        data: ''
      };
    } else if (oldEndDiff >= startDiff && newEndDiff >= startDiff) {
      // 替换操作
      const replacePos = startDiff;
      const replaceLen = oldEndDiff - startDiff + 1;
      const newText = currentContent.substring(startDiff, newEndDiff + 1);
      return {
        type: 'UpdatePage_Replace',
        position: replacePos,
        length: replaceLen,
        data: newText
      };
    }
    
    // 内容相同的情况无需处理
    return null;
  }

  // 更新最后内容
  updateLastContent(content) {
    this.lastContent = content;
  }
}