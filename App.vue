<script setup>
// 这里可以引入侧边栏内容组件或直接写内容
</script>

<template>
  <div class="app-container">
    <div class="sidebar">
      <!-- 侧边栏内容 -->
      <h2>侧边栏</h2>
      <ul>
        <li><router-link to="/chart">图表</router-link></li>
        <li>菜单2</li>
        <li>菜单3</li>
      </ul>
    </div>
    <div class="main-content">
      <div class="content-wrapper">
        <router-view />
      </div>
    
    </div>
  </div>
</template>

<style scoped>

/* 整体布局为flex，左侧侧边栏，右侧主内容区 */
.app-container {
  display: flex;
  height: 100vh;
  background: #f0f2f5;
  /* 增加整体背景色，突出主内容区 */
}

/* 侧边栏样式，固定宽度180px，深色背景，白色文字 */
.sidebar {
  width: 180px;
  background: #2d3a4b;
  color: #fff;
  padding: 24px 12px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  min-height: 100vh;
}

/* 主内容区样式，填满剩余空间 */
.main-content {
  flex: 1;
  padding: 24px;
  min-height: 100vh;
  width: 3000px;
  box-sizing: border-box;
  display: block;
  background: #f0f2f5;

}

/* 主内容区内部容器，去掉最大宽度和居中，宽度100% */
.content-wrapper {
  display: block;
  width: 100%;
  min-height: calc(100vh - 48px);
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  padding: 32px;
  box-sizing: border-box;
  
}

/* 侧边栏标题样式，去除顶部外边距，调整字号和下间距 */
.sidebar h2 {
  margin-top: 0;
  font-size: 1.2em;
  margin-bottom: 16px;
}

/* 侧边栏菜单列表样式，去除默认样式 */
.sidebar ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

/* 侧边栏菜单项样式，增加下间距，鼠标悬停有颜色变化 */
.sidebar li {
  margin-bottom: 12px;
  cursor: pointer;
  transition: color 0.2s;
}

.sidebar li:hover {
  color: #ffd04b;
}
</style>
