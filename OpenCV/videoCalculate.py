# video_stutter_analyzer.py
#
# 这是一个使用 Python 和 OpenCV 实现的视频卡顿检测脚本，包含详细注释。
# 功能：
# 1. 读取视频文件，并在用户指定的 ROI（感兴趣区域）和时间段内提取每帧
# 2. 将每帧裁剪并转换为灰度图，计算相邻帧的帧差并二值化，过滤掉重复帧
# 3. 统计每秒的有效帧数（FPS），根据帧间隔和FPS阈值检测卡顿次数和时长

import cv2  # OpenCV 库，用于视频读写和图像处理
import sys  # 用于获取命令行参数
import os  # 用于文件路径和工作目录操作
from collections import defaultdict  # 用于统计每秒帧数


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
    从视频中提取灰度帧：
    - 打开视频文件
    - 获取视频帧率 (FPS) 并计算起始/结束帧号
    - 跳转到起始帧，按顺序读取到结束帧
    - 对每帧：裁剪 ROI 区域 -> 转为灰度图 -> 记录时间戳和灰度图

    参数：
      video_path: 视频文件路径
      roi: (x, y, w, h) 代表感兴趣区域
      start_sec: 开始时间 (秒)
      end_sec: 结束时间 (秒)
    返回：
      list of (timestamp_ms, gray_frame)
    """
    cap = cv2.VideoCapture(video_path)
    # 获取cap视频的各种信息
    print(f"视频信息: {cap.get(cv2.CAP_PROP_FRAME_WIDTH)}x{cap.get(cv2.CAP_PROP_FRAME_HEIGHT)} @ {cap.get(cv2.CAP_PROP_FPS)}FPS")
    # 检查视频是否成功打开
    if not cap.isOpened():
        raise FileNotFoundError(f"无法打开视频文件: {video_path}")

    # 获取视频帧率 (帧/秒)
    fps = cap.get(cv2.CAP_PROP_FPS)
    # 计算对应的帧编号
    start_frame = int(start_sec * fps)
    end_frame = int(end_sec * fps)
    # 设置视频读取到 start_frame
    cap.set(cv2.CAP_PROP_POS_FRAMES, start_frame)

    frames = []  # 存放提取的 (timestamp_ms, gray_frame)
    while True:
        ret, frame = cap.read()  # 读取一帧
        if not ret:
            # 读取失败，结束循环
            break
        idx = int(cap.get(cv2.CAP_PROP_POS_FRAMES))
        if idx > end_frame:
            # 已超出结束帧，退出
            break
        # 获取当前帧的时间戳（毫秒）
        ts = cap.get(cv2.CAP_PROP_POS_MSEC)
        # 裁剪出感兴趣区域 (ROI)
        x, y, w, h = roi
        crop = frame[y:y + h, x:x + w]
        # 将裁剪的 BGR 图转换为灰度图
        gray = cv2.cvtColor(crop, cv2.COLOR_BGR2GRAY)
        # 保存时间戳和灰度图
        frames.append((ts, gray))

    cap.release()  # 释放视频对象
    return frames


def filter_by_frame_diff(frames, thresh):
    """
    使用帧差法过滤重复帧：
    - 遍历相邻帧对，计算灰度图差分
    - 差分图二值化（阈值 thresh）
    - 统计二值图中的非零像素数；若>0则表示有运动，保留该帧

    参数：
      frames: list of (timestamp_ms, gray_frame)
      thresh: 二值化阈值 (0-255)
    返回：
      有效帧列表 list of (timestamp_ms, gray_frame)
    """
    valid = []  # 用于保存有效帧
    prev_gray = None
    for ts, gray in frames:
        if prev_gray is None:
            # 第一帧没有前帧比较，默认保留
            valid.append((ts, gray))
        else:
            # 计算当前帧与前一帧的像素绝对差分
            diff = cv2.absdiff(gray, prev_gray)
            # 二值化：diff>thresh 为255，否则为0，得到运动掩码
            _, mask = cv2.threshold(diff, thresh, 255, cv2.THRESH_BINARY)
            # 统计掩码中白色像素（运动像素）数量
            non_zero = cv2.countNonZero(mask)
            if non_zero > 0:
                # 只要有一个像素运动，就认为该帧有效
                valid.append((ts, gray))
        # 更新 prev_gray，用于下一次循环比较
        prev_gray = gray
    return valid


def compute_fps(valid_frames):
    """
    统计每秒的有效帧数：
    - 根据时间戳将帧分配到对应秒
    - 使用字典统计

    参数：
      valid_frames: list of (timestamp_ms, gray_frame)
    返回：
      fps_map: dict {sec: frame_count}
    """
    fps_map = defaultdict(int)
    for ts, _ in valid_frames:
        sec = int(ts // 1000)  # 毫秒转秒并取整
        fps_map[sec] += 1
    return fps_map


def analyze_stutter(valid_frames, fps_map):
    """
    根据帧间隔和每秒的FPS检测卡顿：
    - 若相邻两帧时间间隔 >=200ms 或者该秒 FPS<25，计为一次卡顿
    - 累加卡顿次数和卡顿时长

    参数：
      valid_frames: list of (timestamp_ms, gray_frame)
      fps_map: dict {sec: frame_count}
    输出：
      打印有效帧数、总时长、平均FPS、卡顿次数和卡顿时长
    """
    stutter_count = 0
    stutter_duration = 0.0

    # 1. 遍历相邻帧检测帧间隔
    for i in range(1, len(valid_frames)):
        ts_curr, _ = valid_frames[i]
        ts_prev, _ = valid_frames[i - 1]
        delta = ts_curr - ts_prev  # 毫秒差值
        sec = int(ts_curr // 1000)
        fps = fps_map.get(sec, 0)
        # 条件1：帧间隔过大；条件2：该秒FPS过低
        if delta >= 200 or fps < 25:
            stutter_count += 1
            # 仅当FPS正常但间隔大时，累加间隔时长
            if fps >= 25 and delta >= 200:
                stutter_duration += delta

    # 2. 对于FPS<25的每一秒，直接累加1000ms
    for sec, count in fps_map.items():
        if count < 25:
            stutter_duration += 1000.0

    # 计算总时长（秒）和平均FPS
    total_time = (valid_frames[-1][0] - valid_frames[0][0]) / 1000.0
    avg_fps = len(valid_frames) / total_time if total_time > 0 else 0

    # 输出结果
    print(f"有效帧: {len(valid_frames)}, 时长: {total_time:.2f}s, 平均FPS: {avg_fps:.2f}")
    print(f"卡顿次数: {stutter_count}, 卡顿时长: {stutter_duration:.0f}ms")


def main():
    # 加载视频路径并打印调试信息
    video_path = load_video_path()
    print(f"Working dir: {os.getcwd()}")
    print(f"Video path: {video_path}, exists: {os.path.exists(video_path)}")

    # 参数定义：ROI、起止时间、帧差阈值
    roi = (300, 300, 400, 400)  # (x, y, width, height)
    start_sec, end_sec = 0, 120
    diff_thresh = 3

    # 提取并处理帧
    frames = extract_frames(video_path, roi, start_sec, end_sec)
    if not frames:
        print("未提取到任何视频帧，请检查文件和时间范围。")
        return

    valid = filter_by_frame_diff(frames, diff_thresh)
    fps_map = compute_fps(valid)
    analyze_stutter(valid, fps_map)


if __name__ == '__main__':
    main()
