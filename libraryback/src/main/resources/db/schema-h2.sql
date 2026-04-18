-- 图书后台管理系统数据库脚本 (H2版本)

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    avatar VARCHAR(255),
    role TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 图书分类表
CREATE TABLE IF NOT EXISTS book_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255),
    sort_order INT DEFAULT 0,
    parent_id BIGINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 图书信息表
CREATE TABLE IF NOT EXISTS book_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(50),
    publisher VARCHAR(100),
    publish_date DATE,
    category_id BIGINT,
    description TEXT,
    cover_image VARCHAR(255),
    price DECIMAL(10,2),
    total_quantity INT DEFAULT 0,
    available_quantity INT DEFAULT 0,
    location VARCHAR(50),
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 借阅记录表
CREATE TABLE IF NOT EXISTS borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status TINYINT DEFAULT 0,
    remark VARCHAR(255),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 插入图书分类数据 (如果不存在则插入)
MERGE INTO book_category (name, code, description, sort_order, parent_id) KEY(code) VALUES
('文学', 'LITERATURE', '文学类图书', 1, 0);
MERGE INTO book_category (name, code, description, sort_order, parent_id) KEY(code) VALUES
('小说', 'NOVEL', '小说类图书', 2, 0);
MERGE INTO book_category (name, code, description, sort_order, parent_id) KEY(code) VALUES
('科技', 'TECHNOLOGY', '科技类图书', 3, 0);
MERGE INTO book_category (name, code, description, sort_order, parent_id) KEY(code) VALUES
('历史', 'HISTORY', '历史类图书', 4, 0);
MERGE INTO book_category (name, code, description, sort_order, parent_id) KEY(code) VALUES
('艺术', 'ART', '艺术类图书', 5, 0);
MERGE INTO book_category (name, code, description, sort_order, parent_id) KEY(code) VALUES
('教育', 'EDUCATION', '教育类图书', 6, 0);
MERGE INTO book_category (name, code, description, sort_order, parent_id) KEY(code) VALUES
('经济', 'ECONOMICS', '经济类图书', 7, 0);
MERGE INTO book_category (name, code, description, sort_order, parent_id) KEY(code) VALUES
('医学', 'MEDICINE', '医学类图书', 8, 0);

-- 部门表
CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(30) NOT NULL UNIQUE,
    parent_id BIGINT DEFAULT 0,
    description VARCHAR(255),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 内部人员表
CREATE TABLE IF NOT EXISTS sys_employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    employee_no VARCHAR(30) NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    position VARCHAR(50),
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    type TINYINT NOT NULL,
    parent_id BIGINT DEFAULT 0,
    path VARCHAR(255),
    component VARCHAR(255),
    icon VARCHAR(50),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 部门权限关联表
CREATE TABLE IF NOT EXISTS sys_department_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入部门测试数据
MERGE INTO sys_department (name, code, parent_id, description, sort_order, status) KEY(code) VALUES
('总公司', 'B-001', 0, '总公司', 1, 1);
MERGE INTO sys_department (name, code, parent_id, description, sort_order, status) KEY(code) VALUES
('技术部', 'B-002', 1, '技术研发部门', 1, 1);
MERGE INTO sys_department (name, code, parent_id, description, sort_order, status) KEY(code) VALUES
('市场部', 'B-003', 1, '市场营销部门', 2, 1);
MERGE INTO sys_department (name, code, parent_id, description, sort_order, status) KEY(code) VALUES
('人事部', 'B-004', 1, '人力资源部门', 3, 1);
MERGE INTO sys_department (name, code, parent_id, description, sort_order, status) KEY(code) VALUES
('财务部', 'B-005', 1, '财务管理部门', 4, 1);
MERGE INTO sys_department (name, code, parent_id, description, sort_order, status) KEY(code) VALUES
('前端开发组', 'B-006', 2, '前端开发小组', 1, 1);
MERGE INTO sys_department (name, code, parent_id, description, sort_order, status) KEY(code) VALUES
('后端开发组', 'B-007', 2, '后端开发小组', 2, 1);

-- 插入权限测试数据
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('数据概览', 'dashboard', 1, 0, '/dashboard', 'dashboard/index', 'el-icon-s-data', 1, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('图书管理', 'books', 1, 0, '/books', 'book/index', 'el-icon-reading', 2, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('分类管理', 'categories', 1, 0, '/categories', 'category/index', 'el-icon-folder-opened', 3, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('借阅管理', 'borrow', 1, 0, '/borrow', 'borrow/index', 'el-icon-document', 4, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('用户管理', 'users', 1, 0, '/users', 'user/index', 'el-icon-user', 5, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('部门管理', 'departments', 1, 0, '/departments', 'department/index', 'el-icon-office-building', 6, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('人员管理', 'employees', 1, 0, '/employees', 'employee/index', 'el-icon-s-custom', 7, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('新增图书', 'books:add', 2, 2, NULL, NULL, NULL, 1, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('编辑图书', 'books:edit', 2, 2, NULL, NULL, NULL, 2, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('删除图书', 'books:delete', 2, 2, NULL, NULL, NULL, 3, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('新增用户', 'users:add', 2, 5, NULL, NULL, NULL, 1, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('编辑用户', 'users:edit', 2, 5, NULL, NULL, NULL, 2, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('删除用户', 'users:delete', 2, 5, NULL, NULL, NULL, 3, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('查看手机号', 'info:phone', 3, 0, NULL, NULL, NULL, 1, 1);
MERGE INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) KEY(code) VALUES
('查看邮箱', 'info:email', 3, 0, NULL, NULL, NULL, 2, 1);
