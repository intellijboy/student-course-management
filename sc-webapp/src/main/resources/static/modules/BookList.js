// ============================================
// 图书管理组件：列表 + 登记图书 + 查看馆藏状态与预约队列
// ============================================

import { request } from './api.js';

const { ref, reactive, onMounted } = Vue;

export default {
  name: 'BookList',
  props: {
    notify: { type: Function, required: true }
  },
  setup(props) {
    const notify = props.notify;

    const books = ref([]);
    const bookLoading = ref(false);

    // 登记图书表单
    const bookForm = reactive({ title: '', author: '', copyCount: 1 });
    const bookModalOpen = ref(false);

    // 馆藏状态弹窗
    const statusModalOpen = ref(false);
    const currentStatus = ref(null);
    const statusLoading = ref(false);

    async function loadBooks() {
      bookLoading.value = true;
      try {
        books.value = await request('/api/books');
      } catch (e) {
        notify(e.message, 'error');
      } finally {
        bookLoading.value = false;
      }
    }

    function openCreateBook() {
      Object.assign(bookForm, { title: '', author: '', copyCount: 1 });
      bookModalOpen.value = true;
    }

    async function saveBook() {
      if (!bookForm.title.trim()) { notify('请输入书名', 'error'); return; }
      if (!bookForm.author.trim()) { notify('请输入作者', 'error'); return; }
      const copyCount = Number(bookForm.copyCount);
      if (isNaN(copyCount) || copyCount <= 0) { notify('请输入有效的副本数量', 'error'); return; }
      const payload = { title: bookForm.title.trim(), author: bookForm.author.trim(), copyCount: copyCount };
      try {
        await request('/api/books', 'POST', payload);
        notify('图书登记成功');
        bookModalOpen.value = false;
        await loadBooks();
      } catch (e) {
        notify(e.message, 'error');
      }
    }

    // 查看某本书的馆藏状态与预约队列
    async function viewStatus(book) {
      statusModalOpen.value = true;
      currentStatus.value = null;
      statusLoading.value = true;
      try {
        currentStatus.value = await request('/api/books/' + book.id);
      } catch (e) {
        notify(e.message, 'error');
        statusModalOpen.value = false;
      } finally {
        statusLoading.value = false;
      }
    }

    onMounted(loadBooks);

    return {
      books,
      bookLoading,
      bookForm,
      bookModalOpen,
      statusModalOpen,
      currentStatus,
      statusLoading,
      openCreateBook,
      saveBook,
      viewStatus
    };
  },
  template: `
    <section>
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-semibold text-gray-800">图书列表</h2>
        <button @click="openCreateBook" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium">+ 登记图书</button>
      </div>
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50 text-left text-sm text-gray-500">
            <tr>
              <th class="px-4 py-3">ID</th>
              <th class="px-4 py-3">书名</th>
              <th class="px-4 py-3">作者</th>
              <th class="px-4 py-3">馆藏总数</th>
              <th class="px-4 py-3">可借数量</th>
              <th class="px-4 py-3 text-right">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-if="bookLoading"><td colspan="6" class="px-4 py-8 text-center text-gray-400">加载中...</td></tr>
            <tr v-else-if="books.length===0"><td colspan="6" class="px-4 py-8 text-center text-gray-400">暂无图书，点击右上角登记</td></tr>
            <tr v-for="b in books" :key="b.id">
              <td class="px-4 py-3 text-gray-500">{{ b.id }}</td>
              <td class="px-4 py-3 font-medium text-gray-800">{{ b.title }}</td>
              <td class="px-4 py-3 text-gray-600">{{ b.author }}</td>
              <td class="px-4 py-3 text-gray-600">{{ b.totalCopies }}</td>
              <td class="px-4 py-3 text-gray-600">{{ b.availableCopies }}</td>
              <td class="px-4 py-3 text-right">
                <button @click="viewStatus(b)" class="text-blue-600 hover:underline">查看馆藏</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 登记图书弹窗 -->
      <div v-if="bookModalOpen" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-40">
        <div class="bg-white rounded-lg shadow-lg w-96 p-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">登记图书</h3>
          <div class="space-y-4">
            <div>
              <label class="block text-sm text-gray-600 mb-1">书名</label>
              <input v-model="bookForm.title" type="text" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" placeholder="请输入书名">
            </div>
            <div>
              <label class="block text-sm text-gray-600 mb-1">作者</label>
              <input v-model="bookForm.author" type="text" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" placeholder="请输入作者">
            </div>
            <div>
              <label class="block text-sm text-gray-600 mb-1">副本数量</label>
              <input v-model="bookForm.copyCount" type="number" min="1" step="1" class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" placeholder="请输入副本数量">
            </div>
          </div>
          <div class="flex justify-end space-x-2 mt-6">
            <button @click="bookModalOpen=false" class="px-4 py-2 rounded-lg bg-gray-200 text-gray-700">取消</button>
            <button @click="saveBook" class="px-4 py-2 rounded-lg bg-blue-600 text-white">保存</button>
          </div>
        </div>
      </div>

      <!-- 馆藏状态弹窗 -->
      <div v-if="statusModalOpen" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-40">
        <div class="bg-white rounded-lg shadow-lg w-[480px] p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold text-gray-800">{{ currentStatus ? currentStatus.title : '馆藏状态' }}</h3>
            <button @click="statusModalOpen=false" class="text-gray-400 hover:text-gray-600 text-xl leading-none">&times;</button>
          </div>
          <div v-if="statusLoading" class="py-8 text-center text-gray-400">加载中...</div>
          <div v-else-if="currentStatus">
            <div class="grid grid-cols-3 gap-4 mb-6">
              <div class="bg-gray-50 rounded-lg p-4 text-center">
                <div class="text-2xl font-bold text-gray-800">{{ currentStatus.availableCopies + currentStatus.borrowedCopies }}</div>
                <div class="text-sm text-gray-500 mt-1">馆藏总数</div>
              </div>
              <div class="bg-gray-50 rounded-lg p-4 text-center">
                <div class="text-2xl font-bold text-green-600">{{ currentStatus.availableCopies }}</div>
                <div class="text-sm text-gray-500 mt-1">可借数量</div>
              </div>
              <div class="bg-gray-50 rounded-lg p-4 text-center">
                <div class="text-2xl font-bold text-red-500">{{ currentStatus.borrowedCopies }}</div>
                <div class="text-sm text-gray-500 mt-1">在借数量</div>
              </div>
            </div>
            <h4 class="text-sm font-semibold text-gray-700 mb-2">预约队列</h4>
            <div v-if="currentStatus.reservationQueue.length===0" class="py-6 text-center text-gray-400">暂无预约</div>
            <div v-else class="bg-gray-50 rounded-lg overflow-hidden">
              <table class="w-full">
                <thead class="text-left text-sm text-gray-500">
                  <tr>
                    <th class="px-4 py-2">预约ID</th>
                    <th class="px-4 py-2">读者ID</th>
                    <th class="px-4 py-2">状态</th>
                    <th class="px-4 py-2">预约时间</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-100">
                  <tr v-for="r in currentStatus.reservationQueue" :key="r.id">
                    <td class="px-4 py-2 text-gray-600">{{ r.id }}</td>
                    <td class="px-4 py-2 text-gray-600">{{ r.readerId }}</td>
                    <td class="px-4 py-2 text-gray-600">{{ r.status }}</td>
                    <td class="px-4 py-2 text-gray-600">{{ r.createdAt }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </section>
  `
};
