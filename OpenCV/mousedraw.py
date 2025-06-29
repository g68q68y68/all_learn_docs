# 2. 交互式绘图示例
# -----------------
import cv2
import numpy as np

# 全局变量
shape_mode = 'line'  # 当前绘制模式: 'line', 'circle', 'rectangle'
start_point = None  # 鼠标按下时的起始点


def draw_shape(event, x, y, flags, param):
    global start_point, shape_mode, img

    if event == cv2.EVENT_LBUTTONDOWN:
        # 记录按下点
        start_point = (x, y)

    elif event == cv2.EVENT_LBUTTONUP and start_point is not None:
        # 记录抬起点
        end_point = (x, y)
        # 根据当前模式绘制图形
        if shape_mode == 'line':
            cv2.line(img, start_point, end_point, (0, 255, 0), 2)
        elif shape_mode == 'circle':
            # 半径为起点到终点的距离
            radius = int(np.hypot(end_point[0] - start_point[0],
                                  end_point[1] - start_point[1]))
            cv2.circle(img, start_point, radius, (255, 0, 0), 2)
        elif shape_mode == 'rectangle':
            cv2.rectangle(img, start_point, end_point, (0, 0, 255), 2)
        # 重置起始点
        start_point = None


# 创建白色画布
width, height = 800, 600
img = np.ones((height, width, 3), dtype=np.uint8) * 255

# 创建窗口并绑定鼠标回调
cv2.namedWindow('Draw')
cv2.setMouseCallback('Draw', draw_shape)

print('按 \'l\' 切换到直线模式，\'c\' 切换到圆形模式，\'r\' 切换到矩形模式，\'ESC\' 退出。')

while True:
    cv2.imshow('Draw', img)
    key = cv2.waitKey(1) & 0xFF
    if key == 27:  # ESC 键
        break
    elif key == ord('l'):
        shape_mode = 'line'
        print('模式: 直线')
    elif key == ord('c'):
        shape_mode = 'circle'
        print('模式: 圆形')
    elif key == ord('r'):
        shape_mode = 'rectangle'
        print('模式: 矩形')

cv2.destroyAllWindows()
