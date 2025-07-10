package com.se2303.notificationservice.service;

import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.CreateMusicRequest;
import com.se2303.notificationservice.dto.MusicResponse;
import com.se2303.notificationservice.model.Music;
import com.se2303.notificationservice.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicService {
    
    @Autowired
    private MusicRepository musicRepository;
    
    /**
     * 创建音乐
     */
    public ApiResponse createMusic(CreateMusicRequest request) {
        try {
            Music music = new Music(
                request.getTitle(),
                request.getCategory(),
                request.getCover(),
                request.getUrl(),
                request.getDuration()
            );
            
            Music savedMusic = musicRepository.save(music);
            MusicResponse response = convertToResponse(savedMusic);
            
            return ApiResponse.success(response, "音乐创建成功");
        } catch (Exception e) {
            return ApiResponse.error("创建音乐失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取音乐列表
     */
    public ApiResponse getMusicList(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Music> musicPage = musicRepository.findAllByOrderByCreatedAtDesc(pageable);
            
            List<MusicResponse> responses = musicPage.getContent().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responses, "获取音乐列表成功");
        } catch (Exception e) {
            return ApiResponse.error("获取音乐列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据关键词搜索音乐
     */
    public ApiResponse searchMusic(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ApiResponse.error("搜索关键词不能为空");
            }
            
            List<Music> musics = musicRepository.findByKeyword(keyword.trim());
            List<MusicResponse> responses = musics.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responses, "搜索音乐成功");
        } catch (Exception e) {
            return ApiResponse.error("搜索音乐失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据分类获取音乐
     */
    public ApiResponse getMusicByCategory(String category) {
        try {
            if (category == null || category.trim().isEmpty()) {
                return ApiResponse.error("分类不能为空");
            }
            
            List<Music> musics = musicRepository.findByCategoryOrderByCreatedAtDesc(category.trim());
            List<MusicResponse> responses = musics.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responses, "获取分类音乐成功");
        } catch (Exception e) {
            return ApiResponse.error("获取分类音乐失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有音乐
     */
    public ApiResponse getAllMusic() {
        try {
            List<Music> musics = musicRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 100)).getContent();
            List<MusicResponse> responses = musics.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responses, "获取所有音乐成功");
        } catch (Exception e) {
            return ApiResponse.error("获取所有音乐失败: " + e.getMessage());
        }
    }
    
    /**
     * 将Music实体转换为MusicResponse
     */
    private MusicResponse convertToResponse(Music music) {
        return new MusicResponse(
                music.getId(),
                music.getTitle(),
                music.getCategory(),
                music.getCover(),
                music.getUrl(),
                music.getDuration(),
                music.getCreatedAt()
        );
    }
} 