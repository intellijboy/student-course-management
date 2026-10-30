package cn.intellijboy.demo.studentcourse.domain.model;

import cn.intellijboy.demo.studentcourse.domain.exception.InvalidScoreException;

/**
 * 成绩值对象：不可变，承载 0-100 区间不变式。
 */
public record Score(int value) {

    /**
     * 紧凑构造器：校验成绩必须落在 0-100 区间，保证值对象不变式。
     *
     * @param value 成绩分数，必须满足 {@code 0 <= value <= 100}
     * @throws InvalidScoreException 当分数小于 0 或大于 100 时抛出
     */
    public Score {
        if (value < 0 || value > 100) {
            throw new InvalidScoreException("成绩必须在 0 到 100 之间");
        }
    }
}
