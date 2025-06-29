# video_stutter_analyzer.py
#
# 这是一个使用 Python 和 OpenCV 实现的视频卡顿检测脚本，包含详细注释和灰度帧保存功能。
# 功能：
# 1. 读取视频文件，并在用户指定的 ROI（感兴趣区域）和时间段内提取每帧
# 2. 将每帧裁剪并转换为灰度图，保存到 project_root/data 目录
# 3. 计算相邻帧的帧差并二值化，过滤掉重复帧
# 4. 统计每秒的有效帧数（FPS），根据帧间隔和FPS阈值检测卡顿次数和时长

import cv2                 # OpenCV 库，用于视频读写和图像处理
import sys                 # 用于获取命令行参数
import os                  # 用于文件路径和工作目录操作
from collections import defaultdict  # 用于统计每秒帧数

# 项目根目录下的灰度帧保存目录
DATA_DIR = 'data'

if not os.path.exists(DATA_DIR):
    os.makedirs(DATA_DIR)


def load_video_path():
    """
    获取视频文件路径：
    - 如果命令行传入了参数，则使用第一个参数作为路径
    - 否则，默认在当前工作目录下查找 '开心消消乐.mp4'
    """
    if len(sys.argv) > 1:
        return sys.argv[1]
    return '/Users/ganqingyao/Movies/Videos/m3u8/1713602483668.mp4'


def extract_frames(video_path, roi, start_sec, end_sec):
    """
    从视频中提取灰度帧并保存：
    - 打开视频
    - 跳转到起始帧，读取至结束帧
    - 对每帧：裁剪 ROI -> 转灰度 -> 保存到 data 目录 -> 记录时间戳和灰度图

    返回：
      list of (timestamp_ms, gray_frame)
    """
    cap = cv2.VideoCapture(video_path)
    if not cap.isOpened():
        raise FileNotFoundError(f"无法打开视频文件: {video_path}")

    fps = cap.get(cv2.CAP_PROP_FPS)
    start_frame = int(start_sec * fps)
    end_frame   = int(end_sec * fps)
    cap.set(cv2.CAP_PROP_POS_FRAMES, start_frame)

    frames = []
    while True:
        ret, frame = cap.read()
        if not ret:
            break
        idx = int(cap.get(cv2.CAP_PROP_POS_FRAMES))
        if idx > end_frame:
            break
        ts = cap.get(cv2.CAP_PROP_POS_MSEC)
        x, y, w, h = roi
        crop = frame[y:y+h, x:x+w]
        gray = cv2.cvtColor(crop, cv2.COLOR_BGR2GRAY)
        # 保存灰度帧
        file_path = os.path.join(DATA_DIR, f"gray_{idx:06d}.png")
        cv2.imwrite(file_path, gray)
        frames.append((ts, gray))

    cap.release()
    return frames


def filter_by_frame_diff(frames, thresh):
    """
    使用帧差法过滤重复帧：
    - 遍历相邻帧对，计算灰度图差分
    - 差分图二值化 -> 运动掩码
    - 统计非零像素数；若>0保留
    """
    valid = []
    prev_gray = None
    for ts, gray in frames:
        if prev_gray is None:
            valid.append((ts, gray))
        else:
            diff = cv2.absdiff(gray, prev_gray)
            _, mask = cv2.threshold(diff, thresh, 255, cv2.THRESH_BINARY)
            non_zero = cv2.countNonZero(mask)
            if non_zero > 0:
                valid.append((ts, gray))
        prev_gray = gray
    return valid


def compute_fps(valid_frames):
    """
    统计每秒的有效帧数。"""
    fps_map = defaultdict(int)
    for ts, _ in valid_frames:
        sec = int(ts // 1000)
        fps_map[sec] += 1
    return fps_map


def analyze_stutter(valid_frames, fps_map):
    """
    根据帧间隔和FPS检测卡顿，输出结果。"""
    stutter_count = 0
    stutter_duration = 0.0
    for i in range(1, len(valid_frames)):
        ts_curr, _ = valid_frames[i]
        ts_prev, _ = valid_frames[i-1]
        delta = ts_curr - ts_prev
        sec = int(ts_curr // 1000)
        fps = fps_map.get(sec, 0)
        if delta >= 200 or fps < 25:
            stutter_count += 1
            if fps >= 25 and delta >= 200:
                stutter_duration += delta
    for sec, count in fps_map.items():
        if count < 25:
            stutter_duration += 1000.0
    total_time = (valid_frames[-1][0] - valid_frames[0][0]) / 1000.0
    avg_fps = len(valid_frames) / total_time if total_time > 0 else 0
    print(f"有效帧: {len(valid_frames)}, 时长: {total_time:.2f}s, 平均FPS: {avg_fps:.2f}")
    print(f"卡顿次数: {stutter_count}, 卡顿时长: {stutter_duration:.0f}ms")


def main():
    video_path = load_video_path()
    print(f"Working dir: {os.getcwd()}")
    print(f"Video path: {video_path}, exists: {os.path.exists(video_path)}")

    roi = (100, 50, 400, 300)
    start_sec, end_sec = 5.0, 60.0
    diff_thresh = 25

    frames = extract_frames(video_path, roi, start_sec, end_sec)
    if not frames:
        print("未提取到任何视频帧，请检查文件和时间范围。")
        return

    valid = filter_by_frame_diff(frames, diff_thresh)
    fps_map = compute_fps(valid)
    analyze_stutter(valid, fps_map)


if __name__ == '__main__':
    main()
