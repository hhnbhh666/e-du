package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.wyspring.entity.FavoriteQuestion;

/**
 * 收藏题目Mapper
 */
@Mapper
public interface FavoriteQuestionMapper extends BaseMapper<FavoriteQuestion> {

    /**
     * 查询用户的收藏记录
     */
    @Select("SELECT * FROM favorite_questions WHERE user_id = #{userId} AND question_id = #{questionId}")
    FavoriteQuestion selectByUserAndQuestion(@Param("userId") Long userId, @Param("questionId") Long questionId);

    /**
     * 检查用户是否收藏该题
     */
    @Select("SELECT COUNT(*) FROM favorite_questions WHERE user_id = #{userId} AND question_id = #{questionId}")
    int countByUserAndQuestion(@Param("userId") Long userId, @Param("questionId") Long questionId);

    /**
     * 分页查询用户的收藏列表
     */
    @Select("SELECT fq.*, q.content, q.type, q.difficulty " +
            "FROM favorite_questions fq " +
            "LEFT JOIN questions q ON fq.question_id = q.id " +
            "WHERE fq.user_id = #{userId} AND q.is_deleted = 0 " +
            "ORDER BY fq.created_at DESC")
    IPage<FavoriteQuestion> selectPageByUserId(Page<FavoriteQuestion> page, @Param("userId") Long userId);
}
