import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Table, 
  Button, 
  Modal, 
  Form, 
  Input, 
  Upload, 
  message, 
  Space, 
  Popconfirm, 
  Tabs, 
  Tag,
  Select,
  Typography
} from 'antd';
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  UploadOutlined,
  PlayCircleOutlined,
  PauseCircleOutlined,
  BookOutlined,
  SoundOutlined
} from '@ant-design/icons';
import { 
  getRecommendedArticles, 
  getMusicList,
  defaultArticles,
  defaultMusics,
} from '../service/mentalResourceService';
import '../css/AdminMentalResource.css';

const { Title, Text } = Typography;
const { TextArea } = Input;
const { TabPane } = Tabs;
const { Option } = Select;

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

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [articlesData, musicsData] = await Promise.all([
        getRecommendedArticles(),
        getMusicList()
      ]);
      setArticles(articlesData || defaultArticles);
      setMusics(musicsData || defaultMusics);
    } catch (error) {
      console.error('加载数据失败:', error);
      message.error('加载数据失败');
      setArticles(defaultArticles);
      setMusics(defaultMusics);
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
      // await deleteArticle(articleId);
      setArticles(articles.filter(article => article.id !== articleId));
      message.success('文章删除成功');
    } catch (error) {
      console.error('删除文章失败:', error);
      message.error('删除文章失败');
    }
  };

  const handleArticleSubmit = async (values) => {
    try {
      if (editingArticle) {
        // 更新文章
        // await updateArticle(editingArticle.id, values);
        const updatedArticle = { ...editingArticle, ...values };
        setArticles(articles.map(article => 
          article.id === editingArticle.id ? updatedArticle : article
        ));
        message.success('文章更新成功');
      } else {
        // 添加新文章
        const articleData = {
          ...values,
          cover: values.cover || 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=80'
        };
        // const newArticle = await createArticle(articleData);
        // setArticles([...articles, { id: newArticle.id || Date.now().toString(), ...articleData }]);
        message.success('文章添加成功');
      }
      setArticleModalVisible(false);
      articleForm.resetFields();
    } catch (error) {
      console.error('保存文章失败:', error);
      message.error('保存文章失败');
    }
  };

  // 音乐管理相关函数
  const handleAddMusic = () => {
    setEditingMusic(null);
    musicForm.resetFields();
    setMusicModalVisible(true);
  };

  const handleEditMusic = (music) => {
    setEditingMusic(music);
    musicForm.setFieldsValue(music);
    setMusicModalVisible(true);
  };

  const handleDeleteMusic = async (musicId) => {
    try {
      // await deleteMusic(musicId);
      setMusics(musics.filter(music => music.id !== musicId));
      message.success('音乐删除成功');
    } catch (error) {
      console.error('删除音乐失败:', error);
      message.error('删除音乐失败');
    }
  };

  const handleMusicSubmit = async (values) => {
    try {
      if (editingMusic) {
        // 更新音乐
        // await updateMusic(editingMusic.id, values);
        const updatedMusic = { ...editingMusic, ...values };
        setMusics(musics.map(music => 
          music.id === editingMusic.id ? updatedMusic : music
        ));
        message.success('音乐更新成功');
      } else {
        // 添加新音乐
        const musicData = {
          ...values,
          cover: values.cover || 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=120&q=80'
        };
        // const newMusic = await createMusic(musicData);
        // setMusics([...musics, { id: newMusic.id || Date.now().toString(), ...musicData }]);
        message.success('音乐添加成功');
      }
      setMusicModalVisible(false);
      musicForm.resetFields();
    } catch (error) {
      console.error('保存音乐失败:', error);
      message.error('保存音乐失败');
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
      console.error('播放音乐失败:', error);
      message.error('播放音乐失败');
    });
  };

  // 文章表格列配置
  const articleColumns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      ellipsis: true,
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '封面',
      dataIndex: 'cover',
      key: 'cover',
      width: 100,
      render: (cover) => (
        <img 
          src={cover} 
          alt="封面" 
          style={{ width: 60, height: 40, objectFit: 'cover', borderRadius: 4 }}
        />
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space>
          <Button 
            type="primary" 
            size="small" 
            icon={<EditOutlined />}
            onClick={() => handleEditArticle(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除这篇文章吗？"
            // onConfirm={() => handleDeleteArticle(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button 
              type="primary" 
              danger 
              size="small" 
              icon={<DeleteOutlined />}
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 音乐表格列配置
  const musicColumns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: '艺术家',
      dataIndex: 'artist',
      key: 'artist',
    },
    {
      title: '封面',
      dataIndex: 'cover',
      key: 'cover',
      width: 100,
      render: (cover) => (
        <img 
          src={cover} 
          alt="封面" 
          style={{ width: 60, height: 40, objectFit: 'cover', borderRadius: 4 }}
        />
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record) => (
        <Space>
          <Button 
            type="default" 
            size="small" 
            icon={playingMusicId === record.id ? <PauseCircleOutlined /> : <PlayCircleOutlined />}
            onClick={() => handlePlayMusic(record)}
          >
            {playingMusicId === record.id ? '暂停' : '播放'}
          </Button>
          <Button 
            type="primary" 
            size="small" 
            icon={<EditOutlined />}
            onClick={() => handleEditMusic(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除这首音乐吗？"
            // onConfirm={() => handleDeleteMusic(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button 
              type="primary" 
              danger 
              size="small" 
              icon={<DeleteOutlined />}
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="admin-mental-resource">
      <div className="admin-mental-resource-header">
        <Title level={2}>心理资源管理</Title>
        <Text type="secondary">管理心理健康相关的文章和音乐资源</Text>
      </div>

      <Tabs defaultActiveKey="articles" className="admin-mental-resource-tabs">
        <TabPane 
          tab={
            <span>
              <BookOutlined />
              文章管理
            </span>
          } 
          key="articles"
        >
          <Card>
            <div className="admin-mental-resource-toolbar">
              <Button 
                type="primary" 
                icon={<PlusOutlined />}
                onClick={handleAddArticle}
              >
                添加文章
              </Button>
            </div>
            <Table
              columns={articleColumns}
              dataSource={articles}
              rowKey="id"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total) => `共 ${total} 条记录`,
              }}
            />
          </Card>
        </TabPane>

        <TabPane 
          tab={
            <span>
              <SoundOutlined />
              音乐管理
            </span>
          } 
          key="music"
        >
          <Card>
            <div className="admin-mental-resource-toolbar">
              <Button 
                type="primary" 
                icon={<PlusOutlined />}
                onClick={handleAddMusic}
              >
                添加音乐
              </Button>
            </div>
            <Table
              columns={musicColumns}
              dataSource={musics}
              rowKey="id"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total) => `共 ${total} 条记录`,
              }}
            />
          </Card>
        </TabPane>
      </Tabs>

      {/* 文章编辑模态框 */}
      <Modal
        title={editingArticle ? '编辑文章' : '添加文章'}
        open={articleModalVisible}
        onCancel={() => setArticleModalVisible(false)}
        footer={null}
        width={600}
      >
        <Form
          form={articleForm}
          layout="vertical"
          onFinish={handleArticleSubmit}
        >
          <Form.Item
            name="title"
            label="标题"
            rules={[{ required: true, message: '请输入文章标题' }]}
          >
            <Input placeholder="请输入文章标题" />
          </Form.Item>
          <Form.Item
            name="description"
            label="描述"
            rules={[{ required: true, message: '请输入文章描述' }]}
          >
            <TextArea rows={3} placeholder="请输入文章描述" />
          </Form.Item>
          <Form.Item
            name="cover"
            label="封面图片URL"
          >
            <Input placeholder="请输入封面图片URL" />
          </Form.Item>
          <Form.Item
            name="content"
            label="内容"
          >
            <TextArea rows={8} placeholder="请输入文章内容（支持Markdown格式）" />
          </Form.Item>
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                {editingArticle ? '更新' : '添加'}
              </Button>
              <Button onClick={() => setArticleModalVisible(false)}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      {/* 音乐编辑模态框 */}
      <Modal
        title={editingMusic ? '编辑音乐' : '添加音乐'}
        open={musicModalVisible}
        onCancel={() => setMusicModalVisible(false)}
        footer={null}
        width={600}
      >
        <Form
          form={musicForm}
          layout="vertical"
          onFinish={handleMusicSubmit}
        >
          <Form.Item
            name="title"
            label="标题"
            rules={[{ required: true, message: '请输入音乐标题' }]}
          >
            <Input placeholder="请输入音乐标题" />
          </Form.Item>
          <Form.Item
            name="artist"
            label="艺术家"
            rules={[{ required: true, message: '请输入艺术家名称' }]}
          >
            <Input placeholder="请输入艺术家名称" />
          </Form.Item>
          <Form.Item
            name="cover"
            label="封面图片URL"
          >
            <Input placeholder="请输入封面图片URL" />
          </Form.Item>
          <Form.Item
            name="url"
            label="音频文件URL"
            rules={[{ required: true, message: '请输入音频文件URL' }]}
          >
            <Input placeholder="请输入音频文件URL" />
          </Form.Item>
          <Form.Item
            name="description"
            label="描述"
          >
            <TextArea rows={3} placeholder="请输入音乐描述" />
          </Form.Item>
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                {editingMusic ? '更新' : '添加'}
              </Button>
              <Button onClick={() => setMusicModalVisible(false)}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default AdminMentalResource; 