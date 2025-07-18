import React, { useState, useEffect } from 'react';
import { Card, Button, Tabs, Typography, message, Form } from 'antd';
import { PlusOutlined, BookOutlined, SoundOutlined } from '@ant-design/icons';
import AdminArticleTable from '../components/AdminArticleTable';
import AdminMusicTable from '../components/AdminMusicTable';
import AdminArticleModal from '../components/AdminArticleModal';
import AdminMusicModal from '../components/AdminMusicModal';
import AdminCarouselTable from '../components/AdminCarouselTable';
import AdminCarouselModal from '../components/AdminCarouselModal';
import { getRecommendedArticles, getMusicList, deleteArticle, updateArticle, createArticle, defaultArticles, defaultMusics } from '../service/mentalResourceService';
import { getMusicListByIdAsc, deleteMusic, updateMusic, createMusic } from '../service/adminMusicService';
import { getCarouselListByIdAsc, deleteCarousel, updateCarousel, createCarousel } from '../service/adminCarouselService';
import '../css/AdminMentalResource.css';

const { Title, Text } = Typography;

const AdminMentalResource = () => {
  const [articles, setArticles] = useState([]);
  const [musics, setMusics] = useState([]);
  const [loading, setLoading] = useState(false);
  const [articleModalVisible, setArticleModalVisible] = useState(false);
  const [musicModalVisible, setMusicModalVisible] = useState(false);
  const [editingArticle, setEditingArticle] = useState(null);
  const [editingMusic, setEditingMusic] = useState(null);
  const [currentAudio, setCurrentAudio] = useState(null);
  const [playingMusicId, setPlayingMusicId] = useState(null);
  const [articleForm] = Form.useForm();
  const [musicForm] = Form.useForm();
  const [articlePagination, setArticlePagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [musicPagination, setMusicPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [carousels, setCarousels] = useState([]);
  const [carouselModalVisible, setCarouselModalVisible] = useState(false);
  const [editingCarousel, setEditingCarousel] = useState(null);
  const [carouselForm] = Form.useForm();
  const [carouselPagination, setCarouselPagination] = useState({ current: 1, pageSize: 10, total: 0 });

  useEffect(() => {
    loadArticles(articlePagination.current, articlePagination.pageSize);
    loadMusics(musicPagination.current, musicPagination.pageSize);
    loadCarousels(carouselPagination.current, carouselPagination.pageSize);
  }, [articlePagination.current, articlePagination.pageSize, musicPagination.current, musicPagination.pageSize, carouselPagination.current, carouselPagination.pageSize]);

  const loadArticles = async (page = 1, pageSize = 10) => {
    setLoading(true);
    try {
      const res = await getRecommendedArticles({ page, size: pageSize });
      let dataArr = [];
      let total = 0;
      if (Array.isArray(res)) {
        dataArr = res;
        total = res.length;
      } else if (res && Array.isArray(res.list)) {
        dataArr = res.list;
        total = typeof res.total === 'number' ? res.total : dataArr.length;
      }
      setArticles(dataArr);
      setArticlePagination((prev) => ({ ...prev, total }));
    } catch (error) {
      message.error('加载数据失败');
      setArticles([]);
      setArticlePagination((prev) => ({ ...prev, total: 0 }));
    } finally {
      setLoading(false);
    }
  };

  const loadMusics = async (page = 1, pageSize = 10) => {
    setLoading(true);
    try {
      const res = await getMusicListByIdAsc({ page, size: pageSize });
      let dataArr = [];
      let total = 0;
      if (res && Array.isArray(res.list)) {
        dataArr = res.list;
        total = typeof res.total === 'number' ? res.total : dataArr.length;
      }
      setMusics(dataArr);
      setMusicPagination((prev) => ({ ...prev, total }));
    } catch (error) {
      message.error('加载音乐数据失败');
      setMusics([]);
      setMusicPagination((prev) => ({ ...prev, total: 0 }));
    } finally {
      setLoading(false);
    }
  };

  const loadCarousels = async (page = 1, pageSize = 10) => {
    setLoading(true);
    try {
      const res = await getCarouselListByIdAsc({ page, size: pageSize });
      let dataArr = [];
      let total = 0;
      if (res && Array.isArray(res.list)) {
        dataArr = res.list;
        total = typeof res.total === 'number' ? res.total : dataArr.length;
      }
      setCarousels(dataArr);
      setCarouselPagination((prev) => ({ ...prev, total }));
    } catch (error) {
      message.error('加载轮播图数据失败');
      setCarousels([]);
      setCarouselPagination((prev) => ({ ...prev, total: 0 }));
    } finally {
      setLoading(false);
    }
  };

  // 文章管理相关函数
  const handleAddArticle = () => {
    setEditingArticle(null);
    articleForm.resetFields();
    setArticleModalVisible(true);
  };
  const handleEditArticle = (article) => {
    setEditingArticle(article);
    articleForm.setFieldsValue(article);
    setArticleModalVisible(true);
  };
  const handleDeleteArticle = async (articleId) => {
    try {
      await deleteArticle(articleId);
      setArticles(articles.filter(article => article.id !== articleId));
      message.success('文章删除成功');
    } catch (error) {
      message.error('删除文章失败');
    }
  };
  const handleArticleSubmit = async (values) => {
    try {
      if (editingArticle) {
        // 调用更新接口
        const response = await updateArticle(editingArticle.id, values);
        if (response.success) {
          const updatedArticle = { ...editingArticle, ...values };
          setArticles(articles.map(article => article.id === editingArticle.id ? updatedArticle : article));
          message.success('文章更新成功');
        } else {
          message.error(response.message || '更新文章失败');
        }
      } else {
        // 调用新增接口
        const articleData = { ...values, cover: values.cover || 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=80' };
        const response = await createArticle(articleData);
        if (response.success) {
          // 计算新总数和最后一页
          const newTotal = articlePagination.total + 1;
          const lastPage = Math.ceil(newTotal / articlePagination.pageSize);
          setArticlePagination((prev) => ({ ...prev, current: lastPage, total: newTotal }));
          loadArticles(lastPage, articlePagination.pageSize);
          message.success('文章添加成功');
        } else {
          message.error(response.message || '添加文章失败');
        }
      }
      setArticleModalVisible(false);
      articleForm.resetFields();
    } catch (error) {
      message.error('保存文章失败');
    }
  };

  // 音乐管理相关函数
  const handleAddMusic = () => {
    setEditingMusic(null);
    musicForm.resetFields();
    musicForm.setFieldsValue({ duration: undefined });
    setMusicModalVisible(true);
  };
  const handleEditMusic = (music) => {
    setEditingMusic(music);
    musicForm.setFieldsValue({
      ...music,
      duration: typeof music.duration === 'number' ? music.duration : Number(music.duration) || undefined,
    });
    setMusicModalVisible(true);
  };
  const handleDeleteMusic = async (musicId) => {
    try {
      await deleteMusic(musicId);
      setMusics(musics.filter(music => music.id !== musicId));
      message.success('音乐删除成功');
    } catch (error) {
      message.error('删除音乐失败');
    }
  };
  const handleMusicSubmit = async (values) => {
    try {
      if (editingMusic) {
        const response = await updateMusic(editingMusic.id, values);
        if (response.success) {
          const updatedMusic = { ...editingMusic, ...values };
          setMusics(musics.map(music => music.id === editingMusic.id ? updatedMusic : music));
          message.success('音乐更新成功');
        } else {
          message.error(response.message || '更新音乐失败');
        }
      } else {
        const musicData = { ...values, cover: values.cover || 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=120&q=80' };
        const response = await createMusic(musicData);
        if (response.success) {
          // 计算新总数和最后一页
          const newTotal = musicPagination.total + 1;
          const lastPage = Math.ceil(newTotal / musicPagination.pageSize);
          setMusicPagination((prev) => ({ ...prev, current: lastPage, total: newTotal }));
          loadMusics(lastPage, musicPagination.pageSize);
          message.success('音乐添加成功');
        } else {
          message.error(response.message || '添加音乐失败');
        }
      }
      setMusicModalVisible(false);
      musicForm.resetFields();
    } catch (error) {
      message.error('保存音乐失败');
    }
  };

  // 轮播图管理相关函数
  const handleAddCarousel = () => {
    setEditingCarousel(null);
    carouselForm.resetFields();
    setCarouselModalVisible(true);
  };
  const handleEditCarousel = (carousel) => {
    setEditingCarousel(carousel);
    carouselForm.setFieldsValue({
      ...carousel,
      sortOrder: typeof carousel.sortOrder === 'number' ? carousel.sortOrder : Number(carousel.sortOrder) || 0,
    });
    setCarouselModalVisible(true);
  };
  const handleDeleteCarousel = async (carouselId) => {
    try {
      await deleteCarousel(carouselId);
      // 删除后自动刷新当前页
      loadCarousels(carouselPagination.current, carouselPagination.pageSize);
      message.success('轮播图删除成功');
    } catch (error) {
      message.error('删除轮播图失败');
    }
  };
  const handleCarouselSubmit = async (values) => {
    try {
      if (editingCarousel) {
        const response = await updateCarousel(editingCarousel.id, values);
        if (response.success) {
          const updatedCarousel = { ...editingCarousel, ...values };
          setCarousels(carousels.map(carousel => carousel.id === editingCarousel.id ? updatedCarousel : carousel));
          message.success('轮播图更新成功');
        } else {
          message.error(response.message || '更新轮播图失败');
        }
      } else {
        const carouselData = { ...values, sortOrder: typeof values.sortOrder === 'number' ? values.sortOrder : Number(values.sortOrder) || 0 };
        const response = await createCarousel(carouselData);
        if (response.success) {
          const newTotal = carouselPagination.total + 1;
          const lastPage = Math.ceil(newTotal / carouselPagination.pageSize);
          setCarouselPagination((prev) => ({ ...prev, current: lastPage, total: newTotal }));
          loadCarousels(lastPage, carouselPagination.pageSize);
          message.success('轮播图添加成功');
        } else {
          message.error(response.message || '添加轮播图失败');
        }
      }
      setCarouselModalVisible(false);
      carouselForm.resetFields();
    } catch (error) {
      message.error('保存轮播图失败');
    }
  };

  // 音乐播放相关函数
  const handlePlayMusic = (music) => {
    if (currentAudio) {
      currentAudio.pause();
    }
    if (playingMusicId === music.id) {
      setPlayingMusicId(null);
      setCurrentAudio(null);
      return;
    }
    const audio = new Audio(music.url);
    audio.addEventListener('ended', () => {
      setPlayingMusicId(null);
      setCurrentAudio(null);
    });
    audio.play().then(() => {
      setPlayingMusicId(music.id);
      setCurrentAudio(audio);
    }).catch(error => {
      message.error('播放音乐失败');
    });
  };

  return (
    <div className="admin-mental-resource">
      <div className="admin-mental-resource-header">
        <Title level={2}>心理资源管理</Title>
        <Text type="secondary">管理心理健康相关的文章和音乐资源</Text>
      </div>
      <Tabs
        defaultActiveKey="articles"
        className="admin-mental-resource-tabs"
        items={[
          {
            key: 'articles',
            label: (<span><BookOutlined />文章管理</span>),
            children: (
              <Card>
                <div style={{ position: 'relative' }}>
                  <AdminArticleTable
                    articles={articles}
                    loading={loading}
                    pagination={{
                      current: articlePagination.current,
                      pageSize: articlePagination.pageSize,
                      total: articlePagination.total,
                      showSizeChanger: true,
                      showQuickJumper: true,
                      showTotal: (total) => `共 ${total} 条记录`,
                    }}
                    onEdit={handleEditArticle}
                    onDelete={handleDeleteArticle}
                    onPageChange={(pagination) => {
                      setArticlePagination({ ...articlePagination, current: pagination.current, pageSize: pagination.pageSize });
                    }}
                  />
                  <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={handleAddArticle}
                    style={{ position: 'absolute', left: 0, bottom: 0, margin: 16, zIndex: 10 }}
                  >
                    添加文章
                  </Button>
                </div>
                <AdminArticleModal
                  visible={articleModalVisible}
                  editingArticle={editingArticle}
                  form={articleForm}
                  onSubmit={handleArticleSubmit}
                  onCancel={() => setArticleModalVisible(false)}
                />
              </Card>
            ),
          },
          {
            key: 'music',
            label: (<span><SoundOutlined />音乐管理</span>),
            children: (
              <Card>
                <div style={{ position: 'relative' }}>
                  <AdminMusicTable
                    musics={musics}
                    loading={loading}
                    pagination={{
                      current: musicPagination.current,
                      pageSize: musicPagination.pageSize,
                      total: musicPagination.total,
                      showSizeChanger: true,
                      showQuickJumper: true,
                      showTotal: (total) => `共 ${total} 条记录`,
                    }}
                    playingMusicId={playingMusicId}
                    onPlay={handlePlayMusic}
                    onEdit={handleEditMusic}
                    onDelete={handleDeleteMusic}
                    onPageChange={(pagination) => {
                      setMusicPagination({ ...musicPagination, current: pagination.current, pageSize: pagination.pageSize });
                    }}
                    showCategoryColumn={true}
                  />
                  <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={handleAddMusic}
                    style={{ position: 'absolute', left: 0, bottom: 0, margin: 16, zIndex: 10 }}
                  >
                    添加音乐
                  </Button>
                </div>
                <AdminMusicModal
                  visible={musicModalVisible}
                  editingMusic={editingMusic}
                  form={musicForm}
                  onSubmit={handleMusicSubmit}
                  onCancel={() => setMusicModalVisible(false)}
                />
              </Card>
            ),
          },
          {
            key: 'carousel',
            label: (<span><BookOutlined />轮播图管理</span>),
            children: (
              <Card>
                <div style={{ position: 'relative' }}>
                  <AdminCarouselTable
                    carousels={carousels}
                    loading={loading}
                    pagination={{
                      current: carouselPagination.current,
                      pageSize: carouselPagination.pageSize,
                      total: carouselPagination.total,
                      showSizeChanger: true,
                      showQuickJumper: true,
                      showTotal: (total) => `共 ${total} 条记录`,
                    }}
                    onEdit={handleEditCarousel}
                    onDelete={handleDeleteCarousel}
                    onPageChange={(pagination) => {
                      setCarouselPagination({ ...carouselPagination, current: pagination.current, pageSize: pagination.pageSize });
                    }}
                  />
                  <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={handleAddCarousel}
                    style={{ position: 'absolute', left: 0, bottom: 0, margin: 16, zIndex: 10 }}
                  >
                    添加轮播图
                  </Button>
                </div>
                <AdminCarouselModal
                  visible={carouselModalVisible}
                  editingCarousel={editingCarousel}
                  form={carouselForm}
                  onSubmit={handleCarouselSubmit}
                  onCancel={() => setCarouselModalVisible(false)}
                />
              </Card>
            ),
          },
        ]}
      />
    </div>
  );
};

export default AdminMentalResource; 