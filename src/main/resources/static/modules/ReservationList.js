// ============================================
// 预约与逾期组件：创建预约 + 逾期列表（含费用）
// ============================================

import { request } from './api.js';

const { ref, reactive, onMounted } = Vue;

export default {
  name: 'ReservationList',
  props: {
    notify: { type: Function, required: true }
  },
  setup(props) {
    const notify = props.notify;

    // 下拉数据源：读者（学生）与图书
    const students = ref([]);
    const books = ref([]);

    // 预约表单
    const reservationForm = reactive({ readerId: '', bookId: '' });

    // 逾期列表
    const overdueList = ref([]);
    const overdueLoading = ref(false);

    async function loadStudents() {
      try {
        students.value = await request('/api/students');
      } catch (e) {
        notify(e.message, 'error');
      }
    }

    async function loadBooks() {
      try {
        books.value = await request('/api/books');
      } catch (e) {
        notify(e.message, 'error');
      }
    }

    // 创建预约
    async function createReservation() {
      if (!reservationForm.readerId) { notify('请选择读者', 'error'); return; }
      if (!reservationForm.bookId) { notify('请选择图书', 'error'); return; }
      const payload = { readerId: Number(reservationForm.readerId), bookId: Number(reservationForm.bookId) };
      try {
        await request('/api/reservations', 'POST', payload);
        notify('预约创建成功');
        reservationForm.bookId = '';
      } catch (e) {
        notify(e.message, 'error');
      }
    }

    // 加载逾期列表
    async function loadOverdue() {
      overdueLoading.value = true;
      try {
        overdueList.value = await request('/api/loans/overdue');
      } catch (e) {
        notify(e.message, 'error');
      } finally {
        overdueLoading.value = false;
      }
    }

    // 辅助：id 映射读者姓名
    function studentNameOf(id) {
      const s = students.value.find(function (x) { return String(x.id) === String(id); });
      return s ? s.name : '#' + id;
    }

    onMounted(async function () {
      await Promise.all([loadStudents(), loadBooks(), loadOverdue()]);
    });

    return {
      students,
      books,
      reservationForm,
      overdueList,
      overdueLoading,
      createReservation,
      studentNameOf
    };
  },
  template: `
    <section>
      <!-- 创建预约 -->
      <div class="bg-white rounded-lg shadow p-4 mb-4">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">创建预约</h3>
        <div class="flex items-end space-x-3">
          <div class="flex-1">
            <label class="block text-sm text-gray-600 mb-1">读者</label>
            <select v-model="reservationForm.readerId" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm">
              <option value="">-- 请选择读者 --</option>
              <option v-for="s in students" :key="s.id" :value="s.id">{{ s.name }}（{{ s.email }}）</option>
            </select>
          </div>
          <div class="flex-1">
            <label class="block text-sm text-gray-600 mb-1">图书</label>
            <select v-model="reservationForm.bookId" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm">
              <option value="">-- 请选择图书 --</option>
              <option v-for="b in books" :key="b.id" :value="b.id">{{ b.title }}（{{ b.author }}，可借 {{ b.availableCopies }}）</option>
            </select>
          </div>
          <button @click="createReservation" class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-medium">预约</button>
        </div>
        <p v-if="students.length===0" class="text-xs text-gray-400 mt-2">暂无读者可选，请先在学生管理中创建学生。</p>
      </div>

      <!-- 逾期列表 -->
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-semibold text-gray-800">逾期列表</h3>
        <button @click="loadOverdue" class="bg-gray-200 hover:bg-gray-300 text-gray-700 px-4 py-2 rounded-lg font-medium">刷新</button>
      </div>
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50 text-left text-sm text-gray-500">
            <tr>
              <th class="px-4 py-3">借阅ID</th>
              <th class="px-4 py-3">读者</th>
              <th class="px-4 py-3">副本ID</th>
              <th class="px-4 py-3">应还日期</th>
              <th class="px-4 py-3">逾期费用</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-if="overdueLoading"><td colspan="5" class="px-4 py-8 text-center text-gray-400">加载中...</td></tr>
            <tr v-else-if="overdueList.length===0"><td colspan="5" class="px-4 py-8 text-center text-gray-400">暂无逾期记录</td></tr>
            <tr v-for="o in overdueList" :key="o.loanId">
              <td class="px-4 py-3 text-gray-500">{{ o.loanId }}</td>
              <td class="px-4 py-3 font-medium text-gray-800">{{ studentNameOf(o.readerId) }}</td>
              <td class="px-4 py-3 text-gray-600">{{ o.bookCopyId }}</td>
              <td class="px-4 py-3 text-gray-600">{{ o.dueDate }}</td>
              <td class="px-4 py-3 text-red-600">{{ o.fee }} 元</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  `
};
