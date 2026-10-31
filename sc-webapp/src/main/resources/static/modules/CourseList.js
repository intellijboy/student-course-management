// ============================================
// 课程管理组件：列表 + 新增/编辑/删除
// ============================================

import { request } from './api.js';

const { ref, reactive, onMounted } = Vue;

export default {
  name: 'CourseList',
  props: {
    notify: { type: Function, required: true }
  },
  setup(props) {
    const notify = props.notify;

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

    onMounted(loadCourses);

    return {
      courses,
      courseForm,
      courseModalOpen,
      courseLoading,
      openCreateCourse,
      openEditCourse,
      saveCourse,
      deleteCourse
    };
  },
  template: `
    <section>
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
    </section>
  `
};
