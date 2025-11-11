// 配置参数
const CONFIG = {
  userId: '0051e957c8cd41f4a4379793aecc5219',       // TODO: 改成你的 userId
  chapterId: '8987404f7a764731926b26b728aa7139',    // TODO: 改成你的 chapterId
  wsUrl: function() {
    return `ws://localhost:8080/ws/writer/${this.userId}/${this.chapterId}`;
  }
};