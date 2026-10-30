package cn.intellijboy.demo.domain.studentcourse.model;

import cn.intellijboy.demo.domain.studentcourse.exception.InvalidScoreException;

/**
 * 成绩值对象：不可变，承载 0-100 区间不变式。
 */
public record Score(int value) {

    public Score {
        if (value < 0 || value > 100) {
            throw new InvalidScoreException("成绩必须在 0 到 100 之间");
        }
    }
}
