package com.se2303.communityservice.repository;

import com.se2303.communityservice.entity.PostDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostElasticSearchRepository extends ElasticsearchRepository<PostDocument, String> {

    // 根据审核状态查询
    Page<PostDocument> findByReviewStatus(String reviewStatus, Pageable pageable);

    // 根据标签查询
    Page<PostDocument> findByTagsIn(List<String> tags, Pageable pageable);

    // 根据用户ID查询
    Page<PostDocument> findByUserId(String userId, Pageable pageable);

    // 智能关键词搜索 - 支持语义搜索、同义词匹配、模糊搜索
    @Query("{\"bool\": {\"should\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^8\", \"content^3\"], \"type\": \"best_fields\", \"fuzziness\": \"1\", \"minimum_should_match\": \"60%\"}}, {\"wildcard\": {\"title\": \"*?0*\"}}, {\"wildcard\": {\"content\": \"*?0*\"}}, {\"match\": {\"title\": {\"query\": \"?0\", \"fuzziness\": \"1\"}}}, {\"match\": {\"content\": {\"query\": \"?0\", \"fuzziness\": \"2\"}}}], \"minimum_should_match\": 2}}")
    Page<PostDocument> searchByKeyword(String keyword, Pageable pageable);

    // 智能关键词搜索 + 审核状态过滤
    @Query("{\"bool\": {\"should\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^8\", \"content^3\"], \"type\": \"best_fields\", \"fuzziness\": \"1\", \"minimum_should_match\": \"60%\"}}, {\"wildcard\": {\"title\": \"*?0*\"}}, {\"wildcard\": {\"content\": \"*?0*\"}}, {\"match\": {\"title\": {\"query\": \"?0\", \"fuzziness\": \"1\"}}}, {\"match\": {\"content\": {\"query\": \"?0\", \"fuzziness\": \"2\"}}}], \"filter\": [{\"term\": {\"reviewStatus\": \"?1\"}}], \"minimum_should_match\": 2}}")
    Page<PostDocument> searchByKeywordAndReviewStatus(String keyword, String reviewStatus, Pageable pageable);

    // 智能关键词搜索 + 标签筛选
    @Query("{\"bool\": {\"should\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^8\", \"content^3\"], \"type\": \"best_fields\", \"fuzziness\": \"1\", \"minimum_should_match\": \"60%\"}}, {\"wildcard\": {\"title\": \"*?0*\"}}, {\"wildcard\": {\"content\": \"*?0*\"}}, {\"match\": {\"title\": {\"query\": \"?0\", \"fuzziness\": \"1\"}}}, {\"match\": {\"content\": {\"query\": \"?0\", \"fuzziness\": \"2\"}}}], \"filter\": [{\"terms\": {\"tags\": ?1}}], \"minimum_should_match\": 2}}")
    Page<PostDocument> searchByKeywordAndTags(String keyword, List<String> tags, Pageable pageable);

    // 智能关键词搜索 + 标签筛选 + 审核状态过滤
    @Query("{\"bool\": {\"should\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^8\", \"content^3\"], \"type\": \"best_fields\", \"fuzziness\": \"1\", \"minimum_should_match\": \"60%\"}}, {\"wildcard\": {\"title\": \"*?0*\"}}, {\"wildcard\": {\"content\": \"*?0*\"}}, {\"match\": {\"title\": {\"query\": \"?0\", \"fuzziness\": \"1\"}}}, {\"match\": {\"content\": {\"query\": \"?0\", \"fuzziness\": \"2\"}}}], \"filter\": [{\"terms\": {\"tags\": ?1}}, {\"term\": {\"reviewStatus\": \"?2\"}}], \"minimum_should_match\": 2}}")
    Page<PostDocument> searchByKeywordAndTagsAndReviewStatus(String keyword, List<String> tags, String reviewStatus, Pageable pageable);
} 