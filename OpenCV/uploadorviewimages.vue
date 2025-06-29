<template>
  <div>
    <h3>图片预览</h3>
    <img
        :src="fileUrl('dog.jpg')"
        alt="Dog"
        style="max-width: 100%; border: 1px solid #ccc;"
    />

    <h3>视频预览</h3>
    <video
        :src="fileUrl('demo.mp4')"
        controls
        style="max-width: 100%; margin-top: 1em;"
    ></video>

    <!-- ↓ 新增下载区域 ↓ -->
    <div style="margin-top:2em; padding:1em; border:1px dashed #666;">
      <h4>下载文件</h4>
      <input
          v-model="downloadName"
          type="text"
          placeholder="请输入文件名（含扩展名）"
          style="padding: 0.5em; width: 200px;"
      />
      <button
          @click="downloadFile"
          style="padding: 0.5em 1em; margin-left: 0.5em; cursor: pointer;"
      >
        下载
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// API 根路径
const apiBase = 'http://192.168.3.29:9999/api/files';

// 供预览使用
function fileUrl(filename) {
  return `${apiBase}/${encodeURIComponent(filename)}?inline=true`;
}

// 下载框输入
const downloadName = ref('');

// 点击按钮时调用，触发浏览器下载
function downloadFile() {
  if (!downloadName.value) {
    alert('请先输入要下载的文件名');
    return;
  }
  const url = `${apiBase}/down/${encodeURIComponent(downloadName.value)}`;
  // 方法一：直接跳转，由后端的 Content-Disposition: attachment 触发下载
  window.location.href = url;

  // 方法二：动态创建 <a download>（可选，兼容更多场景）
  // const link = document.createElement('a');
  // link.href = url;
  // link.download = downloadName.value;
  // document.body.appendChild(link);
  // link.click();
  // document.body.removeChild(link);
}
</script>
