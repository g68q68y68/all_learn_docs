## 修改方案

要实现稳定的1080*1920@60fps视频流，需要在服务端使用MediaStreamTrackProcessor和WebCodec API进行帧处理和编码，客户端也需要相应修改。

### 当前实现分析

当前服务端在`CatchDesktopVideo`函数中直接将捕获的MediaStream添加到ServerVideoPeer [1](#1-0) ：

```typescript
const stream = await navigator.mediaDevices.getDisplayMedia({
    audio: audio,
    video: {
        frameRate: Number.parseInt(settingStore.frameRate)
    }
})
streams = stream
stream.getTracks().forEach(track => { send = store.ServerVideoPeer.addTrack(track, stream) })
```

客户端通过`ontrack`事件接收视频流 [2](#1-1) 。

### 服务端修改方案

需要修改`CatchDesktopVideo`函数，使用MediaStreamTrackProcessor处理帧，WebCodec API编码后通过DataChannel发送：

1. **移除直接addTrack**，改为使用MediaStreamTrackProcessor
2. **添加WebCodec编码器**，配置为1080x1920@60fps
3. **通过DataChannel发送编码后的数据**

```typescript
async function CatchDesktopVideo(data: ScreenData) {
    const open = data.open
    const sourceId = await window.oneMouse.GetDeskTopId()
    if (!store.ServerVideoPeer) {
        ServerVideoConnect(`http://${connectLink.value}`, port.value, connectPassword.value, onmessage)
    }
    
    if (!open && processor) {
        processor.abort()
        encoder.close()
        processor = undefined
        encoder = undefined
        return
    }
    
    const { frameRate, audio } = data
    if (frameRate) settingStore.updateFrameRate(frameRate)
    
    if (store.ServerVideoPeer && sourceId) {
        try {
            const stream = await navigator.mediaDevices.getDisplayMedia({
                audio: audio,
                video: {
                    frameRate: 60  // 固定60fps
                }
            })
            
            const videoTrack = stream.getVideoTracks()[0]
            processor = new MediaStreamTrackProcessor({ track: videoTrack })
            
            encoder = new VideoEncoder({
                output: (chunk, metadata) => {
                    // 通过DataChannel发送编码后的数据
                    const dataChannel = store.ServerVideoPeer.dataChannels[0]
                    if (dataChannel && dataChannel.readyState === 'open') {
                        dataChannel.send(chunk.data)
                    }
                },
                error: (e) => console.error('Encoder error:', e)
            })
            
            encoder.configure({
                codec: 'avc1.640028', // H.264 High Profile
                width: 1080,
                height: 1920,
                bitrate: 5000000,
                framerate: 60,
            })
            
            const reader = processor.readable.getReader()
            async function processFrames() {
                while (true) {
                    const result = await reader.read()
                    if (result.done) break
                    
                    const frame = result.value
                    // 调整帧尺寸
                    const resizedFrame = new VideoFrame(frame, {
                        timestamp: frame.timestamp,
                        visibleRect: { x: 0, y: 0, width: 1080, height: 1920 }
                    })
                    
                    encoder.encode(resizedFrame)
                    frame.close()
                    resizedFrame.close()
                }
            }
            
            processFrames()
            
        } catch (e) {
            console.log(e)
        }
    }
}
```

### 客户端修改方案

客户端需要修改以接收和解码WebCodec编码的数据：

1. **移除ontrack监听**，改为通过DataChannel接收数据
2. **添加WebCodec解码器**，解码视频帧
3. **将解码后的帧绘制到Canvas**

```typescript
// 在control.vue中修改
onMounted(() => {
    // ... 其他代码保持不变
    const { channel, peer } = ClientVideoConnect(`http://${host_name}`, port, password)
    dataChannel = channel
    
    // 设置WebCodec解码器
    decoder = new VideoDecoder({
        output: (frame) => {
            // 将帧绘制到canvas
            const canvas = document.createElement('canvas')
            canvas.width = 1080
            canvas.height = 1920
            const ctx = canvas.getContext('2d')
            ctx.drawImage(frame, 0, 0)
            
            // 将canvas内容显示在video元素中
            video.value.srcObject = canvas.captureStream(60)
            frame.close()
        },
        error: (e) => console.error('Decoder error:', e)
    })
    
    decoder.configure({
        codec: 'avc1.640028',
        width: 1080,
        height: 1920,
    })
    
    channel.onopen = function () {
        console.log('data channel open success!')
    }
    
    // 接收编码后的数据
    channel.onmessage = (event) => {
        const chunk = new EncodedVideoChunk({
            type: 'key', // 或根据实际情况判断
            timestamp: performance.now(),
            data: event.data
        })
        decoder.decode(chunk)
    }
})
```

### 关键修改点

1. **服务端变量声明**：在server.vue中添加processor和encoder变量
2. **客户端变量声明**：在control.vue中添加decoder变量
3. **DataChannel使用**：确保ServerVideoPeer和ClientVideoPeer都有可用的DataChannel
4. **错误处理**：添加编码器/解码器的错误处理逻辑

## Notes

- MediaStreamTrackProcessor和WebCodec API是较新的Web API，需要确保浏览器支持
- 1080x1920是竖屏分辨率，确认是否符合需求
- 客户端需要修改，从WebRTC track接收改为WebCodec解码
- 需要处理编码器初始化、帧率控制和错误处理逻辑
- 考虑添加关键帧控制机制以确保客户端正确解码

Wiki pages you might want to explore:
- [Connection Lifecycle Management (feiiyuu/onemouse)](/wiki/feiiyuu/onemouse#5.3)
- [Screen Capture and Streaming (feiiyuu/onemouse)](/wiki/feiiyuu/onemouse#6.3)

### Citations

**File:** src/components/starter/server.vue (L81-88)
```vue
            const stream = await navigator.mediaDevices.getDisplayMedia({
                audio: audio,
                video: {
                    frameRate: Number.parseInt(settingStore.frameRate)
                }
            })
            streams = stream
            stream.getTracks().forEach(track => { send = store.ServerVideoPeer.addTrack(track, stream) })
```

**File:** src/pages/control.vue (L44-49)
```vue
function ontrack(ev: RTCTrackEvent) {
    video.value.srcObject = ev.streams[0]
    video.value.onloadedmetadata = () => {
        video.value.play();
    };
}
```







# 方案2

## 帧时间戳和队列控制解决方案

### 问题分析

`frame.timestamp` 来自原始捕获的 MediaStream，不能保证均匀的60fps帧率 [1](#2-0) 。需要自己生成均匀的时间戳来稳定帧率。

### 修改后的服务端实现

```typescript
// 在 server.vue 顶部添加变量
let processor: MediaStreamTrackProcessor | undefined
let encoder: VideoEncoder | undefined
let frameCount = 0
let startTime = performance.now()

async function CatchDesktopVideo(data: ScreenData) {
    const open = data.open
    const sourceId = await window.oneMouse.GetDeskTopId()
    if (!store.ServerVideoPeer) {
        ServerVideoConnect(`http://${connectLink.value}`, port.value, connectPassword.value, onmessage)
    }
    
    if (!open && processor) {
        processor.abort()
        encoder.close()
        processor = undefined
        encoder = undefined
        frameCount = 0
        return
    }
    
    const { frameRate, audio } = data
    if (frameRate) settingStore.updateFrameRate(frameRate)
    
    if (store.ServerVideoPeer && sourceId) {
        try {
            const stream = await navigator.mediaDevices.getDisplayMedia({
                audio: audio,
                video: {
                    frameRate: 60
                }
            })
            
            const videoTrack = stream.getVideoTracks()[0]
            processor = new MediaStreamTrackProcessor({ track: videoTrack })
            
            encoder = new VideoEncoder({
                output: (chunk, metadata) => {
                    const dataChannel = store.ServerVideoPeer.dataChannels[0]
                    if (dataChannel && dataChannel.readyState === 'open') {
                        dataChannel.send(chunk.data)
                    }
                },
                error: (e) => console.error('Encoder error:', e)
            })
            
            encoder.configure({
                codec: 'avc1.640028',
                width: 1920,
                height: 1080,
                bitrate: 5000000,
                framerate: 60,
            })
            
            const reader = processor.readable.getReader()
            startTime = performance.now()
            
            async function processFrames() {
                while (true) {
                    const result = await reader.read()
                    if (result.done) break
                    
                    // 检查编码队列大小，超过10则丢弃帧
                    if (encoder.encodeQueueSize >= 10) {
                        result.value.close()
                        continue
                    }
                    
                    const frame = result.value
                    
                    // 生成均匀的时间戳 (60fps = 16.67ms每帧)
                    const targetTimestamp = (frameCount * 1666667) / 1000 // 微秒
                    const resizedFrame = new VideoFrame(frame, {
                        timestamp: targetTimestamp,
                        visibleRect: { x: 0, y: 0, width: 1920, height: 1080 }
                    })
                    
                    encoder.encode(resizedFrame)
                    frame.close()
                    resizedFrame.close()
                    frameCount++
                }
            }
            
            processFrames()
            
        } catch (e) {
            console.log(e)
        }
    }
}
```

### 关键改进点

1. **均匀时间戳生成**：使用 `frameCount * 1666667 / 1000` 生成精确的60fps时间戳
2. **队列大小控制**：检查 `encoder.encodeQueueSize >= 10` 时直接丢弃帧
3. **分辨率修正**：改为1920*1080横屏分辨率
4. **资源管理**：在停止时重置 `frameCount`

## Notes

- `frame.timestamp` 来自浏览器捕获，不保证均匀间隔，需要自己生成
- `encodeQueueSize` 检查防止编码器过载，避免内存堆积
- 1666667微秒 = 16.67ms，对应60fps的帧间隔
- 客户端仍需相应修改以接收WebCodec编码数据

Wiki pages you might want to explore:
- [Settings Page (feiiyuu/onemouse)](/wiki/feiiyuu/onemouse#3.6)
- [Connection Lifecycle Management (feiiyuu/onemouse)](/wiki/feiiyuu/onemouse#5.3)

### Citations

**File:** src/components/starter/server.vue (L81-88)
```vue
            const stream = await navigator.mediaDevices.getDisplayMedia({
                audio: audio,
                video: {
                    frameRate: Number.parseInt(settingStore.frameRate)
                }
            })
            streams = stream
            stream.getTracks().forEach(track => { send = store.ServerVideoPeer.addTrack(track, stream) })
```

## 问题1：

## EncodedVideoChunk type字段和时间戳同步解决方案

### 问题分析

`EncodedVideoChunk`的`type`字段需要服务端和客户端配合，客户端不应使用`performance.now()`作为时间戳，而应使用服务端生成的均匀时间戳。

### 服务端修改方案

服务端需要发送帧类型信息给客户端：

```typescript
// 在server.vue的encoder.output回调中修改
encoder = new VideoEncoder({
    output: (chunk, metadata) => {
        const dataChannel = store.ServerVideoPeer.dataChannels[0]
        if (dataChannel && dataChannel.readyState === 'open') {
            // 发送包含类型和时间戳的数据
            const frameData = {
                type: chunk.type, // 'key' 或 'delta'
                timestamp: chunk.timestamp, // 使用服务端生成的均匀时间戳
                data: chunk.data
            }
            dataChannel.send(JSON.stringify(frameData))
        }
    },
    error: (e) => console.error('Encoder error:', e)
})
```

### 客户端修改方案

客户端需要解析服务端发送的数据并使用正确的时间戳：

```typescript
// 在control.vue中修改channel.onmessage
channel.onmessage = (event) => {
    const frameData = JSON.parse(event.data)
    const chunk = new EncodedVideoChunk({
        type: frameData.type, // 使用服务端发送的类型
        timestamp: frameData.timestamp, // 使用服务端的时间戳
        data: new Uint8Array(frameData.data) // 转换回Uint8Array
    })
    decoder.decode(chunk)
}
```

### 关键改进点

1. **帧类型传递**：服务端通过`chunk.type`获取帧类型（'key'或'delta'）并发送给客户端
2. **时间戳同步**：客户端使用服务端生成的`frameCount * 1666667 / 1000`时间戳，确保60fps均匀性
3. **数据格式**：使用JSON包装帧信息，包含type、timestamp和data
4. **数据转换**：客户端需要将JSON中的data转换回`Uint8Array`

## Notes

- `EncodedVideoChunk.type`自动由编码器生成，关键帧为'key'，增量帧为'delta'
- 服务端的时间戳生成确保了60fps的均匀性，客户端必须使用这个时间戳
- JSON序列化会有性能开销，生产环境可考虑使用二进制协议
- 客户端解码器配置需要与服务端编码器保持一致

Wiki pages you might want to explore:
- [Screen Capture and Streaming (feiiyuu/onemouse)](/wiki/feiiyuu/onemouse#6.3)
- [Connection Lifecycle Management (feiiyuu/onemouse)](/wiki/feiiyuu/onemouse#5.3)

Wiki pages you might want to explore:
- [WebRTC Architecture (feiiyuu/onemouse)](/wiki/feiiyuu/onemouse#2.3)
- [Connection Lifecycle Management (feiiyuu/onemouse)](/wiki/feiiyuu/onemouse#5.3)
- [Screen Capture and Streaming (feiiyuu/onemouse)](/wiki/feiiyuu/onemouse#6.3)





## 问题2：

## WebCodec编解码器配置详解

### VideoEncoder配置

`VideoEncoder`构造函数接受一个配置对象，包含`output`和`error`两个回调函数：

```typescript
const encoder = new VideoEncoder({
    output: (chunk: EncodedVideoChunk, metadata: EncodedVideoChunkMetadata) => {
        // 处理编码后的数据
    },
    error: (e: DOMException) => {
        // 处理编码错误
    }
})
```

**output回调参数：**
- `chunk: EncodedVideoChunk` - 编码后的视频数据块，包含type('key'/'delta')、timestamp和data
- `metadata: EncodedVideoChunkMetadata` - 编码元数据（如关键帧信息）

**error回调参数：**
- `e: DOMException` - 编码过程中发生的错误

### VideoDecoder配置

`VideoDecoder`构造函数同样接受`output`和`error`回调：

```typescript
const decoder = new VideoDecoder({
    output: (frame: VideoFrame) => {
        // 处理解码后的视频帧
    },
    error: (e: DOMException) => {
        // 处理解码错误
    }
})
```

**output回调参数：**
- `frame: VideoFrame` - 解码后的视频帧，包含像素数据和时间戳

**error回调参数：**
- `e: DOMException` - 解码过程中发生的错误

### 实际应用示例

在服务端的`CatchDesktopVideo`函数中，`output`回调用于发送编码数据 [1](#4-0) ：

```typescript
encoder = new VideoEncoder({
    output: (chunk, metadata) => {
        const dataChannel = store.ServerVideoPeer.dataChannels[0]
        if (dataChannel && dataChannel.readyState === 'open') {
            dataChannel.send(chunk.data)
        }
    },
    error: (e) => console.error('Encoder error:', e)
})
```

在客户端，`output`回调用于渲染解码后的帧：

```typescript
decoder = new VideoDecoder({
    output: (frame) => {
        const canvas = document.createElement('canvas')
        const ctx = canvas.getContext('2d')
        ctx.drawImage(frame, 0, 0)
        video.value.srcObject = canvas.captureStream(60)
        frame.close()
    },
    error: (e) => console.error('Decoder error:', e)
})
```

## Notes

- `output`回调在每次编码/解码完成时被调用
- `error`回调在编解码失败时被调用，用于错误处理
- VideoFrame使用后需要调用`close()`释放资源
- EncodedVideoChunk的type字段自动由编码器生成，无需手动设置

Wiki pages you might want to explore:
- [Connection Lifecycle Management (feiiyuu/onemouse)](/wiki/feiiyuu/onemouse#5.3)
- [Screen Capture and Streaming (feiiyuu/onemouse)](/wiki/feiiyuu/onemouse#6.3)

### Citations

**File:** src/components/starter/server.vue (L81-88)
```vue
            const stream = await navigator.mediaDevices.getDisplayMedia({
                audio: audio,
                video: {
                    frameRate: Number.parseInt(settingStore.frameRate)
                }
            })
            streams = stream
            stream.getTracks().forEach(track => { send = store.ServerVideoPeer.addTrack(track, stream) })
```