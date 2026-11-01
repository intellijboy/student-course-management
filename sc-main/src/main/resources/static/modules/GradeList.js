// ============================================
// 成绩管理组件：登记/修改/删除 + 按学生/按课程查询
// ============================================

import { request } from './api.js';

const { ref, reactive, computed, onMounted } = Vue;

export default {
  name: 'GradeList',
  props: {
    notify: { type: Function, required: true }
  },
  setup(props) {
    const notify = props.notify;

    const students = ref([]);
    const courses = ref([]);

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

    async function loadStudents() {
      try {
        students.value = await request('/api/students');
      } catch (e) {
        notify(e.message, 'error');
      }
    }

    async function loadCourses() {
      try {
        courses.value = await request('/api/courses');
      } catch (e) {
        notify(e.message, 'error');
      }
    }

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
      students,
      courses,
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
  template: `
    <section>
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
    </section>
  `
};
