package com.wini.leader_board_integration.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by kamal on 1/23/2019.
 */
public interface StorageService {
    String store(MultipartFile file);
}
