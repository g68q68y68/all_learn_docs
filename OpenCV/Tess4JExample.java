import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Tess4JExample {
    
    public static void main(String[] args) {
        // 1. 设置Tesseract路径
        configureTess4J();
        
        // 2. 准备图像
        File imageFile = new File("sample.png");
        
        try {
            // 3. 执行OCR
            String result = recognizeText(imageFile);
            System.out.println("识别结果: " + result);
            
        } catch (Exception e) {
            System.err.println("OCR处理失败: " + e.getMessage());
        }
    }
    
    private static void configureTess4J() {
        // 设置tessdata目录路径 (项目内的相对路径)
        System.setProperty("tessdata.dir", "./tessdata");
        
        // 设置JNA库路径 (包含DLL的目录)
        System.setProperty("jna.library.path", "./lib/win32-x86-64");
    }
    
    private static String recognizeText(File imageFile) throws Exception {
        // 读取图像
        BufferedImage image = ImageIO.read(imageFile);
        
        // 创建Tesseract实例
        ITesseract tesseract = new Tesseract();
        
        // 设置语言数据路径 (自动使用系统属性)
        tesseract.setDatapath(System.getProperty("tessdata.dir"));
        
        // 设置识别语言
        tesseract.setLanguage("eng");
        
        // 执行OCR
        return tesseract.doOCR(image);
    }
}
