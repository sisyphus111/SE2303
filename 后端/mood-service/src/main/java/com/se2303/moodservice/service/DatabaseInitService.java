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
        
        // 创建示例心情记录
        Mood mood1 = new Mood();
        mood1.setUserId("1");
        mood1.setDate(LocalDate.now().minusDays(1));
        mood1.setScore(4);
        mood1.setScoreCommitted(true);
        mood1.setDiaryDescription("昨天心情很好，工作顺利，完成了所有任务");
        mood1.setDiaryCommitted(true);
        mood1 = moodDAO.save(mood1);
        
        // 添加心情标签
        List<String> labels1 = Arrays.asList("开心", "满足", "充满活力");
        for (String label : labels1) {
            MoodItem moodItem = new MoodItem(label, mood1);
            moodItemDAO.save(moodItem);
        }
        
        // 创建第二个示例记录
        Mood mood2 = new Mood();
        mood2.setUserId("1");
        mood2.setDate(LocalDate.now().minusDays(2));
        mood2.setScore(3);
        mood2.setScoreCommitted(true);
        mood2.setDiaryDescription("前天工作有些压力，但整体还可以");
        mood2.setDiaryCommitted(true);
        mood2 = moodDAO.save(mood2);
        
        // 添加心情标签
        List<String> labels2 = Arrays.asList("平静", "专注");
        for (String label : labels2) {
            MoodItem moodItem = new MoodItem(label, mood2);
            moodItemDAO.save(moodItem);
        }
        
        // 创建第三个示例记录（负面情绪）
        Mood mood3 = new Mood();
        mood3.setUserId("2");
        mood3.setDate(LocalDate.now().minusDays(3));
        mood3.setScore(1);
        mood3.setScoreCommitted(true);
        mood3.setDiaryDescription("前几天心情不太好，工作压力很大");
        mood3.setDiaryCommitted(true);
        mood3 = moodDAO.save(mood3);
        
        // 添加心情标签
        List<String> labels3 = Arrays.asList("焦虑", "压力");
        for (String label : labels3) {
            MoodItem moodItem = new MoodItem(label, mood3);
            moodItemDAO.save(moodItem);
        }
        
        System.out.println("示例数据初始化完成！");
        System.out.println("创建了 " + moodDAO.countByUserId("1") + " 条心情记录");
        System.out.println("创建了 " + moodItemDAO.countByLabel("开心") + " 条心情标签");
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