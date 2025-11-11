// 应用主逻辑
(function() {
  // 初始化各个模块
  const webSocketManager = new WebSocketManager();
  const dataProcessor = new DataProcessor();
  const uiManager = new UIManager();
  
  // 初始化UI
  uiManager.init();
  
  // 设置数据处理器的UI管理器
  dataProcessor.setUIManager(uiManager);
  
  // 设置UI回调
  uiManager.setRefreshCallback(onRefreshClick);
  uiManager.setInputCallback(onEditorInput);
  
  // 连接WebSocket
  connect();
  
  // 连接函数
  function connect() {
    webSocketManager.connect(
      CONFIG.wsUrl(),
      onWebSocketOpen,
      onWebSocketMessage,
      onWebSocketError,
      onWebSocketClose
    );
  }
  
  // WebSocket打开回调
  function onWebSocketOpen() {
    uiManager.updateStatus('✅ WebSocket 已连接');
    uiManager.setEditorEnabled(true);
    
    // 首先发送GetPageInfo请求
    sendGetPageInfoRequest();
  }
  
  // WebSocket消息回调
  function onWebSocketMessage(event) {
    console.log("📩 收到后端消息:", event.data);
    try {
      // 先解析整个JSON字符串
      const serverData = JSON.parse(event.data);

      // 根据后端类定义，应该是ServerCommandSet对象，包含commands数组
      if (serverData && serverData.commands && serverData.commands.length > 0) {
        // 如果是GetPageInfo的响应
        if (serverData.type === "GetPageInfoResponse") {
          const pageIds = dataProcessor.handlePageInfoResponse(serverData.commands);
          
          // 根据pageIds发送多次GetPage请求
          pageIds.forEach((pageId, index) => {
            // 使用setTimeout避免请求发送过快
            setTimeout(() => {
              sendGetPageRequest(index);
            }, index * 100); // 每个请求间隔100ms
          });
        } 
        // 如果是GetPage的响应
        else if (serverData.type === "GetPageResponse") {
          const mergedContent = dataProcessor.handlePageResponse(serverData.commands[0]);
          
          // 如果所有页面都已加载完成，显示合并后的内容
          if (mergedContent !== null) {
            uiManager.updateEditorContent(mergedContent);
            uiManager.updateStatus('✅ 内容加载完成');
            
            // 隐藏进度条
            setTimeout(() => {
              uiManager.showProgressBar(false);
            }, 1000);
          }
        }
      }
    } catch (e) {
      console.error("解析后端 JSON 出错:", e);
      uiManager.updateStatus(`❌ 解析出错: ${e.message}`);
    }
  }
  
  // WebSocket错误回调
  function onWebSocketError(error) {
    uiManager.updateStatus('❌ 连接出错');
  }
  
  // WebSocket关闭回调
  function onWebSocketClose() {
    uiManager.updateStatus('🔌 连接已关闭，3 秒后重连...');
    uiManager.setEditorEnabled(false);
    
    // 安排重连
    webSocketManager.scheduleReconnect(CONFIG.wsUrl(), () => {
      uiManager.updateStatus('⏳ 正在重新连接...');
    });
  }
  
  // 发送GetPageInfo请求
  function sendGetPageInfoRequest() {
    const msg = {
      type: "GetPageInfo",
      startPos: 0,
      otherPos: 0,
      num: 0,
      data: ""
    };
    const msgforsend = {
      commands: [msg],
      timeStamp: new Date().toISOString()
    };
    
    webSocketManager.send(msgforsend);
  }
  
  // 发送GetPage请求
  function sendGetPageRequest(startPos) {
    const msg = {
      type: "GetPage",
      startPos: startPos, // 使用列表下标作为startPos
      otherPos: 0,
      num: 0,
      data: ""
    };
    
    const msgforsend = {
      commands: [msg],
      timeStamp: new Date().toISOString()
    };
    
    console.log(`📤 发送GetPage请求，startPos=${startPos}`);
    webSocketManager.send(msgforsend);
  }
  
  // 发送更新消息
  function sendUpdateMessage(type, startPos, num, data) {
    const msg = {
      type: type,
      startPos: 0,
      otherPos: startPos, // 根据需要设置
      num: num,
      data: data
    };

    const msgforsend = {
      commands: [msg],
      timeStamp: new Date().toISOString()
    };

    console.log("📤 发送消息:", JSON.stringify(msgforsend));
    webSocketManager.send(msgforsend);
  }
  
  // 刷新按钮点击事件
  function onRefreshClick() {
    if (webSocketManager.isConnected) {
      uiManager.updateStatus('🔄 重新获取内容中...');
      sendGetPageInfoRequest();
    } else {
      uiManager.updateStatus('❌ WebSocket 未连接');
    }
  }
  
  // 编辑器输入事件
  function onEditorInput(currentContent) {
    if (!webSocketManager.isConnected) {
      return;
    }

    // 计算变更
    const changeInfo = dataProcessor.detectChanges(currentContent);
    
    // 如果有变更，发送更新消息
    if (changeInfo) {
      sendUpdateMessage(changeInfo.type, changeInfo.position, changeInfo.length, changeInfo.data);
      
      // 更新最后内容
      dataProcessor.updateLastContent(currentContent);
    }
  }
  
  // 页面卸载时关闭WebSocket连接
  window.addEventListener('beforeunload', () => {
    webSocketManager.close();
  });
})();