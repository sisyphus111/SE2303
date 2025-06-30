import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Card, Typography, Button } from "antd";
import "../css/MentalResource.css"

// API服务导入注释
// import { getArticleDetail } from '../service/mentalResourceService';

const { Title, Paragraph } = Typography;

// 心理健康文章详细数据配风景封面，实际项目可从接口获取
const articles = [
  {
    id: "1",
    title: "如何保持良好的心理健康",
    description: "分享一些日常保持心理健康的小技巧，包括规律作息、适度运动、保持社交联系等实用方法。",
    content: "心理健康是我们整体健康的重要组成部分。保持良好的心理健康需要我们在日常生活中养成一些健康的习惯。首先，保持规律的作息时间，充足的睡眠对心理健康至关重要。其次，适度的运动不仅能改善身体状况，还能释放内啡肽，提升心情。保持积极的社交联系也很重要，与家人朋友的交流能够提供情感支持。此外，学会管理压力，通过深呼吸、冥想等放松技巧来缓解紧张情绪。最后，培养兴趣爱好，给生活增添乐趣和意义。",
    cover: "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&w=1200&q=85",
  },
  {
    id: "2",
    title: "压力管理的有效方法",
    description: "学会管理生活和工作中的压力，通过时间管理、放松技巧和心态调整来应对挑战。",
    content: "现代生活中，压力无处不在，学会有效管理压力是维护心理健康的关键。首先要识别压力源，了解什么情况下会感到压力。时间管理是减压的有效方法，合理安排时间，避免任务堆积。学会说'不'，不要过度承担责任。建立支持网络，在需要时寻求帮助。掌握放松技巧如深呼吸、渐进性肌肉放松等。保持积极心态，将挑战视为成长机会。定期进行体育锻炼，这是天然的压力释放方式。最重要的是，给自己留出休息和娱乐的时间。",
    cover: "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1200&q=85",
  },
  {
    id: "3",
    title: "冥想与放松技巧",
    description: "通过冥想和放松练习改善心情，每天10分钟的正念练习就能带来显著的心理健康改善。",
    content: "冥想是一种古老而有效的心理健康练习方法。正念冥想能帮助我们专注当下，减少焦虑和抑郁情绪。开始时，每天只需10-15分钟的练习。找一个安静的地方坐下，闭上眼睛，专注于呼吸。当思绪飘散时，温和地将注意力带回呼吸上。身体扫描是另一种有效技巧，从头到脚依次关注身体各部位的感觉。渐进性肌肉放松法通过紧张和放松不同肌肉群来释放压力。引导冥想音频对初学者很有帮助。坚持练习，你会发现心情更平静，注意力更集中。",
    cover: "https://images.unsplash.com/photo-1464207687429-7505649dae38?auto=format&fit=crop&w=1200&q=85",
  },
  {
    id: "4",
    title: "建立健康的睡眠习惯",
    description: "良好的睡眠对心理健康至关重要，了解如何改善睡眠质量和建立规律的睡眠模式。",
    content: "睡眠与心理健康密切相关，睡眠不足会影响情绪调节和认知功能。建立规律的睡眠习惯非常重要。每天在同一时间上床睡觉和起床，包括周末。睡前1-2小时避免使用电子设备，蓝光会干扰褪黑素分泌。创造良好的睡眠环境：保持房间凉爽、安静、黑暗。睡前进行放松活动，如阅读、听轻音乐或泡温水澡。避免睡前摄入咖啡因和大量液体。如果躺在床上20分钟仍无法入睡，起床做些安静的活动直到困倦再回到床上。保持良好的睡眠卫生习惯，你的心理健康会得到显著改善。",
    cover: "https://images.unsplash.com/photo-1418065460487-3baa5e04c47b?auto=format&fit=crop&w=1200&q=85",
  },
  {
    id: "5",
    title: "情绪调节与自我关怀",
    description: "学习识别和管理自己的情绪，培养自我关怀的习惯，建立更健康的心理状态。",
    content: "情绪调节是心理健康的核心技能。首先学会识别情绪，给感受命名有助于更好地理解自己。接受所有情绪都是正常的，不要压抑负面情绪。学会情绪表达的健康方式，如写日记、与信任的人交谈、艺术创作等。自我关怀意味着像对待好朋友一样善待自己。当犯错时，用理解和同情替代自我批评。培养感恩习惯，每天记录三件值得感恩的事情。学会自我安慰的技巧，如拥抱、听音乐、洗热水澡等。建立个人边界，学会拒绝不合理要求。记住，照顾好自己才能更好地照顾他人。",
    cover: "https://images.unsplash.com/photo-1523906834658-6e24ef2386f9?auto=format&fit=crop&w=1200&q=85",
  },
  {
    id: "6",
    title: "社交焦虑的克服方法",
    description: "如何逐步克服社交焦虑，建立自信，改善人际关系，享受更丰富的社交生活。",
    content: "社交焦虑是很常见的心理问题，但可以通过适当的方法得到改善。首先了解社交焦虑的表现：在社交场合感到紧张、担心被评判、避免社交活动等。逐步暴露是有效的治疗方法，从低焦虑的社交情况开始，逐渐挑战更困难的场合。练习放松技巧，如深呼吸和肌肉放松，在紧张时使用。改变负面思维模式，质疑'别人会觉得我很奇怪'这样的想法。提前准备话题，减少社交时的不确定感。关注对方而非自己的表现。从小的社交目标开始，如主动向收银员问好。建立支持网络，与理解你的人分享感受。记住，大多数人都专注于自己，不会过度关注你的表现。",
    cover: "https://images.unsplash.com/photo-1531366936337-7c912a4589a7?auto=format&fit=crop&w=1200&q=85",
  },
  {
    id: "7",
    title: "正念练习与情绪平衡",
    description: "学习正念技巧帮助平衡情绪，减少焦虑和抑郁情绪，提升整体心理健康水平。",
    content: "正念是一种关注当下的心理状态，不带评判地觉察此刻的体验。正念练习能帮助我们更好地管理情绪，减少反刍思维。日常正念练习包括：正念呼吸、正念进食、正念行走等。当强烈情绪出现时，使用STOP技巧：停下来(Stop)、深呼吸(Take a breath)、观察(Observe)感受和想法、继续前进(Proceed)。正念观察情绪如同观察天空中的云朵，它们会自然来去。培养观察者的心态，与情绪保持适当距离。定期练习身体扫描和正念冥想。将正念融入日常活动，如刷牙、洗碗时保持专注。正念练习需要时间和耐心，但会带来持久的情绪平衡。",
    cover: "https://images.unsplash.com/photo-1522383225653-ed111181a951?auto=format&fit=crop&w=1200&q=85",
  },
  {
    id: "8",
    title: "心理韧性的培养与提升",
    description: "增强心理韧性，学会在面对困难和挫折时保持积极心态，建立更强的抗压能力。",
    content: "心理韧性是指在面对逆境、创伤或重大压力时的适应和恢复能力。培养韧性需要多方面的努力。建立积极的认知模式，将挑战视为成长机会而非威胁。培养解决问题的能力，面对困难时主动寻找解决方案。建立强大的社会支持网络，在困难时有人可以依靠。培养情绪调节技能，学会管理压力和负面情绪。保持身体健康，规律运动和健康饮食为心理韧性提供基础。设定现实可达的目标，每个小成功都能增强信心。学会从失败中吸取教训，将挫折视为学习经验。培养感恩心态，专注于生活中的积极方面。实践自我关怀，在困难时期给自己温暖和支持。记住，韧性是可以培养的技能，通过练习会越来越强。",
    cover: "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=1200&q=85",
  },
];

function ArticleDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [article, setArticle] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadArticleDetail();
  }, [id]);

  // 加载文章详情的函数
  const loadArticleDetail = async () => {
    try {
      setLoading(true);
      
      // API调用注释：获取文章详情
      // const response = await getArticleDetail(id);
      // setArticle(response.data);
      
      // 使用模拟数据作为备选
      const foundArticle = articles.find(a => a.id === id);
      setArticle(foundArticle);
      
    } catch (error) {
      console.error('获取文章详情失败:', error);
      // 使用模拟数据作为备选
      const foundArticle = articles.find(a => a.id === id);
      setArticle(foundArticle);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  if (!article) {
    return <div>文章不存在</div>;
  }

  return (
    <div className="mental-resource-bg">
      <Card className="mental-resource-card mental-resource-card-body">
        <Button onClick={() => navigate(-1)} className="mental-resource-detail-back-btn">返回</Button>
        <img src={article.cover} alt={article.title} className="mental-resource-detail-cover" />
        <Title level={2}>{article.title}</Title>
        <Paragraph type="secondary">{article.description}</Paragraph>
        <Paragraph>{article.content}</Paragraph>
      </Card>
    </div>
  );
}

export default ArticleDetail;