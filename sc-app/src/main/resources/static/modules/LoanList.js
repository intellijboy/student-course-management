// ============================================
// 借还管理组件：借阅 + 按读者查询在借记录 + 归还
// ============================================

import { request } from './api.js';

const { ref, reactive, onMounted } = Vue;

export default {
  name: 'LoanList',
  props: {
    notify: { type: Function, required: true }
  },
  setup(props) {
    const notify = props.notify;

    // 下拉数据源：读者（学生）与图书
    const students = ref([]);
    const books = ref([]);

    // 借阅表单
    const loanForm = reactive({ readerId: '', bookId: '' });

    // 按读者查询在借记录
    const selectedReaderId = ref('');
    const readerLoans = ref([]);
    const loanLoading = ref(false);

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

    // 借阅
    async function borrow() {
      if (!loanForm.readerId) { notify('请选择读者', 'error'); return; }
      if (!loanForm.bookId) { notify('请选择图书', 'error'); return; }
      const payload = { readerId: Number(loanForm.readerId), bookId: Number(loanForm.bookId) };
      try {
        await request('/api/loans', 'POST', payload);
        notify('借阅成功');
        loanForm.bookId = '';
        // 若当前查询的正是借阅读者，刷新其记录
        if (String(selectedReaderId.value) === String(loanForm.readerId)) {
          await loadReaderLoans();
        }
      } catch (e) {
        notify(e.message, 'error');
      }
    }

    // 按读者查询在借记录
    async function loadReaderLoans() {
      const id = selectedReaderId.value;
      if (!id) { readerLoans.value = []; return; }
      loanLoading.value = true;
      try {
        readerLoans.value = await request('/api/readers/' + id + '/loans');
      } catch (e) {
        notify(e.message, 'error');
        readerLoans.value = [];
      } finally {
        loanLoading.value = false;
      }
    }

    // 归还
    async function returnLoan(loan) {
      if (!confirm('确认归还该在借记录？')) return;
      try {
        const result = await request('/api/loans/' + loan.id + '/return', 'POST');
        let text = '归还成功';
        if (result && Number(result.overdueFee) > 0) {
          text += '，逾期费用 ' + result.overdueFee + ' 元';
        }
        notify(text);
        await loadReaderLoans();
      } catch (e) {
        notify(e.message, 'error');
      }
    }

    // 辅助：id 映射书名
    function bookNameOf(id) {
      const b = books.value.find(function (x) { return String(x.id) === String(id); });
      return b ? b.title : '#' + id;
    }

    onMounted(async function () {
      await Promise.all([loadStudents(), loadBooks()]);
      if (students.value.length > 0) {
        selectedReaderId.value = students.value[0].id;
        loadReaderLoans();
      }
    });

    return {
      students,
      books,
      loanForm,
      selectedReaderId,
      readerLoans,
      loanLoading,
      borrow,
      loadReaderLoans,
      returnLoan,
      bookNameOf
    };
  },
  template: `
    <section>
      <!-- 借阅表单 -->
      <div class="bg-white rounded-lg shadow p-4 mb-4">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">借阅</h3>
        <div class="flex items-end space-x-3">
          <div class="flex-1">
            <label class="block text-sm text-gray-600 mb-1">读者</label>
            <select v-model="loanForm.readerId" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm">
              <option value="">-- 请选择读者 --</option>
              <option v-for="s in students" :key="s.id" :value="s.id">{{ s.name }}（{{ s.email }}）</option>
            </select>
          </div>
          <div class="flex-1">
            <label class="block text-sm text-gray-600 mb-1">图书</label>
            <select v-model="loanForm.bookId" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm">
              <option value="">-- 请选择图书 --</option>
              <option v-for="b in books" :key="b.id" :value="b.id">{{ b.title }}（{{ b.author }}，可借 {{ b.availableCopies }}）</option>
            </select>
          </div>
          <button @click="borrow" class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-medium">借阅</button>
        </div>
        <p v-if="students.length===0" class="text-xs text-gray-400 mt-2">暂无读者可选，请先在学生管理中创建学生。</p>
      </div>

      <!-- 按读者查询在借记录 -->
      <div class="bg-white rounded-lg shadow p-4 mb-4">
        <div class="flex items-center space-x-3">
          <label class="text-sm text-gray-600">按读者查询在借记录：</label>
          <select v-model="selectedReaderId" @change="loadReaderLoans()" class="border border-gray-300 rounded-lg px-3 py-2 text-sm">
            <option value="">-- 请选择读者 --</option>
            <option v-for="s in students" :key="s.id" :value="s.id">{{ s.name }}（{{ s.email }}）</option>
          </select>
        </div>
      </div>
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50 text-left text-sm text-gray-500">
            <tr>
              <th class="px-4 py-3">ID</th>
              <th class="px-4 py-3">图书</th>
              <th class="px-4 py-3">副本ID</th>
              <th class="px-4 py-3">借出日期</th>
              <th class="px-4 py-3">应还日期</th>
              <th class="px-4 py-3 text-right">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-if="loanLoading"><td colspan="6" class="px-4 py-8 text-center text-gray-400">加载中...</td></tr>
            <tr v-else-if="readerLoans.length===0"><td colspan="6" class="px-4 py-8 text-center text-gray-400">该读者暂无在借记录，请先选择读者查询</td></tr>
            <tr v-for="l in readerLoans" :key="l.id">
              <td class="px-4 py-3 text-gray-500">{{ l.id }}</td>
              <td class="px-4 py-3 font-medium text-gray-800">{{ bookNameOf(l.bookId) }}</td>
              <td class="px-4 py-3 text-gray-600">{{ l.bookCopyId }}</td>
              <td class="px-4 py-3 text-gray-600">{{ l.borrowedAt }}</td>
              <td class="px-4 py-3 text-gray-600">{{ l.dueDate }}</td>
              <td class="px-4 py-3 text-right">
                <button @click="returnLoan(l)" class="text-green-600 hover:underline">归还</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  `
};
