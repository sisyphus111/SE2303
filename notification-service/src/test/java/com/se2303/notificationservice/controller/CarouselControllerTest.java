package com.se2303.notificationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.CreateCarouselImageRequest;
import com.se2303.notificationservice.dto.UpdateCarouselImageRequest;
import com.se2303.notificationservice.service.CarouselService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarouselController.class)
public class CarouselControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private CarouselService carouselService;

    @InjectMocks
    private CarouselController carouselController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(carouselController).build();
    }

    @Test
    public void testCreateCarouselImageSuccess() throws Exception {
        CreateCarouselImageRequest req = new CreateCarouselImageRequest();
        req.setTitle("test");
        req.setImageUrl("http://test.com/img.jpg"); // 必须设置
        when(carouselService.createCarouselImage(any(CreateCarouselImageRequest.class)))
                .thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(post("/api/notification/carousel/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testCreateCarouselImageFail() throws Exception {
        CreateCarouselImageRequest req = new CreateCarouselImageRequest();
        req.setTitle("test");
        req.setImageUrl("http://test.com/img.jpg"); // 必须设置
        when(carouselService.createCarouselImage(any(CreateCarouselImageRequest.class)))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/notification/carousel/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testCreateCarouselImageValidationFail() throws Exception {
        CreateCarouselImageRequest req = new CreateCarouselImageRequest();
        // 不设置 title
        mockMvc.perform(post("/api/notification/carousel/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetCarouselList() throws Exception {
        when(carouselService.getCarouselImages(any())).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/notification/carousel/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetAllCarouselImages() throws Exception {
        when(carouselService.getAllCarouselImages()).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/notification/carousel/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetCarouselListByIdAsc() throws Exception {
        when(carouselService.getCarouselListByIdAsc(eq(1), eq(10))).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/notification/carousel/listByIdAsc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testDeleteCarouselImageSuccess() throws Exception {
        when(carouselService.deleteCarouselImage(eq(1L))).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(delete("/api/notification/carousel/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testDeleteCarouselImageFail() throws Exception {
        when(carouselService.deleteCarouselImage(eq(1L))).thenReturn(ApiResponse.error("fail"));
        mockMvc.perform(delete("/api/notification/carousel/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testDeleteCarouselImageException() throws Exception {
        when(carouselService.deleteCarouselImage(eq(1L))).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(delete("/api/notification/carousel/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testUpdateCarouselImageSuccess() throws Exception {
        UpdateCarouselImageRequest req = new UpdateCarouselImageRequest();
        req.setTitle("test");
        when(carouselService.updateCarouselImage(eq(1L), any(UpdateCarouselImageRequest.class)))
                .thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(put("/api/notification/carousel/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testUpdateCarouselImageFail() throws Exception {
        UpdateCarouselImageRequest req = new UpdateCarouselImageRequest();
        req.setTitle("test");
        when(carouselService.updateCarouselImage(eq(1L), any(UpdateCarouselImageRequest.class)))
                .thenReturn(ApiResponse.error("fail"));
        mockMvc.perform(put("/api/notification/carousel/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testUpdateCarouselImageException() throws Exception {
        UpdateCarouselImageRequest req = new UpdateCarouselImageRequest();
        req.setTitle("test");
        when(carouselService.updateCarouselImage(eq(1L), any(UpdateCarouselImageRequest.class)))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(put("/api/notification/carousel/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }
} 