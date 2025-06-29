<template>
  <div>
    <!-- 视频 + Canvas 容器 -->
    <div class="video-container" style="position: relative; width: 400px;">
      <!-- 底层视频 -->
      <video
          ref="videoEl"
          :src="videoSrc"
          @loadedmetadata="onLoadedMetadata"
          @seeked="onSeeked"
          @click="togglePlay"
          style="width: 100%; display: block;"
      ></video>

      <!-- ROI 绘制 Canvas -->
      <canvas
          ref="canvasEl"
          :width="canvasWidth"
          :height="canvasHeight"
          class="video-canvas"
          style="position: absolute; top: 0; left: 0;"
      ></canvas>

      <!-- 中央播放按钮 -->
      <div
          v-if="showPlayButton"
          class="play-btn"
          @click="togglePlay"
      >
        ▶
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';

// 1. 视频源路径
const videoSrc = 'video.mp4';

// 2. 原始视频尺寸
const videoWidth = ref(0);
const videoHeight = ref(0);

// 3. Canvas 固定宽度
const canvasWidth = 400;
const canvasHeight = ref(0);

// 4. 绘图上下文
let ctx;

// 5. ROI 数据
let isDrawing = false;
let startX = 0;
let startY = 0;
const rois = ref([]);

// 6. 播放按钮状态
const showPlayButton = ref(true);

// 7. video 元素加载完元数据
function onLoadedMetadata() {
  const video = videoEl.value;
  videoWidth.value = video.videoWidth;
  videoHeight.value = video.videoHeight;

  const scale = canvasWidth / videoWidth.value;
  canvasHeight.value = videoHeight.value * scale;

  // 暂停在第一帧，等待用户点击播放
  video.currentTime = 5;
  video.pause();

  // 播放结束后重新显示按钮
  video.addEventListener('ended', () => {
    showPlayButton.value = true;
  });
}

// 8. 视频 seek 完成后初始化 Canvas
function onSeeked() {
  ctx = canvasEl.value.getContext('2d');
  ctx.clearRect(0, 0, canvasWidth, canvasHeight.value);
}

// 9. 播放 / 暂停切换
function togglePlay(event) {
  // 阻止事件冒泡，避免与 Canvas 交互冲突
  if (event) event.stopPropagation();
  const video = videoEl.value;
  if (video.paused) {
    video.play();
    showPlayButton.value = false;
  } else {
    video.pause();
    showPlayButton.value = true;
  }
}

// ROI 交互
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

  ctx.clearRect(0, 0, canvasWidth, canvasHeight.value);
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

  const scale = videoWidth.value / canvasWidth;
  const roiX = startX * scale;
  const roiY = startY * scale;
  const roiW = (endX - startX) * scale;
  const roiH = (endY - startY) * scale;

  rois.value.push({ x: roiX, y: roiY, width: roiW, height: roiH });
  console.log('原始视频 ROI:', rois.value[rois.value.length - 1]);
}

const canvasEl = ref();
const videoEl = ref();

onMounted(() => {
  // Canvas 事件绑定
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

.play-btn {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 32px;
  line-height: 64px;
  text-align: center;
  width: 64px;
  height: 64px;
  color: #fff;
  background-color: rgba(0, 0, 0, 0.4);
  border-radius: 50%;
  cursor: pointer;
  user-select: none;
  transition: background-color 0.3s;
}

.play-btn:hover {
  background-color: rgba(0, 0, 0, 0.6);
}
</style>
