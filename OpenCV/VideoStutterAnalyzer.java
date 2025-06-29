import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这是一个使用 Java 和 OpenCV 实现的视频卡顿检测脚本，包含详细注释。
 * 功能：
 * 1. 读取视频文件，并在用户指定的 ROI（感兴趣区域）和时间段内提取每帧
 * 2. 将每帧裁剪并转换为灰度图，计算相邻帧的帧差并二值化，过滤掉重复帧
 * 3. 统计每秒的有效帧数（FPS），根据帧间隔和FPS阈值检测卡顿次数和时长
 */
public class VideoStutterAnalyzer {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    /**
     * 获取视频路径：
     * - 如果传入命令行参数，则使用第一个参数
     * - 否则使用默认路径
     */
    private static String loadVideoPath(String[] args) {
        if (args.length > 0) {
            return args[0];
        }
        return "/Users/ganqingyao/Movies/Videos/m3u8/1713602483668.mp4";
    }

    /**
     * 提取灰度帧并附带时间戳
     */
    private static List<FrameData> extractFrames(
            String videoPath, Rect roi, double startSec, double endSec) {
        VideoCapture cap = new VideoCapture(videoPath);
        if (!cap.isOpened()) {
            throw new RuntimeException("无法打开视频文件: " + videoPath);
        }

        double fps = cap.get(VideoCapture.CAP_PROP_FPS);
        int startFrame = (int)(startSec * fps);
        int endFrame = (int)(endSec * fps);
        cap.set(VideoCapture.CAP_PROP_POS_FRAMES, startFrame);

        List<FrameData> frames = new ArrayList<>();
        Mat frame = new Mat();
        while (cap.read(frame)) {
            int idx = (int)cap.get(VideoCapture.CAP_PROP_POS_FRAMES);
            if (idx > endFrame) break;
            double ts = cap.get(VideoCapture.CAP_PROP_POS_MSEC);
            // 裁剪ROI
            Mat crop = new Mat(frame, roi);
            // 转灰度
            Mat gray = new Mat();
            Imgproc.cvtColor(crop, gray, Imgproc.COLOR_BGR2GRAY);
            frames.add(new FrameData(ts, gray));
        }
        cap.release();
        return frames;
    }

    /**
     * 使用帧差法过滤无运动帧
     */
    private static List<FrameData> filterByFrameDiff(
            List<FrameData> frames, double thresh) {
        List<FrameData> valid = new ArrayList<>();
        Mat prevGray = null;
        for (FrameData fd : frames) {
            if (prevGray == null) {
                valid.add(fd);
            } else {
                Mat diff = new Mat();
                Core.absdiff(fd.gray, prevGray, diff);
                Mat mask = new Mat();
                Imgproc.threshold(diff, mask, thresh, 255, Imgproc.THRESH_BINARY);
                int nonZero = Core.countNonZero(mask);
                if (nonZero > 0) {
                    valid.add(fd);
                }
            }
            prevGray = fd.gray;
        }
        return valid;
    }

    /**
     * 统计每秒有效帧数
     */
    private static Map<Integer, Integer> computeFps(List<FrameData> valid) {
        Map<Integer, Integer> fpsMap = new HashMap<>();
        for (FrameData fd : valid) {
            int sec = (int)(fd.timestamp / 1000);
            fpsMap.put(sec, fpsMap.getOrDefault(sec, 0) + 1);
        }
        return fpsMap;
    }

    /**
     * 分析卡顿
     */
    private static void analyzeStutter(
            List<FrameData> valid, Map<Integer, Integer> fpsMap) {
        int stutterCount = 0;
        double stutterDuration = 0;

        // 相邻帧间隔 & FPS阈值
        for (int i = 1; i < valid.size(); i++) {
            double currTs = valid.get(i).timestamp;
            double prevTs = valid.get(i-1).timestamp;
            double delta = currTs - prevTs;
            int sec = (int)(currTs / 1000);
            int fps = fpsMap.getOrDefault(sec, 0);
            if (delta >= 200 || fps < 25) {
                stutterCount++;
                if (fps >= 25 && delta >= 200) {
                    stutterDuration += delta;
                }
            }
        }
        // FPS<25 的秒
        for (Map.Entry<Integer, Integer> e : fpsMap.entrySet()) {
            if (e.getValue() < 25) {
                stutterDuration += 1000;
            }
        }
        double totalTime = (valid.get(valid.size()-1).timestamp - valid.get(0).timestamp) / 1000.0;
        double avgFps = valid.size() / totalTime;

        System.out.printf("有效帧: %d, 时长: %.2fs, 平均FPS: %.2f\n",
                valid.size(), totalTime, avgFps);
        System.out.printf("卡顿次数: %d, 卡顿时长: %.0fms\n",
                stutterCount, stutterDuration);
    }

    public static void main(String[] args) {
        String videoPath = loadVideoPath(args);
        System.out.println("Video path: " + videoPath);

        // ROI 和参数
        Rect roi = new Rect(300, 300, 400, 400);
        double startSec = 0, endSec = 120;
        double diffThresh = 3;

        List<FrameData> frames = extractFrames(videoPath, roi, startSec, endSec);
        if (frames.isEmpty()) {
            System.err.println("未提取到任何视频帧，请检查路径和时间范围。");
            return;
        }
        List<FrameData> valid = filterByFrameDiff(frames, diffThresh);
        Map<Integer, Integer> fpsMap = computeFps(valid);
        analyzeStutter(valid, fpsMap);
    }

    /**
     * 帧数据容器：包含时间戳和灰度图
     */
    private static class FrameData {
        double timestamp;
        Mat gray;
        FrameData(double ts, Mat g) { this.timestamp = ts; this.gray = g; }
    }
}
