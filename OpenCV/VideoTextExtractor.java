import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class VideoTextExtractor {

    // 加载OpenCV库
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        // 视频文件路径
        String videoPath = "input_video.mp4";
        // 输出Excel文件路径
        String excelPath = "output_results.xlsx";
        // 要识别的区域 (x, y, width, height)
        Rect textRegion = new Rect(100, 100, 200, 50);
        
        // 处理视频并提取文字
        List<FrameResult> results = processVideo(videoPath, textRegion);
        
        // 导出结果到Excel
        exportToExcel(results, excelPath);
        
        System.out.println("处理完成！结果已保存到: " + excelPath);
    }

    /**
     * 处理视频文件，提取特定区域文字
     * @param videoPath 视频文件路径
     * @param textRegion 要识别的文字区域
     * @return 包含时间戳和识别结果的列表
     */
    private static List<FrameResult> processVideo(String videoPath, Rect textRegion) {
        List<FrameResult> results = new ArrayList<>();
        
        // 创建视频捕获对象
        VideoCapture capture = new VideoCapture(videoPath);
        
        // 检查视频是否成功打开
        if (!capture.isOpened()) {
            System.err.println("无法打开视频文件: " + videoPath);
            return results;
        }
        
        // 获取视频的帧率
        double fps = capture.get(VideoCapture.CAP_PROP_FPS);
        if (fps <= 0) fps = 30; // 默认帧率
        
        // 每帧间隔(秒)
        double frameInterval = 1.0; // 每秒处理一次
        int frameSkip = (int) (fps * frameInterval);
        
        int frameCount = 0;
        Mat frame = new Mat();
        
        // 预加载Tesseract OCR
        TesseractOCR ocr = new TesseractOCR();
        
        // 逐帧读取视频
        while (capture.read(frame)) {
            frameCount++;
            
            // 如果不是每秒的最后一帧，跳过
            if (frameCount % frameSkip != 0) {
                continue;
            }
            
            // 计算当前时间(秒)
            double currentTime = frameCount / fps;
            
            // 提取指定区域
            Mat roi = new Mat(frame, textRegion);
            
            // 预处理图像以提高OCR识别率
            Mat processed = preprocessImage(roi);
            
            // 使用OCR识别文字
            String recognizedText = ocr.recognizeText(processed);
            
            // 提取数字部分 (如从"5ms"中提取"5")
            String delayValue = extractDelayValue(recognizedText);
            
            // 添加到结果列表
            results.add(new FrameResult(currentTime, delayValue));
            
            // 显示进度
            System.out.printf("时间: %.1fs, 识别结果: %s -> %sms%n", 
                currentTime, recognizedText, delayValue);
        }
        
        // 释放资源
        capture.release();
        return results;
    }
    
    /**
     * 图像预处理 - 提高OCR识别率
     * @param input 输入图像
     * @return 处理后的图像
     */
    private static Mat preprocessImage(Mat input) {
        Mat gray = new Mat();
        Mat binary = new Mat();
        
        // 转为灰度图
        Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGR2GRAY);
        
        // 二值化
        Imgproc.threshold(gray, binary, 128, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        
        // 可选: 去噪
        // Imgproc.medianBlur(binary, binary, 3);
        
        return binary;
    }
    
    /**
     * 从识别文本中提取延迟数值 (如从"5ms"中提取"5")
     * @param text 识别到的文本
     * @return 提取的数字部分
     */
    private static String extractDelayValue(String text) {
        if (text == null || text.isEmpty()) {
            return "N/A";
        }
        
        // 提取数字部分 (匹配数字后跟"ms")
        String numericPart = text.replaceAll("[^0-9]", "").trim();
        return numericPart.isEmpty() ? "N/A" : numericPart;
    }
    
    /**
     * 将结果导出到Excel文件
     * @param results 识别结果列表
     * @param filePath 输出的Excel文件路径
     */
    private static void exportToExcel(List<FrameResult> results, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("结果");
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("时间 (s)");
            headerRow.createCell(1).setCellValue("时延 (ms)");
            
            // 填充数据
            int rowNum = 1;
            for (FrameResult result : results) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(result.getTime());
                row.createCell(1).setCellValue(result.getDelay());
            }
            
            // 自动调整列宽
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            
            // 写入文件
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            System.err.println("导出Excel时出错: " + e.getMessage());
        }
    }
    
    /**
     * 封装帧识别结果
     */
    private static class FrameResult {
        private final double time; // 时间(秒)
        private final String delay; // 时延
        
        public FrameResult(double time, String delay) {
            this.time = time;
            this.delay = delay;
        }
        
        public double getTime() {
            return time;
        }
        
        public String getDelay() {
            return delay;
        }
    }
    
    /**
     * Tesseract OCR 封装类
     */
    private static class TesseractOCR {
        // 注意: 实际使用时需要替换为适合你的Tesseract OCR接口
        
        public String recognizeText(Mat image) {
            // 这里应该是集成Tesseract OCR的实际代码
            // 以下是模拟实现，实际实现需要根据你使用的Tesseract Java接口
            
            // 实际使用时可以这样集成:
            /*
            try {
                // 将OpenCV Mat转换为BufferedImage
                BufferedImage bufferedImage = matToBufferedImage(image);
                
                // 创建Tesseract实例
                ITesseract instance = new Tesseract();
                
                // 设置语言数据路径 (如果使用训练数据)
                instance.setDatapath("tessdata");
                instance.setLanguage("eng"); // 使用英文识别
                
                // 执行OCR
                return instance.doOCR(bufferedImage);
            } catch (Exception e) {
                System.err.println("OCR识别出错: " + e.getMessage());
                return "";
            }
            */
            
            // 模拟实现 - 返回随机值用于演示
            return "N/A";
        }
        
        // 模拟方法 - 实际需要实现Mat到BufferedImage的转换
        private Object matToBufferedImage(Mat mat) {
            // 需要实现将OpenCV Mat转换为BufferedImage
            // 这里仅作为示例
            return null;
        }
    }
}
