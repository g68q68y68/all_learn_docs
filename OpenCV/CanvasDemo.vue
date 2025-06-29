<template>
  <div>
    <!-- Canvas 及控件 -->
    <canvas ref="canvas" width="600" height="400" class="border"></canvas>
    <div id="controls">
      <button @click="startDrawing" :disabled="drawingEnabled">Start Drawing</button>
      <button @click="clearCanvas">Clear Canvas</button>
      <button v-for="mode in modes" :key="mode" @click="setMode(mode)" :class="{ active: currentMode === mode }">
        {{ mode.charAt(0).toUpperCase() + mode.slice(1) }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

// 1. 获取 Canvas 元素及其 2D 绘图上下文
const canvas = ref(null);
let ctx;

// 2. 定义可用绘图模式数组
const modes = ['point', 'line', 'rect', 'circle', 'arc'];

// 3. 定义响应式状态
const drawingEnabled = ref(false);    // 控制是否启用绘图
const currentMode = ref('point');     // 当前绘图模式，默认为 'point'
const isDrawing = ref(false);         // 标记鼠标是否处于按下状态
const startPos = ref({ x: 0, y: 0 }); // 记录拖拽绘制的起点坐标
const shapes = ref([]);               // 存储所有已绘制的图形数据

// 4. 启用绘图
function startDrawing() {
  drawingEnabled.value = true;       // 一旦启用，就允许响应鼠标事件绘制图形
}

// 5. 清空画布
function clearCanvas() {
  shapes.value = [];                 // 清空数据数组
  redraw();                          // 清空并重绘（此时仅会清空画布，因为 shapes 为空）
}

// 6. 设置绘图模式
function setMode(mode) {
  currentMode.value = mode;          // 更新当前选中模式
}

// 7. 重绘函数：清空画布并重绘所有已存图形
function redraw() {
  const c = canvas.value;
  ctx.clearRect(0, 0, c.width, c.height); // clearRect(x,y,w,h)：清除指定区域
  shapes.value.forEach(shape => drawShape(shape));
}

// 8. 单个图形绘制函数：使用 beginPath/save/restore 系列 API
function drawShape(shape) {
  ctx.save();                         // save()：保存当前绘图状态（样式、变换、剪切等）
  ctx.beginPath();                    // beginPath(): 开启新路径，确保新图形不会和旧路径相连

  // 8.1 设置绘图样式
  ctx.lineWidth = 2;                  // 线宽
  ctx.strokeStyle = '#000';           // 描边颜色
  ctx.fillStyle = '#000';             // 填充颜色（仅点模式使用）

  // 8.2 根据图形类型调用不同 API
  switch (shape.type) {
    case 'point':
      // arc(x,y,r,startAngle,endAngle)：绘制圆或圆弧路径
      ctx.arc(shape.x, shape.y, 2, 0, Math.PI * 2);
      // fill(): 填充当前路径
      ctx.fill();
      break;

    case 'line':
      // moveTo(x,y)：将画笔移动到起点
      ctx.moveTo(shape.x1, shape.y1);
      // lineTo(x,y)：从当前点绘制直线到目标点
      ctx.lineTo(shape.x2, shape.y2);
      // stroke(): 描边当前路径
      ctx.stroke();
      break;

    case 'rect':
      // strokeRect(x,y,width,height)：绘制矩形路径并立即描边
      const xMin = Math.min(shape.x1, shape.x2);
      const yMin = Math.min(shape.y1, shape.y2);
      const w = Math.abs(shape.x2 - shape.x1);
      const h = Math.abs(shape.y2 - shape.y1);
      ctx.strokeRect(xMin, yMin, w, h);
      break;

    case 'circle':
      // compute radius via hypot(dx,dy)
      const r = Math.hypot(shape.x2 - shape.x1, shape.y2 - shape.y1);
      ctx.arc(shape.x1, shape.y1, r, 0, Math.PI * 2);
      ctx.stroke();
      break;

    case 'arc':
      // 绘制半圆
      const r2 = Math.hypot(shape.x2 - shape.x1, shape.y2 - shape.y1);
      ctx.arc(shape.x1, shape.y1, r2, 0, Math.PI);
      ctx.stroke();
      break;
  }

  ctx.restore();                      // restore(): 恢复到上一次 save() 时的状态
}

// 9. Canvas 鼠标事件处理
function onMouseDown(e) {
  if (!drawingEnabled.value) return;  // 未启用绘图则直接返回
  const rect = canvas.value.getBoundingClientRect();
  /**
   * getBoundingClientRect():
   *   返回元素在视口中的位置和尺寸：{ left, top, width, height }
   *   用于将鼠标的 clientX/Y 转换成相对于画布的坐标
   */
  const x = e.clientX - rect.left;      // clientX: 鼠标相对视口的水平坐标
  const y = e.clientY - rect.top;       // clientY: 鼠标相对视口的垂直坐标

  if (currentMode.value === 'point') {
    // 点模式：立即绘制并存储点数据
    shapes.value.push({ type: 'point', x, y });
    drawShape({ type: 'point', x, y });
  } else {
    // 其他模式：拖拽开始
    isDrawing.value = true;
    startPos.value = { x, y };
  }
}

function onMouseMove(e) {
  if (!drawingEnabled.value || !isDrawing.value || currentMode.value === 'point') return;
  const rect = canvas.value.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;

  redraw();                            // 清空并重绘历史图形
  drawShape({
    type: currentMode.value,
    x1: startPos.value.x,
    y1: startPos.value.y,
    x2: x,
    y2: y
  });
}

function onMouseUp(e) {
  if (!drawingEnabled.value || !isDrawing.value) return;
  const rect = canvas.value.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;
  // 存储最终拖拽图形
  shapes.value.push({
    type: currentMode.value,
    x1: startPos.value.x,
    y1: startPos.value.y,
    x2: x,
    y2: y
  });
  isDrawing.value = false;
  redraw();
}

// 10. 注册事件并初始化
onMounted(() => {
  ctx = canvas.value.getContext('2d');    // 初始化 2D 上下文
  canvas.value.addEventListener('mousedown', onMouseDown);
  canvas.value.addEventListener('mousemove', onMouseMove);
  canvas.value.addEventListener('mouseup', onMouseUp);
});
</script>

<style scoped>
.border {
  border: 1px solid #ccc;
}
#controls button {
  margin-right: 5px;
  padding: 5px 10px;
}
#controls button.active {
  background-color: #4CAF50;
  color: white;
}
</style>
