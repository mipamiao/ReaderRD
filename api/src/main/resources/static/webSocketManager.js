class WebSocketManager {
  constructor() {
    this.socket = null;
    this.isConnected = false;
    this.reconnectTimeout = null;
    this.onOpenCallback = null;
    this.onMessageCallback = null;
    this.onErrorCallback = null;
    this.onCloseCallback = null;
  }

  connect(url, onOpenCallback, onMessageCallback, onErrorCallback, onCloseCallback) {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      return;
    }

    // 保存回调函数以便重连时使用
    this.onOpenCallback = onOpenCallback;
    this.onMessageCallback = onMessageCallback;
    this.onErrorCallback = onErrorCallback;
    this.onCloseCallback = onCloseCallback;

    this.socket = new WebSocket(url);

    this.socket.onopen = () => {
      console.log('✅ WebSocket 已连接');
      this.isConnected = true;
      if (this.onOpenCallback) this.onOpenCallback();
    };

    this.socket.onmessage = (event) => {
      if (this.onMessageCallback) this.onMessageCallback(event);
    };

    this.socket.onerror = (error) => {
      console.error('❌ WebSocket 发生错误:', error);
      this.isConnected = false;
      if (this.onErrorCallback) this.onErrorCallback(error);
    };

    this.socket.onclose = () => {
      console.log('🔌 WebSocket 连接已关闭');
      this.isConnected = false;
      if (this.onCloseCallback) this.onCloseCallback();
    };
  }

  send(message) {
    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
      console.warn('WebSocket 未连接，无法发送消息');
      return false;
    }

    try {
      this.socket.send(JSON.stringify(message));
      return true;
    } catch (error) {
      console.error('发送消息失败:', error);
      return false;
    }
  }

  close() {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
      this.isConnected = false;
    }
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }
  }

  scheduleReconnect(url, onReconnectCallback, delay = 3000) {
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
    }

    this.reconnectTimeout = setTimeout(() => {
      console.log('⏳ 尝试重新连接...');
      // 使用保存的回调函数进行重连
      this.connect(url, this.onOpenCallback, this.onMessageCallback, this.onErrorCallback, this.onCloseCallback);
      if (onReconnectCallback) onReconnectCallback();
    }, delay);
  }
}