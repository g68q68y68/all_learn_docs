<template>
  <div>
    <!-- 隐藏的 video 元素，用于加载并抓取第一帧 -->
    <video ref="videoEl" :src="videoSrc" @loadedmetadata="onLoadedMetadata" style="display:none"></video>

    <!-- 绘制视频帧和 ROI 的 Canvas，宽度固定 400px，高度按视频比例设置 -->
    <canvas ref="canvasEl" :width="canvasWidth" :height="canvasHeight" class="video-canvas"></canvas>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue';

// 1. 视频源路径
const videoSrc = 'video.mp4';

// 2. 原始视频尺寸（在 loadedmetadata 后获取）
const videoWidth = ref(0);
const videoHeight = ref(0);

// 3. Canvas 固定宽度，按比例计算高度
const canvasWidth = 400;
const canvasHeight = ref(0);

// 4. 绘图上下文
let ctx;

// 5. 鼠标绘制状态和 ROI 数据
let isDrawing = false;
let startX = 0;
let startY = 0;
const rois = ref([]); // 存储所有绘制 ROI 的像素坐标（原始视频分辨率）

// 6. video 元素加载元数据后，初始化 Canvas 大小并绘制第一帧
function onLoadedMetadata() {
  const video = videoEl.value;
  videoWidth.value = video.videoWidth;    // e.g. 1280
  videoHeight.value = video.videoHeight;  // e.g. 720
  console.log(videoWidth.value,videoHeight.value)

  // 等比例缩放到固定宽度
  const scale = canvasWidth / videoWidth.value;
  canvasHeight.value = videoHeight.value * scale;

  // 获取 canvas 上下文
  const canvas = canvasEl.value;
  ctx = canvas.getContext('2d');

  // 把视频第一帧绘制到 Canvas
  video.currentTime = 5;
  video.pause();
  video.addEventListener('seeked', () => {
    ctx.drawImage(video, 0, 0, canvasWidth, canvasHeight.value);
  }, { once: true });
}

// 7. 鼠标事件：开始/移动/结束绘制 ROI
function onMouseDown(e) {
  const rect = canvasEl.value.getBoundingClientRect();
  startX = e.clientX - rect.left;
  startY = e.clientY - rect.top;
  isDrawing = true;
}
function onMouseMove(e) {
  if (!isDrawing) return;
  const rect = canvasEl.value.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;

  // 重新绘制视频帧 + ROI 预览
  ctx.clearRect(0, 0, canvasWidth, canvasHeight.value);
  ctx.drawImage(videoEl.value, 0, 0, canvasWidth, canvasHeight.value);
  ctx.strokeStyle = 'red';
  ctx.lineWidth = 2;
  ctx.strokeRect(startX, startY, x - startX, y - startY);
}
function onMouseUp(e) {
  if (!isDrawing) return;
  isDrawing = false;
  const rect = canvasEl.value.getBoundingClientRect();
  const endX = e.clientX - rect.left;
  const endY = e.clientY - rect.top;

  // 计算 ROI 在原始视频坐标
  const scale = videoWidth.value / canvasWidth;
  const roiX = startX * scale;
  const roiY = startY * scale;
  const roiW = (endX - startX) * scale;
  const roiH = (endY - startY) * scale;

  rois.value.push({ x: roiX, y: roiY, width: roiW, height: roiH });
  console.log('原始视频 ROI:', rois.value[rois.value.length - 1]);
  console.log(rois.value)
}

// 8. 绑定事件
const canvasEl = ref();
const videoEl = ref();
onMounted(() => {
  // 监听视频 metadata
  // videoEl.value.addEventListener('loadedmetadata', onLoadedMetadata, { once: true });
  // 绑定 Canvas 事件
  canvasEl.value.addEventListener('mousedown', onMouseDown);
  canvasEl.value.addEventListener('mousemove', onMouseMove);
  canvasEl.value.addEventListener('mouseup', onMouseUp);
});
</script>

<style scoped>
.video-canvas {
  border: 1px solid #ccc;
  cursor: crosshair;
}
</style>
