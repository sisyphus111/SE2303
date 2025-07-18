package com.se2303.moodservice.service;

import com.se2303.moodservice.dao.MoodDAO;
import com.se2303.moodservice.dao.MoodItemDAO;
import com.se2303.moodservice.model.Mood;
import com.se2303.moodservice.model.MoodItem;
import com.se2303.moodservice.model.MoodLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 数据库初始化服务
 * 使用DAO层进行数据访问
 */
@Service
public class DatabaseInitService implements CommandLineRunner {
    
    @Autowired
    private MoodDAO moodDAO;
    
    @Autowired
    private MoodItemDAO moodItemDAO;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 检查是否需要初始化数据
        if (moodDAO.countByUserId("1") == 0) {
            initializeSampleData();
        }
    }
    
    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        System.out.println("正在初始化示例数据...");
        
        // 创建示例心情记录 - 用户1的最近7天数据
        for (int i = 0; i < 7; i++) {
            Mood mood = new Mood();
            mood.setUserId("1");
            mood.setDate(LocalDate.now().minusDays(i));
            
            // 根据天数设置不同的心情评分
            int score = 3 + (i % 3); // 3-5分之间循环
            mood.setScore(score);
            mood.setScoreCommitted(true);
            
            // 根据评分设置不同的日记内容
            String diaryContent;
            List<String> labels;
            switch (score) {
                case 5:
                    diaryContent = "今天心情非常好，工作顺利，和朋友聊天很开心";
                    labels = Arrays.asList("开心", "满足", "充满活力");
                    break;
                case 4:
                    diaryContent = "今天心情不错，完成了大部分工作，感觉有成就感";
                    labels = Arrays.asList("满足", "专注", "平静");
                    break;
                default:
                    diaryContent = "今天心情一般，工作有些压力，需要调整心态";
                    labels = Arrays.asList("平静", "专注", "压力");
                    break;
            }
            
            mood.setDiaryDescription(diaryContent);
            mood.setDiaryCommitted(true);
            mood.setDiaryTitle("第" + (i + 1) + "天的心情记录");
            mood = moodDAO.save(mood);
            
            // 添加心情标签
            for (String label : labels) {
                MoodItem moodItem = new MoodItem(label, mood);
                moodItemDAO.save(moodItem);
            }
        }
        
        // 创建用户2的示例记录
        Mood mood2 = new Mood();
        mood2.setUserId("2");
        mood2.setDate(LocalDate.now().minusDays(1));
        mood2.setScore(2);
        mood2.setScoreCommitted(true);
        mood2.setDiaryDescription("今天心情不太好，工作压力很大，需要放松一下");
        mood2.setDiaryCommitted(true);
        mood2.setDiaryTitle("压力的一天");
        mood2 = moodDAO.save(mood2);
        
        // 添加心情标签
        List<String> labels2 = Arrays.asList("焦虑", "压力", "疲惫");
        for (String label : labels2) {
            MoodItem moodItem = new MoodItem(label, mood2);
            moodItemDAO.save(moodItem);
        }
        
        System.out.println("示例数据初始化完成！");
        System.out.println("创建了 " + moodDAO.countByUserId("1") + " 条用户1的心情记录");
        System.out.println("创建了 " + moodDAO.countByUserId("2") + " 条用户2的心情记录");
    }
    
    /**
     * 手动初始化数据
     */
    @Transactional
    public void manualInit() {
        initializeSampleData();
    }
    
    /**
     * 清空所有数据
     */
    @Transactional
    public void clearAllData() {
        moodItemDAO.deleteAll();
        moodDAO.deleteAll();
        System.out.println("所有数据已清空");
    }
    
    /**
     * 获取数据统计
     */
    public String getDataStats() {
        long moodCount = moodDAO.countByUserId("1");
        long moodItemCount = moodItemDAO.countByLabel("开心");
        
        return String.format("数据统计: 心情记录 %d 条, 心情标签 %d 条", moodCount, moodItemCount);
    }
} 