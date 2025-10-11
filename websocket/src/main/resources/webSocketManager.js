class WebSocketManager {
  constructor() {
    this.socket = null;
    this.isConnected = false;
    this.reconnectTimeout = null;
  }

  connect(url, onOpenCallback, onMessageCallback, onErrorCallback, onCloseCallback) {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      return;
    }

    this.socket = new WebSocket(url);

    this.socket.onopen = () => {
      console.log('✅ WebSocket 已连接');
      this.isConnected = true;
      if (onOpenCallback) onOpenCallback();
    };

    this.socket.onmessage = (event) => {
      if (onMessageCallback) onMessageCallback(event);
    };

    this.socket.onerror = (error) => {
      console.error('❌ WebSocket 发生错误:', error);
      this.isConnected = false;
      if (onErrorCallback) onErrorCallback(error);
    };

    this.socket.onclose = () => {
      console.log('🔌 WebSocket 连接已关闭');
      this.isConnected = false;
      if (onCloseCallback) onCloseCallback();
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
      this.connect(url);
      if (onReconnectCallback) onReconnectCallback();
    }, delay);
  }
}