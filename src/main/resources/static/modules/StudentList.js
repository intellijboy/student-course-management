// ============================================
// 学生管理组件：列表 + 新增/编辑/删除
// ============================================

import { request } from './api.js';

const { ref, reactive, onMounted } = Vue;

export default {
  name: 'StudentList',
  props: {
    notify: { type: Function, required: true }
  },
  setup(props) {
    const notify = props.notify;

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

    onMounted(loadStudents);

    return {
      students,
      studentForm,
      studentModalOpen,
      studentLoading,
      openCreateStudent,
      openEditStudent,
      saveStudent,
      deleteStudent
    };
  },
  template: `
    <section>
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
    </section>
  `
};
