// ============================================================
// shared.js — Gas Management System UI Prototype
// Shared utilities: Sidebar, Header, Modal, Toast, Navigation
// ============================================================

// ── ROLE CONFIG ──────────────────────────────────────────────
const ROLES = {
  admin: {
    label: 'Admin (Chủ đại lý)',
    icon: 'shield-check',
    color: 'blue',
    menu: [
      { label: 'Dashboard', icon: 'layout-dashboard', href: 'admin_dashboard.html' },
      { label: 'Sản phẩm & Giá', icon: 'package', href: 'admin_products.html' },
      { label: 'Khách hàng & Nợ', icon: 'users', href: 'admin_customers.html' },
      { label: 'Quản lý Tài xế', icon: 'truck', href: 'admin_drivers.html' },
      { label: 'Công nợ NSX', icon: 'factory', href: 'admin_suppliers.html' },
      { label: 'Cấu hình hệ thống', icon: 'settings', href: 'admin_settings.html' },
    ]
  },
  operator: {
    label: 'Nhân viên Tổng đài',
    icon: 'headphones',
    color: 'indigo',
    menu: [
      { label: 'Quản lý Đơn hàng', icon: 'clipboard-list', href: 'operator_orders.html' },
      { label: 'Đối soát Cuối ca', icon: 'calculator', href: 'operator_reconciliation.html' },
      { label: 'Bảo hành & Đổi trả', icon: 'wrench', href: 'operator_warranty.html' },
      { label: 'Tồn kho Vỏ bình', icon: 'archive', href: 'operator_inventory.html' },
    ]
  },
  driver: {
    label: 'Tài xế Giao hàng',
    icon: 'truck',
    color: 'emerald',
    menu: [
      { label: 'Chợ đơn', icon: 'shopping-bag', href: 'driver_jobboard.html' },
      { label: 'Đang giao hàng', icon: 'navigation', href: 'driver_delivery.html' },
      { label: 'Nộp tiền & Quyết toán', icon: 'wallet', href: 'driver_reconciliation.html' },
      { label: 'Bảo hành & Sửa chữa', icon: 'wrench', href: 'driver_warranty.html' },
    ]
  },
  customer: {
    label: 'Khách hàng',
    icon: 'store',
    color: 'amber',
    menu: [
      { label: 'Danh mục Sản phẩm', icon: 'shopping-bag', href: 'customer_catalog.html' },
      { label: 'Giỏ hàng', icon: 'shopping-cart', href: 'customer_cart.html' },
      { label: 'Đơn hàng của tôi', icon: 'package-check', href: 'customer_orders.html' },
      { label: 'Bảo hành', icon: 'shield', href: 'customer_warranty.html' },
    ]
  }
};

// ── GET CURRENT ROLE (Auto-inferred from URL or localStorage) ──
function getCurrentRole() {
  const currentFile = (window.location.pathname.split('/').pop() || '').toLowerCase();
  if (currentFile.startsWith('admin_')) return 'admin';
  if (currentFile.startsWith('operator_')) return 'operator';
  if (currentFile.startsWith('driver_')) return 'driver';
  if (currentFile.startsWith('customer_')) return 'customer';
  return localStorage.getItem('gas_role') || 'admin';
}

function setCurrentRole(role) {
  localStorage.setItem('gas_role', role);
}

function getCurrentUser() {
  const role = getCurrentRole();
  const names = {
    admin: 'Nguyễn Văn Hùng',
    operator: 'Trần Thị Mai',
    driver: 'Lê Minh Tuấn',
    customer: 'Phạm Hoàng Anh'
  };
  return names[role] || 'Người dùng';
}

// ── RENDER SIDEBAR ───────────────────────────────────────────
function renderSidebar(activeHref) {
  const role = getCurrentRole();
  const config = ROLES[role] || ROLES.admin;
  const currentFile = activeHref || window.location.pathname.split('/').pop() || 'index.html';

  const sidebar = document.getElementById('sidebar');
  if (!sidebar) return;

  sidebar.innerHTML = `
    <div class="flex flex-col h-full bg-slate-900 text-slate-100">
      <!-- Logo -->
      <div class="p-5 border-b border-slate-800">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-blue-600 flex items-center justify-center shadow-lg shadow-blue-600/30">
            <i data-lucide="flame" class="w-6 h-6 text-white"></i>
          </div>
          <div>
            <h1 class="text-white font-bold text-lg leading-tight">Gas Pro</h1>
            <p class="text-slate-400 text-xs">Quản lý Đại lý Gas</p>
          </div>
        </div>
      </div>

      <!-- Role Badge -->
      <div class="px-4 py-3 border-b border-slate-800">
        <div class="flex items-center gap-2 px-3 py-2 rounded-lg bg-slate-800/80 border border-slate-700">
          <i data-lucide="${config.icon}" class="w-4 h-4 text-blue-400"></i>
          <span class="text-slate-200 text-sm font-medium">${config.label}</span>
        </div>
      </div>

      <!-- Menu -->
      <nav class="flex-1 p-3 space-y-1 overflow-y-auto">
        ${config.menu.map(item => {
          const isActive = currentFile === item.href;
          return `
            <a href="${item.href}" class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all duration-200
              ${isActive 
                ? 'bg-blue-600 text-white font-semibold shadow-lg shadow-blue-600/30' 
                : 'text-slate-300 hover:bg-slate-800 hover:text-white'}">
              <i data-lucide="${item.icon}" class="w-5 h-5 flex-shrink-0"></i>
              <span class="truncate">${item.label}</span>
            </a>
          `;
        }).join('')}
      </nav>

      <!-- User + Logout -->
      <div class="p-4 border-t border-slate-800 bg-slate-900/50">
        <div class="flex items-center gap-3 mb-3">
          <div class="w-9 h-9 rounded-full bg-slate-700 flex items-center justify-center flex-shrink-0">
            <i data-lucide="user" class="w-5 h-5 text-slate-300"></i>
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-white text-sm font-medium truncate">${getCurrentUser()}</p>
            <p class="text-slate-400 text-xs truncate">${config.label}</p>
          </div>
        </div>
        <button onclick="logout()" class="w-full flex items-center justify-center gap-2 px-3 py-2 rounded-lg text-sm text-slate-400 hover:bg-red-500/10 hover:text-red-400 transition-colors">
          <i data-lucide="log-out" class="w-4 h-4"></i>
          <span>Đăng xuất</span>
        </button>
      </div>
    </div>
  `;
  try {
    if (window.lucide) lucide.createIcons();
  } catch (e) {
    console.warn('Lucide icon warning:', e);
  }
}

// ── RENDER HEADER ────────────────────────────────────────────
function renderHeader(title, breadcrumbs) {
  const header = document.getElementById('header');
  if (!header) return;

  const crumbs = breadcrumbs || [{ label: title }];
  header.innerHTML = `
    <div class="flex items-center justify-between">
      <div>
        <nav class="flex items-center gap-2 text-sm text-slate-500 mb-1">
          <a href="${getHomePage()}" class="hover:text-blue-600">Trang chủ</a>
          ${crumbs.map(c => `<span>/</span><span class="text-slate-700 font-medium">${c.label}</span>`).join('')}
        </nav>
        <h2 class="text-2xl font-bold text-slate-800">${title}</h2>
      </div>
      <div class="flex items-center gap-4">
        <button class="relative p-2 rounded-lg hover:bg-slate-100 transition-colors" onclick="showToast('Bạn có 3 thông báo mới', 'info')">
          <i data-lucide="bell" class="w-5 h-5 text-slate-600"></i>
          <span class="absolute -top-0.5 -right-0.5 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center font-bold">3</span>
        </button>
        <div class="flex items-center gap-2 pl-4 border-l border-slate-200">
          <div class="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center">
            <i data-lucide="user" class="w-4 h-4 text-blue-600"></i>
          </div>
          <span class="text-sm font-medium text-slate-700">${getCurrentUser()}</span>
        </div>
      </div>
    </div>
  `;
  try {
    if (window.lucide) lucide.createIcons();
  } catch (e) {
    console.warn('Lucide icon warning:', e);
  }
}

function getHomePage() {
  const role = getCurrentRole();
  const homeMap = { admin: 'admin_dashboard.html', operator: 'operator_orders.html', driver: 'driver_jobboard.html', customer: 'customer_catalog.html' };
  return homeMap[role] || 'index.html';
}

// ── MODAL ─────────────────────────────────────────────────────
function showModal(title, bodyHtml, footerHtml, sizeClass = 'max-w-2xl') {
  let overlay = document.getElementById('modal-overlay');
  if (!overlay) {
    overlay = document.createElement('div');
    overlay.id = 'modal-overlay';
    document.body.appendChild(overlay);
  }
  overlay.className = 'fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm transition-opacity overflow-y-auto';
  overlay.innerHTML = `
    <div class="bg-white rounded-2xl shadow-2xl w-full ${sizeClass} max-h-[85vh] flex flex-col overflow-hidden animate-scale-in my-auto">
      <div class="flex items-center justify-between px-6 py-3.5 border-b border-slate-200 bg-slate-50 flex-shrink-0">
        <h3 class="text-base font-bold text-slate-800">${title}</h3>
        <button onclick="closeModal()" class="p-1 rounded-lg hover:bg-slate-200 text-slate-400 hover:text-slate-600 transition-colors">
          <i data-lucide="x" class="w-5 h-5"></i>
        </button>
      </div>
      <div class="px-6 py-4 flex-1 overflow-y-auto space-y-4 text-sm">${bodyHtml}</div>
      ${footerHtml ? `<div class="px-6 py-3 bg-slate-50 border-t border-slate-200 flex justify-end gap-3 flex-shrink-0">${footerHtml}</div>` : ''}
    </div>
  `;
  overlay.onclick = (e) => { if (e.target === overlay) closeModal(); };
  try {
    if (window.lucide) lucide.createIcons();
  } catch (e) {
    console.warn('Lucide icon warning:', e);
  }
}

function closeModal() {
  const overlay = document.getElementById('modal-overlay');
  if (overlay) overlay.remove();
}

// ── TOAST ─────────────────────────────────────────────────────
function showToast(message, type = 'success') {
  const colors = {
    success: 'bg-green-600', error: 'bg-red-600', warning: 'bg-orange-600', info: 'bg-blue-600'
  };
  const icons = {
    success: 'check-circle', error: 'x-circle', warning: 'alert-triangle', info: 'info'
  };
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'fixed top-4 right-4 z-[60] flex flex-col gap-2';
    document.body.appendChild(container);
  }
  const toast = document.createElement('div');
  toast.className = `${colors[type]} text-white px-5 py-3 rounded-xl shadow-lg flex items-center gap-3 text-sm font-medium animate-slide-in min-w-[300px]`;
  toast.innerHTML = `<i data-lucide="${icons[type]}" class="w-5 h-5 flex-shrink-0"></i><span>${message}</span>`;
  container.appendChild(toast);
  try {
    if (window.lucide) lucide.createIcons();
  } catch (e) {
    console.warn('Lucide icon warning:', e);
  }
  setTimeout(() => { toast.style.opacity = '0'; toast.style.transform = 'translateX(100%)'; setTimeout(() => toast.remove(), 300); }, 3500);
}

// ── CONFIRM DIALOG ────────────────────────────────────────────
function showConfirm(title, message, onConfirm) {
  showModal(title,
    `<p class="text-slate-600">${message}</p>`,
    `<button onclick="closeModal()" class="px-4 py-2 rounded-lg border border-slate-300 text-slate-600 hover:bg-slate-100 text-sm font-medium">Hủy</button>
     <button onclick="closeModal(); (${onConfirm})()" class="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 text-sm font-medium">Xác nhận</button>`
  );
}

// ── LOGOUT ────────────────────────────────────────────────────
function logout() {
  localStorage.removeItem('gas_role');
  window.location.href = 'index.html';
}

// ── STATUS BADGE ──────────────────────────────────────────────
function statusBadge(status) {
  const map = {
    'Chờ nhận đơn':   'bg-yellow-100 text-yellow-700',
    'Đang giao':      'bg-blue-100 text-blue-700',
    'Đã hoàn thành':  'bg-green-100 text-green-700',
    'Đã hủy':         'bg-red-100 text-red-700',
    'Đang sửa chữa':  'bg-purple-100 text-purple-700',
    'Chờ linh kiện':  'bg-orange-100 text-orange-700',
    'Chờ thanh toán': 'bg-amber-100 text-amber-700',
    'Hoạt động':      'bg-green-100 text-green-700',
    'Ngoại tuyến':    'bg-slate-100 text-slate-500',
    'Bị khóa':        'bg-red-100 text-red-700',
    'Rảnh':           'bg-green-100 text-green-700',
    'Đang cho mượn':  'bg-orange-100 text-orange-700',
  };
  const cls = map[status] || 'bg-slate-100 text-slate-600';
  return `<span class="inline-flex px-2.5 py-1 rounded-full text-xs font-semibold ${cls}">${status}</span>`;
}

// ── FORMAT CURRENCY ───────────────────────────────────────────
function fmtMoney(n) {
  return new Intl.NumberFormat('vi-VN').format(n) + 'đ';
}

// ── INIT PAGE (Executes immediately if DOM ready, or on DOMContentLoaded) ──
function initPage(title, breadcrumbs) {
  const doInit = () => {
    renderSidebar();
    renderHeader(title, breadcrumbs);
    try {
      if (window.lucide) lucide.createIcons();
    } catch (e) {
      console.warn('Lucide icon warning:', e);
    }
  };
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', doInit);
  } else {
    doInit();
  }
}

// ── CUSTOM CSS ANIMATIONS (injected) ──────────────────────────
(function injectAnimations() {
  const style = document.createElement('style');
  style.textContent = `
    @keyframes scale-in { from { opacity:0; transform:scale(0.95) } to { opacity:1; transform:scale(1) } }
    @keyframes slide-in { from { opacity:0; transform:translateX(100%) } to { opacity:1; transform:translateX(0) } }
    .animate-scale-in { animation: scale-in 0.2s ease-out; }
    .animate-slide-in { animation: slide-in 0.3s ease-out; }
  `;
  document.head.appendChild(style);
})();

