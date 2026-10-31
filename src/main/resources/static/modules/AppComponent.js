// ============================================
// 根组件：顶部导航（标签页切换）+ 全局提示
// 挂载 学生 / 课程 / 成绩 / 图书借阅 四个子组件
// ============================================

import StudentList from './StudentList.js';
import CourseList from './CourseList.js';
import GradeList from './GradeList.js';
import BookList from './BookList.js';
import LoanList from './LoanList.js';
import ReservationList from './ReservationList.js';

const { ref, reactive } = Vue;

export default {
  name: 'AppComponent',
  components: { StudentList, CourseList, GradeList, BookList, LoanList, ReservationList },
  setup() {
    // 当前激活的顶部标签页
    const activeTab = ref('students');

    // 图书借阅内部的次级标签页（图书 / 借还 / 预约与逾期）
    const lendingTab = ref('books');

    // 全局提示信息
    const message = reactive({ type: '', text: '', show: false });
    function notify(text, type) {
      type = type || 'success';
      message.type = type;
      message.text = text;
      message.show = true;
      setTimeout(function () { message.show = false; }, 3000);
    }

    return {
      activeTab,
      lendingTab,
      message,
      notify
    };
  },
  template: `
    <div class="min-h-screen bg-gray-100">
      <!-- 顶部导航 -->
      <header class="bg-white shadow">
        <div class="max-w-6xl mx-auto px-4 py-4 flex items-center justify-between">
          <h1 class="text-xl font-bold text-gray-800">图书馆管理系统</h1>
          <nav class="flex space-x-2">
            <button @click="activeTab='students'" :class="activeTab==='students' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'" class="px-4 py-2 rounded-lg font-medium">学生管理</button>
            <button @click="activeTab='courses'" :class="activeTab==='courses' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'" class="px-4 py-2 rounded-lg font-medium">课程管理</button>
            <button @click="activeTab='grades'" :class="activeTab==='grades' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'" class="px-4 py-2 rounded-lg font-medium">成绩管理</button>
            <button @click="activeTab='lending'" :class="activeTab==='lending' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'" class="px-4 py-2 rounded-lg font-medium">图书借阅</button>
          </nav>
        </div>
      </header>

      <!-- 全局提示 -->
      <div v-if="message.show" class="fixed top-4 left-1/2 transform -translate-x-1/2 z-50 px-4 py-2 rounded-lg shadow text-white" :class="message.type==='error' ? 'bg-red-500' : 'bg-green-500'">
        {{ message.text }}
      </div>

      <main class="max-w-6xl mx-auto px-4 py-6">
        <StudentList v-if="activeTab==='students'" :notify="notify" />
        <CourseList v-if="activeTab==='courses'" :notify="notify" />
        <GradeList v-if="activeTab==='grades'" :notify="notify" />

        <!-- 图书借阅：次级标签切换三个区块 -->
        <div v-if="activeTab==='lending'">
          <div class="flex space-x-2 mb-4">
            <button @click="lendingTab='books'" :class="lendingTab==='books' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'" class="px-4 py-2 rounded-lg font-medium">图书</button>
            <button @click="lendingTab='loans'" :class="lendingTab==='loans' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'" class="px-4 py-2 rounded-lg font-medium">借还</button>
            <button @click="lendingTab='reservations'" :class="lendingTab==='reservations' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'" class="px-4 py-2 rounded-lg font-medium">预约与逾期</button>
          </div>
          <BookList v-if="lendingTab==='books'" :notify="notify" />
          <LoanList v-if="lendingTab==='loans'" :notify="notify" />
          <ReservationList v-if="lendingTab==='reservations'" :notify="notify" />
        </div>
      </main>
    </div>
  `
};
