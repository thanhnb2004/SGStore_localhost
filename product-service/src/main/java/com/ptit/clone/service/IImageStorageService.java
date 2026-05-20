package com.ptit.clone.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageStorageService {

    /** Lưu 1 file vào thư mục upload, trả về URL public để truy cập ảnh. */
    String store(MultipartFile file);

    /** Lưu nhiều file, trả về danh sách URL public theo đúng thứ tự. */
    List<String> storeAll(List<MultipartFile> files);

    Resource loadAsResource(String fileName);

    void delete(String fileName);

    /** Xóa ảnh từ 1 URL public (ngược lại với store). */
    void deleteByUrl(String url);
}
