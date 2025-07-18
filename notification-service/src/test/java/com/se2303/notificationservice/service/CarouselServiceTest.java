package com.se2303.notificationservice.service;

import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.CreateCarouselImageRequest;
import com.se2303.notificationservice.dto.UpdateCarouselImageRequest;
import com.se2303.notificationservice.model.CarouselImage;
import com.se2303.notificationservice.repository.CarouselImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

public class CarouselServiceTest {
    @Mock
    private CarouselImageRepository carouselImageRepository;

    @InjectMocks
    private CarouselService carouselService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCarouselImageSuccess() {
        CreateCarouselImageRequest req = new CreateCarouselImageRequest();
        CarouselImage img = new CarouselImage();
        when(carouselImageRepository.save(any(CarouselImage.class))).thenReturn(img);
        ApiResponse resp = carouselService.createCarouselImage(req);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testCreateCarouselImageException() {
        CreateCarouselImageRequest req = new CreateCarouselImageRequest();
        when(carouselImageRepository.save(any(CarouselImage.class))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = carouselService.createCarouselImage(req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetCarouselListSuccess() {
        CarouselImage img = new CarouselImage();
        when(carouselImageRepository.findAllByOrderBySortOrderAsc()).thenReturn(Arrays.asList(img));
        ApiResponse resp = carouselService.getCarouselList();
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetCarouselListException() {
        when(carouselImageRepository.findAllByOrderBySortOrderAsc()).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = carouselService.getCarouselList();
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetCarouselImagesSuccess() {
        CarouselImage img = new CarouselImage();
        when(carouselImageRepository.findAllByOrderBySortOrderAsc()).thenReturn(Arrays.asList(img));
        ApiResponse resp = carouselService.getCarouselImages(null);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetCarouselImagesException() {
        when(carouselImageRepository.findAllByOrderBySortOrderAsc()).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = carouselService.getCarouselImages(null);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetAllCarouselImagesSuccess() {
        CarouselImage img = new CarouselImage();
        when(carouselImageRepository.findAllByOrderBySortOrderAsc()).thenReturn(Arrays.asList(img));
        ApiResponse resp = carouselService.getAllCarouselImages();
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetAllCarouselImagesException() {
        when(carouselImageRepository.findAllByOrderBySortOrderAsc()).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = carouselService.getAllCarouselImages();
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetCarouselListByIdAscSuccess() {
        CarouselImage img = new CarouselImage();
        Page<CarouselImage> page = new PageImpl<>(Collections.singletonList(img));
        when(carouselImageRepository.findAllByOrderByIdAsc(any(Pageable.class))).thenReturn(page);
        ApiResponse resp = carouselService.getCarouselListByIdAsc(1, 10);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetCarouselListByIdAscException() {
        when(carouselImageRepository.findAllByOrderByIdAsc(any(Pageable.class))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = carouselService.getCarouselListByIdAsc(1, 10);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testDeleteCarouselImageSuccess() {
        when(carouselImageRepository.existsById(eq(1L))).thenReturn(true);
        ApiResponse resp = carouselService.deleteCarouselImage(1L);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testDeleteCarouselImageIdNull() {
        ApiResponse resp = carouselService.deleteCarouselImage(null);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testDeleteCarouselImageNotFound() {
        when(carouselImageRepository.existsById(eq(1L))).thenReturn(false);
        ApiResponse resp = carouselService.deleteCarouselImage(1L);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testDeleteCarouselImageException() {
        when(carouselImageRepository.existsById(eq(1L))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = carouselService.deleteCarouselImage(1L);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testUpdateCarouselImageSuccess() {
        UpdateCarouselImageRequest req = new UpdateCarouselImageRequest();
        CarouselImage img = new CarouselImage();
        when(carouselImageRepository.existsById(eq(1L))).thenReturn(true);
        when(carouselImageRepository.findById(eq(1L))).thenReturn(Optional.of(img));
        when(carouselImageRepository.save(any(CarouselImage.class))).thenReturn(img);
        ApiResponse resp = carouselService.updateCarouselImage(1L, req);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testUpdateCarouselImageIdNull() {
        UpdateCarouselImageRequest req = new UpdateCarouselImageRequest();
        ApiResponse resp = carouselService.updateCarouselImage(null, req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testUpdateCarouselImageNotFound() {
        UpdateCarouselImageRequest req = new UpdateCarouselImageRequest();
        when(carouselImageRepository.existsById(eq(1L))).thenReturn(false);
        ApiResponse resp = carouselService.updateCarouselImage(1L, req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testUpdateCarouselImageFindNull() {
        UpdateCarouselImageRequest req = new UpdateCarouselImageRequest();
        when(carouselImageRepository.existsById(eq(1L))).thenReturn(true);
        when(carouselImageRepository.findById(eq(1L))).thenReturn(Optional.empty());
        ApiResponse resp = carouselService.updateCarouselImage(1L, req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testUpdateCarouselImageException() {
        UpdateCarouselImageRequest req = new UpdateCarouselImageRequest();
        when(carouselImageRepository.existsById(eq(1L))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = carouselService.updateCarouselImage(1L, req);
        assertFalse(resp.isSuccess());
    }
} 