package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.wyspring.entity.UserAnswerRecord;

/**
 * 用户答题记录Mapper
 */
@Mapper
public interface UserAnswerRecordMapper extends BaseMapper<UserAnswerRecord> {

    /**
     * 查询用户的答题记录
     */
    @Select("SELECT * FROM user_answer_records WHERE user_id = #{userId} AND question_id = #{questionId}")
    UserAnswerRecord selectByUserAndQuestion(@Param("userId") Long userId, @Param("questionId") Long questionId);

    /**
     * 检查用户是否答过该题
     */
    @Select("SELECT COUNT(*) FROM user_answer_records WHERE user_id = #{userId} AND question_id = #{questionId}")
    int countByUserAndQuestion(@Param("userId") Long userId, @Param("questionId") Long questionId);

    /**
     * 统计用户答对题目数
     */
    @Select("SELECT COUNT(*) FROM user_answer_records WHERE user_id = #{userId} AND is_correct = 1")
    int countCorrectByUserId(@Param("userId") Long userId);

    /**
     * 统计用户答题总数
     */
    @Select("SELECT COUNT(*) FROM user_answer_records WHERE user_id = #{userId}")
    int countTotalByUserId(@Param("userId") Long userId);
}
