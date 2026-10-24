// ============================================
// 学生 / 课程 / 成绩 管理系统前端
// 基于 Vue3（全局构建版）+ fetch 调用后端 REST API
// ============================================

const { createApp, ref, reactive, computed, onMounted } = Vue;

// ---------- 通用请求工具 ----------
async function request(url, method, body) {
  method = method || 'GET';
  const options = { method: method, headers: { 'Content-Type': 'application/json' } };
  if (body !== undefined && method !== 'GET') {
    options.body = JSON.stringify(body);
  }
  const res = await fetch(url, options);
  if (!res.ok) {
    let message = '请求失败 (HTTP ' + res.status + ')';
    try {
      const data = await res.json();
      if (data && (data.message || data.error)) {
        message = data.message || data.error;
      }
    } catch (e) { /* 忽略非 JSON 响应 */ }
    throw new Error(message);
  }
  if (res.status === 204) return null;
  return res.json();
}

// ---------- 主应用 ----------
const App = {
  setup() {
    // 当前激活的标签页
    const activeTab = ref('students');

    // 全局提示信息
    const message = reactive({ type: '', text: '', show: false });
    function notify(text, type) {
      type = type || 'success';
      message.type = type;
      message.text = text;
      message.show = true;
      setTimeout(function () { message.show = false; }, 3000);
    }

    // ---------- 学生管理 ----------
    const students = ref([]);
    const studentForm = reactive({ id: null, name: '', email: '' });
    const studentModalOpen = ref(false);
    const studentLoading = ref(false);

    async function loadStudents() {
      studentLoading.value = true;
      try {
        students.value = await request('/api/students');
      } catch (e) {
        notify(e.message, 'error');
      } finally {
        studentLoading.value = false;
      }
    }

    function openCreateStudent() {
      Object.assign(studentForm, { id: null, name: '', email: '' });
      studentModalOpen.value = true;
    }

    function openEditStudent(s) {
      Object.assign(studentForm, { id: s.id, name: s.name, email: s.email });
      studentModalOpen.value = true;
    }

    async function saveStudent() {
      if (!studentForm.name.trim()) { notify('请输入学生姓名', 'error'); return; }
      if (!studentForm.email.trim()) { notify('请输入邮箱', 'error'); return; }
      const payload = { name: studentForm.name.trim(), email: studentForm.email.trim() };
      try {
        if (studentForm.id === null) {
          await request('/api/students', 'POST', payload);
          notify('学生创建成功');
        } else {
          await request('/api/students/' + studentForm.id, 'PUT', payload);
          notify('学生更新成功');
        }
        studentModalOpen.value = false;
        await loadStudents();
      } catch (e) {
        notify(e.message, 'error');
      }
    }

    async function deleteStudent(s) {
      if (!confirm('确认删除学生「' + s.name + '」？其成绩记录可能一并删除。')) return;
      try {
        await request('/api/students/' + s.id, 'DELETE');
        notify('学生删除成功');
        await loadStudents();
      } catch (e) {
        notify(e.message, 'error');
      }
    }

    // ---------- 课程管理 ----------
    const courses = ref([]);
    const courseForm = reactive({ id: null, name: '', credits: 1 });
    const courseModalOpen = ref(false);
    const courseLoading = ref(false);

    async function loadCourses() {
      courseLoading.value = true;
      try {
        courses.value = await request('/api/courses');
      } catch (e) {
        notify(e.message, 'error');
      } finally {
        courseLoading.value = false;
      }
    }

    function openCreateCourse() {
      Object.assign(courseForm, { id: null, name: '', credits: 1 });
      courseModalOpen.value = true;
    }

    function openEditCourse(c) {
      Object.assign(courseForm, { id: c.id, name: c.name, credits: c.credits });
      courseModalOpen.value = true;
    }

    async function saveCourse() {
      if (!courseForm.name.trim()) { notify('请输入课程名称', 'error'); return; }
      const credits = Number(courseForm.credits);
      if (isNaN(credits) || credits < 0) { notify('请输入有效的学分', 'error'); return; }
      const payload = { name: courseForm.name.trim(), credits: credits };
      try {
        if (courseForm.id === null) {
          await request('/api/courses', 'POST', payload);
          notify('课程创建成功');
        } else {
          await request('/api/courses/' + courseForm.id, 'PUT', payload);
          notify('课程更新成功');
        }
        courseModalOpen.value = false;
        await loadCourses();
      } catch (e) {
        notify(e.message, 'error');
      }
    }

    async function deleteCourse(c) {
      if (!confirm('确认删除课程「' + c.name + '」？其成绩记录可能一并删除。')) return;
      try {
        await request('/api/courses/' + c.id, 'DELETE');
        notify('课程删除成功');
        await loadCourses();
      } catch (e) {
        notify(e.message, 'error');
      }
    }


    // ---------- 成绩管理 ----------
    const gradesByStudent = ref([]);
    const gradesByCourse = ref([]);
    const gradeLoading = ref(false);

    // 成绩表单（登记 / 修改共用）
    const gradeForm = reactive({
      id: null,
      studentId: null,
      courseId: null,
      score: '',
      studentName: ''
    });
    const gradeModalOpen = ref(false);

    // 成绩查询视图：'student' 按学生查，'course' 按课程查
    const gradeView = ref('student');
    const selectedStudentId = ref('');
    const selectedCourseId = ref('');
    const selectedStudentName = ref('');
    const selectedCourseName = ref('');

    // 成绩下拉可选分数范围 0-100
    const scoreOptions = computed(function () {
      const list = [];
      for (let i = 0; i <= 100; i += 1) list.push(i);
      return list;
    });

    // 按学生查询成绩
    async function loadGradesByStudent(studentId) {
      gradeLoading.value = true;
      try {
        const id = studentId || selectedStudentId.value;
        if (!id) { gradesByStudent.value = []; return; }
        const stu = students.value.find(function (s) { return String(s.id) === String(id); });
        selectedStudentName.value = stu ? stu.name : '';
        gradesByStudent.value = await request('/api/students/' + id + '/grades');
      } catch (e) {
        notify(e.message, 'error');
        gradesByStudent.value = [];
      } finally {
        gradeLoading.value = false;
      }
    }

    // 按课程查询成绩
    async function loadGradesByCourse(courseId) {
      gradeLoading.value = true;
      try {
        const id = courseId || selectedCourseId.value;
        if (!id) { gradesByCourse.value = []; return; }
        const c = courses.value.find(function (x) { return String(x.id) === String(id); });
        selectedCourseName.value = c ? c.name : '';
        gradesByCourse.value = await request('/api/courses/' + id + '/grades');
      } catch (e) {
        notify(e.message, 'error');
        gradesByCourse.value = [];
      } finally {
        gradeLoading.value = false;
      }
    }

    function switchGradeView(view) {
      gradeView.value = view;
      if (view === 'student' && selectedStudentId.value) {
        loadGradesByStudent();
      } else if (view === 'course' && selectedCourseId.value) {
        loadGradesByCourse();
      }
    }

    // 打开“登记成绩”弹窗（可默认选中当前学生）
    function openCreateGrade(studentId, studentName) {
      Object.assign(gradeForm, {
        id: null,
        studentId: studentId || null,
        courseId: null,
        score: '',
        studentName: studentName || ''
      });
      gradeModalOpen.value = true;
    }

    // 打开“修改成绩”弹窗
    function openEditGrade(g) {
      Object.assign(gradeForm, {
        id: g.id,
        studentId: g.studentId,
        courseId: g.courseId,
        score: g.score,
        studentName: studentNameOf(g.studentId)
      });
      gradeModalOpen.value = true;
    }

    // 校验成绩范围 0-100
    function validateScore(score) {
      const n = Number(score);
      if (isNaN(n) || n < 0 || n > 100) {
        notify('成绩必须是 0-100 之间的数字', 'error');
        return false;
      }
      return true;
    }

    async function saveGrade() {
      if (!gradeForm.studentId) { notify('请选择学生', 'error'); return; }
      if (!gradeForm.courseId) { notify('请选择课程', 'error'); return; }
      if (!validateScore(gradeForm.score)) return;
      const payload = {
        studentId: Number(gradeForm.studentId),
        courseId: Number(gradeForm.courseId),
        score: Number(gradeForm.score)
      };
      try {
        if (gradeForm.id === null) {
          await request('/api/grades', 'POST', payload);
          notify('成绩登记成功');
        } else {
          await request('/api/grades/' + gradeForm.id, 'PUT', payload);
          notify('成绩修改成功');
        }
        gradeModalOpen.value = false;
        refreshGradeViews();
      } catch (e) {
        notify(e.message, 'error');
      }
    }

    async function deleteGrade(g) {
      if (!confirm('确认删除该条成绩记录？')) return;
      try {
        await request('/api/grades/' + g.id, 'DELETE');
        notify('成绩删除成功');
        refreshGradeViews();
      } catch (e) {
        notify(e.message, 'error');
      }
    }

    // 刷新当前正在展示的成绩视图
    function refreshGradeViews() {
      if (gradeView.value === 'student' && selectedStudentId.value) loadGradesByStudent();
      if (gradeView.value === 'course' && selectedCourseId.value) loadGradesByCourse();
    }

    // 辅助方法：把 id 映射为姓名
    function studentNameOf(id) {
      const s = students.value.find(function (x) { return String(x.id) === String(id); });
      return s ? s.name : '#' + id;
    }
    function courseNameOf(id) {
      const c = courses.value.find(function (x) { return String(x.id) === String(id); });
      return c ? c.name : '#' + id;
    }


    // ---------- 初始化 ----------
    onMounted(async function () {
      await Promise.all([loadStudents(), loadCourses()]);
      if (students.value.length > 0) {
        selectedStudentId.value = students.value[0].id;
        loadGradesByStudent();
      }
      if (courses.value.length > 0) {
        selectedCourseId.value = courses.value[0].id;
        loadGradesByCourse();
      }
    });

    return {
      activeTab,
      message,
      students,
      studentForm,
      studentModalOpen,
      studentLoading,
      loadStudents,
      openCreateStudent,
      openEditStudent,
      saveStudent,
      deleteStudent,
      courses,
      courseForm,
      courseModalOpen,
      courseLoading,
      loadCourses,
      openCreateCourse,
      openEditCourse,
      saveCourse,
      deleteCourse,
      gradesByStudent,
      gradesByCourse,
      gradeLoading,
      gradeForm,
      gradeModalOpen,
      gradeView,
      selectedStudentId,
      selectedCourseId,
      selectedStudentName,
      selectedCourseName,
      scoreOptions,
      loadGradesByStudent,
      loadGradesByCourse,
      switchGradeView,
      openCreateGrade,
      openEditGrade,
      saveGrade,
      deleteGrade,
      studentNameOf,
      courseNameOf
    };
  },


  // ---------- 模板 ----------
  template: `
    <div class="min-h-screen bg-gray-100">
      <!-- 顶部导航 -->
      <header class="bg-white shadow">
        <div class="max-w-6xl mx-auto px-4 py-4 flex items-center justify-between">
          <h1 class="text-xl font-bold text-gray-800">学生课程成绩管理系统</h1>
          <nav class="flex space-x-2">
            <button @click="activeTab='students'" :class="activeTab==='students' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'" class="px-4 py-2 rounded-lg font-medium">学生管理</button>
            <button @click="activeTab='courses'" :class="activeTab==='courses' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'" class="px-4 py-2 rounded-lg font-medium">课程管理</button>
            <button @click="activeTab='grades'" :class="activeTab==='grades' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'" class="px-4 py-2 rounded-lg font-medium">成绩管理</button>
          </nav>
        </div>
      </header>

      <!-- 全局提示 -->
      <div v-if="message.show" class="fixed top-4 left-1/2 transform -translate-x-1/2 z-50 px-4 py-2 rounded-lg shadow text-white" :class="message.type==='error' ? 'bg-red-500' : 'bg-green-500'">
        {{ message.text }}
      </div>

      <main class="max-w-6xl mx-auto px-4 py-6">
        <!-- ==================== 学生管理 ==================== -->
        <section v-if="activeTab==='students'">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-800">学生列表</h2>
            <button @click="openCreateStudent" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium">+ 新增学生</button>
          </div>
          <div class="bg-white rounded-lg shadow overflow-hidden">
            <table class="w-full">
              <thead class="bg-gray-50 text-left text-sm text-gray-500">
                <tr>
                  <th class="px-4 py-3">ID</th>
                  <th class="px-4 py-3">姓名</th>
                  <th class="px-4 py-3">邮箱</th>
                  <th class="px-4 py-3 text-right">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100">
                <tr v-if="studentLoading"><td colspan="4" class="px-4 py-8 text-center text-gray-400">加载中...</td></tr>
                <tr v-else-if="students.length===0"><td colspan="4" class="px-4 py-8 text-center text-gray-400">暂无学生，点击右上角新增</td></tr>
                <tr v-for="s in students" :key="s.id">
                  <td class="px-4 py-3 text-gray-500">{{ s.id }}</td>
                  <td class="px-4 py-3 font-medium text-gray-800">{{ s.name }}</td>
                  <td class="px-4 py-3 text-gray-600">{{ s.email }}</td>
                  <td class="px-4 py-3 text-right space-x-2">
                    <button @click="openEditStudent(s)" class="text-blue-600 hover:underline">编辑</button>
                    <button @click="deleteStudent(s)" class="text-red-600 hover:underline">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <!-- ==================== 课程管理 ==================== -->
        <section v-if="activeTab==='courses'">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-800">课程列表</h2>
            <button @click="openCreateCourse" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium">+ 新增课程</button>
          </div>
          <div class="bg-white rounded-lg shadow overflow-hidden">
            <table class="w-full">
              <thead class="bg-gray-50 text-left text-sm text-gray-500">
                <tr>
                  <th class="px-4 py-3">ID</th>
                  <th class="px-4 py-3">课程名称</th>
                  <th class="px-4 py-3">学分</th>
                  <th class="px-4 py-3 text-right">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100">
                <tr v-if="courseLoading"><td colspan="4" class="px-4 py-8 text-center text-gray-400">加载中...</td></tr>
                <tr v-else-if="courses.length===0"><td colspan="4" class="px-4 py-8 text-center text-gray-400">暂无课程，点击右上角新增</td></tr>
                <tr v-for="c in courses" :key="c.id">
                  <td class="px-4 py-3 text-gray-500">{{ c.id }}</td>
                  <td class="px-4 py-3 font-medium text-gray-800">{{ c.name }}</td>
                  <td class="px-4 py-3 text-gray-600">{{ c.credits }}</td>
                  <td class="px-4 py-3 text-right space-x-2">
                    <button @click="openEditCourse(c)" class="text-blue-600 hover:underline">编辑</button>
                    <button @click="deleteCourse(c)" class="text-red-600 hover:underline">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>


        <!-- ==================== 成绩管理 ==================== -->
        <section v-if="activeTab==='grades'">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-800">成绩管理</h2>
            <button @click="openCreateGrade(null)" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium">+ 登记成绩</button>
          </div>

          <!-- 按学生 / 按课程 切换 -->
          <div class="flex space-x-2 mb-4">
            <button @click="switchGradeView('student')" :class="gradeView==='student' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'" class="px-4 py-2 rounded-lg font-medium">按学生查看</button>
            <button @click="switchGradeView('course')" :class="gradeView==='course' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'" class="px-4 py-2 rounded-lg font-medium">按课程查看</button>
          </div>

          <!-- 按学生查看 -->
          <div v-if="gradeView==='student'" class="bg-white rounded-lg shadow p-4 mb-4">
            <div class="flex items-center space-x-3">
              <label class="text-sm text-gray-600">选择学生：</label>
              <select v-model="selectedStudentId" @change="loadGradesByStudent()" class="border border-gray-300 rounded-lg px-3 py-2 text-sm">
                <option value="">-- 请选择学生 --</option>
                <option v-for="s in students" :key="s.id" :value="s.id">{{ s.name }}（{{ s.email }}）</option>
              </select>
              <span v-if="selectedStudentName" class="text-sm text-gray-500">当前学生：{{ selectedStudentName }}</span>
            </div>
          </div>
          <div v-if="gradeView==='student'" class="bg-white rounded-lg shadow overflow-hidden">
            <table class="w-full">
              <thead class="bg-gray-50 text-left text-sm text-gray-500">
                <tr>
                  <th class="px-4 py-3">ID</th>
                  <th class="px-4 py-3">课程</th>
                  <th class="px-4 py-3">成绩</th>
                  <th class="px-4 py-3 text-right">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100">
                <tr v-if="gradeLoading"><td colspan="4" class="px-4 py-8 text-center text-gray-400">加载中...</td></tr>
                <tr v-else-if="gradesByStudent.length===0"><td colspan="4" class="px-4 py-8 text-center text-gray-400">暂无成绩记录</td></tr>
                <tr v-for="g in gradesByStudent" :key="g.id">
                  <td class="px-4 py-3 text-gray-500">{{ g.id }}</td>
                  <td class="px-4 py-3 font-medium text-gray-800">{{ courseNameOf(g.courseId) }}</td>
                  <td class="px-4 py-3 text-gray-600">{{ g.score }}</td>
                  <td class="px-4 py-3 text-right space-x-2">
                    <button @click="openEditGrade(g)" class="text-blue-600 hover:underline">修改</button>
                    <button @click="deleteGrade(g)" class="text-red-600 hover:underline">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 按课程查看 -->
          <div v-if="gradeView==='course'" class="bg-white rounded-lg shadow p-4 mb-4">
            <div class="flex items-center space-x-3">
              <label class="text-sm text-gray-600">选择课程：</label>
              <select v-model="selectedCourseId" @change="loadGradesByCourse()" class="border border-gray-300 rounded-lg px-3 py-2 text-sm">
                <option value="">-- 请选择课程 --</option>
                <option v-for="c in courses" :key="c.id" :value="c.id">{{ c.name }}（{{ c.credits }} 学分）</option>
              </select>
              <span v-if="selectedCourseName" class="text-sm text-gray-500">当前课程：{{ selectedCourseName }}</span>
            </div>
          </div>
          <div v-if="gradeView==='course'" class="bg-white rounded-lg shadow overflow-hidden">
            <table class="w-full">
              <thead class="bg-gray-50 text-left text-sm text-gray-500">
                <tr>
                  <th class="px-4 py-3">ID</th>
                  <th class="px-4 py-3">学生</th>
                  <th class="px-4 py-3">成绩</th>
                  <th class="px-4 py-3 text-right">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100">
                <tr v-if="gradeLoading"><td colspan="4" class="px-4 py-8 text-center text-gray-400">加载中...</td></tr>
                <tr v-else-if="gradesByCourse.length===0"><td colspan="4" class="px-4 py-8 text-center text-gray-400">暂无成绩记录</td></tr>
                <tr v-for="g in gradesByCourse" :key="g.id">
                  <td class="px-4 py-3 text-gray-500">{{ g.id }}</td>
                  <td class="px-4 py-3 font-medium text-gray-800">{{ studentNameOf(g.studentId) }}</td>
                  <td class="px-4 py-3 text-gray-600">{{ g.score }}</td>
                  <td class="px-4 py-3 text-right space-x-2">
                    <button @click="openEditGrade(g)" class="text-blue-600 hover:underline">修改</button>
                    <button @click="deleteGrade(g)" class="text-red-600 hover:underline">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </main>

      <!-- ==================== 学生 / 课程 / 成绩 通用弹窗 ==================== -->

      <!-- 学生表单弹窗 -->
      <div v-if="studentModalOpen" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-40">
        <div class="bg-white rounded-lg shadow-lg w-96 p-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">{{ studentForm.id === null ? '新增学生' : '编辑学生' }}</h3>
          <div class="space-y-4">
            <div>
              <label class="block text-sm text-gray-600 mb-1">姓名</label>
              <input v-model="studentForm.name" type="text" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" placeholder="请输入姓名">
            </div>
            <div>
              <label class="block text-sm text-gray-600 mb-1">邮箱</label>
              <input v-model="studentForm.email" type="email" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" placeholder="请输入邮箱">
            </div>
          </div>
          <div class="flex justify-end space-x-2 mt-6">
            <button @click="studentModalOpen=false" class="px-4 py-2 rounded-lg bg-gray-200 text-gray-700">取消</button>
            <button @click="saveStudent" class="px-4 py-2 rounded-lg bg-blue-600 text-white">保存</button>
          </div>
        </div>
      </div>

      <!-- 课程表单弹窗 -->
      <div v-if="courseModalOpen" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-40">
        <div class="bg-white rounded-lg shadow-lg w-96 p-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">{{ courseForm.id === null ? '新增课程' : '编辑课程' }}</h3>
          <div class="space-y-4">
            <div>
              <label class="block text-sm text-gray-600 mb-1">课程名称</label>
              <input v-model="courseForm.name" type="text" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" placeholder="请输入课程名称">
            </div>
            <div>
              <label class="block text-sm text-gray-600 mb-1">学分</label>
              <input v-model="courseForm.credits" type="number" min="0" step="1" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" placeholder="请输入学分">
            </div>
          </div>
          <div class="flex justify-end space-x-2 mt-6">
            <button @click="courseModalOpen=false" class="px-4 py-2 rounded-lg bg-gray-200 text-gray-700">取消</button>
            <button @click="saveCourse" class="px-4 py-2 rounded-lg bg-blue-600 text-white">保存</button>
          </div>
        </div>
      </div>

      <!-- 成绩表单弹窗（登记 / 修改共用） -->
      <div v-if="gradeModalOpen" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-40">
        <div class="bg-white rounded-lg shadow-lg w-96 p-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">{{ gradeForm.id === null ? '登记成绩' : '修改成绩' }}</h3>
          <div class="space-y-4">
            <div>
              <label class="block text-sm text-gray-600 mb-1">学生</label>
              <select v-model="gradeForm.studentId" :disabled="gradeForm.id !== null" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm">
                <option value="">-- 请选择学生 --</option>
                <option v-for="s in students" :key="s.id" :value="s.id">{{ s.name }}（{{ s.email }}）</option>
              </select>
            </div>
            <div>
              <label class="block text-sm text-gray-600 mb-1">课程</label>
              <select v-model="gradeForm.courseId" :disabled="gradeForm.id !== null" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm">
                <option value="">-- 请选择课程 --</option>
                <option v-for="c in courses" :key="c.id" :value="c.id">{{ c.name }}（{{ c.credits }} 学分）</option>
              </select>
            </div>
            <div>
              <label class="block text-sm text-gray-600 mb-1">成绩（范围 0-100）</label>
              <input v-model="gradeForm.score" type="number" min="0" max="100" step="1" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" placeholder="0-100">
              <p class="text-xs text-gray-400 mt-1">请输入 0-100 之间的整数分数</p>
            </div>
          </div>
          <div class="flex justify-end space-x-2 mt-6">
            <button @click="gradeModalOpen=false" class="px-4 py-2 rounded-lg bg-gray-200 text-gray-700">取消</button>
            <button @click="saveGrade" class="px-4 py-2 rounded-lg bg-blue-600 text-white">保存</button>
          </div>
        </div>
      </div>
    </div>
  `
};

createApp(App).mount('#app');

