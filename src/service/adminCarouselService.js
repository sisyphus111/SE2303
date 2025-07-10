import { get, post, put, del } from './common';

export const getCarouselListByIdAsc = async (params = { page: 1, size: 10 }) => {
  const query = new URLSearchParams(params).toString();
  return await get(`/notification/carousel/listByIdAsc?${query}`);
};

export const deleteCarousel = async (carouselId) => {
  return await del(`/notification/carousel/${carouselId}`);
};

export const updateCarousel = async (carouselId, data) => {
  return await put(`/notification/carousel/${carouselId}`, data);
};

export const createCarousel = async (data) => {
  return await post('/notification/carousel/create', data);
}; 