package com.nanfeng.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final Path basePath = Paths.get("./data"); // 存储文件的根目录

    /**
     * 通用文件接口，根据 query 参数 decide 是否下载或 inline 预览
     *
     * @param filename  要访问的文件名
     * @param inline    如果为 true，则 Content-Disposition 为 inline，否则 attachment
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String filename,
            @RequestParam(defaultValue = "false") boolean inline) throws IOException {

        Path file = basePath.resolve(filename).normalize();
        if (!Files.exists(file) || Files.isDirectory(file)) {
            return ResponseEntity.notFound().build();
        }

        // 根据后缀猜测 MIME，
        String contentType = Files.probeContentType(file);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        Resource resource = new UrlResource(file.toUri());
        String disposition = (inline ? "inline" : "attachment") +
                             "; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .body(resource);
    }

    /**
     * 下载或内联预览文件
     * @param filename 客户端请求的文件名
     * @param inline   true → inline; false → attachment
     */
    @GetMapping("/down/{filename:.+}")
    public void serveFile(
            @PathVariable String filename,
            @RequestParam(defaultValue = "false") boolean inline,
            HttpServletResponse response) throws IOException {

        Path file = basePath.resolve(filename).normalize();
        if (!Files.exists(file) || Files.isDirectory(file)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 检测并设置缓存头
        FileTime lastMod = Files.getLastModifiedTime(file);
        response.setHeader("Last-Modified",
                DateTimeFormatter.RFC_1123_DATE_TIME
                        .withZone(ZoneId.of("GMT"))
                        .format(lastMod.toInstant()));
        response.setHeader("Cache-Control", "max-age=3600"); // 1 小时内走缓存

        // 浏览器带 If-Modified-Since 时自动返回 304
        String ifModSince = response.getHeader("If-Modified-Since");
        // （Spring MVC 会在底层自动处理 If-Modified-Since → 304）
        // 若需手动判断，可比较 lastMod 与 ifModSince 时间，调用 response.setStatus(304) 并 return

        // 设置 MIME 类型
        String contentType = Files.probeContentType(file);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        response.setContentType(contentType);

        // 设置 Content-Disposition
        String disposition = (inline ? "inline" : "attachment")
                + "; filename=\"" + file.getFileName() + "\"";
        response.setHeader("Content-Disposition", disposition);

        // 写出文件流
        response.setContentLengthLong(Files.size(file));
        try (InputStream in = Files.newInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        }
    }
}
