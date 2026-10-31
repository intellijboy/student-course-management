-- 演示种子数据：覆盖 student-course 与 library-lending 两个上下文全部功能场景。
-- 按外键依赖顺序插入：students/courses → course_grades → books → book_copies → loans → reservations。
-- books/book_copies 主键无自增（id 由领域代码分配）：书目 id 由 IncrementalBookIdGenerator（从 1 递增）
-- 分配，副本 id = 书id*100 + 序号。种子数据必须显式指定 id，且书目 id 使用不与生成器递增区间
-- （从 1 起）冲突的预留大数段（9001 起），确保后续 API 登记新书时 id 不与种子冲突。
-- 其余自增表显式指定 id 后，用 ALTER TABLE ... RESTART WITH 重置 IDENTITY 计数，避免后续新增主键冲突。

-- ============ 学生 ×3 ============
INSERT INTO students (id, name, email) VALUES (1, '张三', 'zhangsan@example.com');
INSERT INTO students (id, name, email) VALUES (2, '李四', 'lisi@example.com');
INSERT INTO students (id, name, email) VALUES (3, '王五', 'wangwu@example.com');
ALTER TABLE students ALTER COLUMN id RESTART WITH 4;

-- ============ 课程 ×3 ============
INSERT INTO courses (id, name, credits) VALUES (1, 'Java程序设计', 4);
INSERT INTO courses (id, name, credits) VALUES (2, '数据库原理', 3);
INSERT INTO courses (id, name, credits) VALUES (3, '算法分析与设计', 3);
ALTER TABLE courses ALTER COLUMN id RESTART WITH 4;

-- ============ 成绩若干 ============
-- 张三: Java 92、数据库 85；李四: Java 76、算法 88；王五: 数据库 90、算法 95
INSERT INTO course_grades (id, student_id, course_id, score) VALUES (1, 1, 1, 92);
INSERT INTO course_grades (id, student_id, course_id, score) VALUES (2, 1, 2, 85);
INSERT INTO course_grades (id, student_id, course_id, score) VALUES (3, 2, 1, 76);
INSERT INTO course_grades (id, student_id, course_id, score) VALUES (4, 2, 3, 88);
INSERT INTO course_grades (id, student_id, course_id, score) VALUES (5, 3, 2, 90);
INSERT INTO course_grades (id, student_id, course_id, score) VALUES (6, 3, 3, 95);
ALTER TABLE course_grades ALTER COLUMN id RESTART WITH 7;

-- ============ 图书 ×3（主键无自增，显式指定预留段 id；副本 id = 书id*100 + 序号） ============
INSERT INTO books (id, title, author) VALUES (9001, '深入理解Java虚拟机', '周志明');
INSERT INTO books (id, title, author) VALUES (9002, '代码整洁之道', 'Robert C. Martin');
INSERT INTO books (id, title, author) VALUES (9003, '算法导论', 'Thomas H. Cormen');

-- 《深入理解Java虚拟机》×2 副本（900101、900102，均 AVAILABLE）
INSERT INTO book_copies (id, book_id, status) VALUES (900101, 9001, 'AVAILABLE');
INSERT INTO book_copies (id, book_id, status) VALUES (900102, 9001, 'AVAILABLE');
-- 《代码整洁之道》×3 副本（900201、900202、900203；900201 在借中 BORROWED，其余可借）
INSERT INTO book_copies (id, book_id, status) VALUES (900201, 9002, 'BORROWED');
INSERT INTO book_copies (id, book_id, status) VALUES (900202, 9002, 'AVAILABLE');
INSERT INTO book_copies (id, book_id, status) VALUES (900203, 9002, 'AVAILABLE');
-- 《算法导论》×1 副本（900301，已借出 BORROWED）
INSERT INTO book_copies (id, book_id, status) VALUES (900301, 9003, 'BORROWED');

-- ============ 借阅记录：在借 ×1 + 逾期 ×1（日期相对启动日构造） ============
-- 在借：张三(1) 借《代码整洁之道》(书9002，副本900201)，借期14天，未超期未归还
INSERT INTO loans (id, reader_id, book_id, book_copy_id, borrowed_at, due_date, returned_at)
VALUES (1, 1, 9002, 900201, CURRENT_DATE - 10, CURRENT_DATE + 4, NULL);
-- 逾期：李四(2) 借《算法导论》(书9003，副本900301)，借期14天，已超期未归还（可展示逾期费用）
INSERT INTO loans (id, reader_id, book_id, book_copy_id, borrowed_at, due_date, returned_at)
VALUES (2, 2, 9003, 900301, CURRENT_DATE - 30, CURRENT_DATE - 16, NULL);
ALTER TABLE loans ALTER COLUMN id RESTART WITH 3;

-- ============ 预约队列：王五预约《算法导论》（该书唯一副本被借出 → WAITING） ============
INSERT INTO reservations (id, reader_id, book_id, status, created_at)
VALUES (1, 3, 9003, 'WAITING', CURRENT_TIMESTAMP);
ALTER TABLE reservations ALTER COLUMN id RESTART WITH 2;