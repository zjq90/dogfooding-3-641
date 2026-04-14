<template>
  <div class="order-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input
            v-model="searchForm.orderNo"
            placeholder="请输入订单号"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="借阅人员">
          <el-select
            v-model="searchForm.borrowerId"
            placeholder="请选择借阅人员"
            clearable
            filterable
            style="width: 200px"
          >
            <el-option
              v-for="item in borrowerOptions"
              :key="item.id"
              :label="item.name + ' (' + item.phone + ')'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="借阅中" :value="0" />
            <el-option label="已归还" :value="1" />
            <el-option label="逾期" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="押金状态">
          <el-select
            v-model="searchForm.depositStatus"
            placeholder="请选择押金状态"
            clearable
            style="width: 150px"
          >
            <el-option label="未支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已退还" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">
            搜索
          </el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 借阅订单列表 -->
    <el-card class="table-card">
      <div slot="header" class="card-header">
        <span>借阅订单列表</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">
          新增订单
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" width="50" align="center" />
        <el-table-column prop="orderNo" label="订单号" min-width="150" />
        <el-table-column prop="borrowerName" label="借阅人员" min-width="100" />
        <el-table-column prop="borrowerPhone" label="手机号" min-width="120" />
        <el-table-column prop="bookTitle" label="图书名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="bookIsbn" label="ISBN" min-width="120" />
        <el-table-column prop="borrowDate" label="借阅日期" min-width="100" />
        <el-table-column prop="dueDate" label="应还日期" min-width="100" />
        <el-table-column prop="returnDate" label="归还日期" min-width="100" />
        <el-table-column prop="depositAmount" label="押金金额" min-width="100" align="right">
          <template slot-scope="scope">
            <span style="color: #F56C6C; font-weight: bold;">¥{{ scope.row.depositAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="depositStatus" label="押金状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getDepositStatusType(scope.row.depositStatus)" size="small">
              {{ getDepositStatusText(scope.row.depositStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="支付状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getPayStatusType(scope.row.payStatus)" size="small">
              {{ getPayStatusText(scope.row.payStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === 0"
              type="success"
              size="small"
              @click="handleReturn(scope.row)"
            >
              归还
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        :current-page="page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增订单对话框 -->
    <el-dialog
      title="新增借阅订单"
      :visible.sync="dialogVisible"
      width="600px"
    >
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="借阅人员" prop="borrowerId">
          <el-select
            v-model="form.borrowerId"
            placeholder="请选择借阅人员"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in borrowerOptions"
              :key="item.id"
              :label="item.name + ' (' + item.phone + ')'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择图书" prop="bookId">
          <el-select
            v-model="form.bookId"
            filterable
            remote
            reserve-keyword
            placeholder="请输入图书名称搜索"
            :remote-method="searchBooks"
            :loading="bookSearchLoading"
            style="width: 100%"
          >
            <el-option
              v-for="item in bookOptions"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            >
              <span>{{ item.title }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                可借: {{ item.availableQuantity }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="借阅天数" prop="borrowDays">
          <el-slider v-model="form.borrowDays" :max="60" show-stops :marks="{7:'7天', 30:'30天', 60:'60天'}"></el-slider>
        </el-form-item>
        <el-form-item label="押金金额" prop="depositAmount">
          <el-input-number v-model="form.depositAmount" :min="0" :precision="2" style="width: 200px" />
          <span style="margin-left: 10px; color: #909399;">元（可选）</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getOrderList, createOrder, returnOrder } from '@/api/order'
import { getAllBorrowers } from '@/api/borrower'
import { getBookList } from '@/api/book'

export default {
  name: 'BorrowOrder',
  data() {
    return {
      loading: false,
      tableData: [],
      page: 1,
      size: 10,
      total: 0,
      searchForm: {
        orderNo: '',
        borrowerId: null,
        status: null,
        depositStatus: null
      },
      dialogVisible: false,
      form: {
        borrowerId: null,
        bookId: null,
        borrowDays: 30,
        depositAmount: 0,
        remark: ''
      },
      rules: {
        borrowerId: [{ required: true, message: '请选择借阅人员', trigger: 'change' }],
        bookId: [{ required: true, message: '请选择图书', trigger: 'change' }]
      },
      submitLoading: false,
      borrowerOptions: [],
      bookOptions: [],
      bookSearchLoading: false
    }
  },
  created() {
    this.loadData()
    this.loadBorrowers()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getOrderList({
          page: this.page,
          size: this.size,
          orderNo: this.searchForm.orderNo,
          borrowerId: this.searchForm.borrowerId,
          status: this.searchForm.status,
          depositStatus: this.searchForm.depositStatus
        })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        console.error('加载订单失败:', error)
      } finally {
        this.loading = false
      }
    },

    async loadBorrowers() {
      try {
        const res = await getAllBorrowers()
        if (res.code === 200) {
          this.borrowerOptions = res.data
        }
      } catch (error) {
        console.error('加载借阅人员失败:', error)
      }
    },

    handleSearch() {
      this.page = 1
      this.loadData()
    },

    handleReset() {
      this.searchForm = {
        orderNo: '',
        borrowerId: null,
        status: null,
        depositStatus: null
      }
      this.handleSearch()
    },

    handleSizeChange(val) {
      this.size = val
      this.loadData()
    },

    handleCurrentChange(val) {
      this.page = val
      this.loadData()
    },

    handleAdd() {
      this.form = {
        borrowerId: null,
        bookId: null,
        borrowDays: 30,
        depositAmount: 0,
        remark: ''
      }
      this.bookOptions = []
      this.dialogVisible = true
    },

    async searchBooks(query) {
      if (query !== '') {
        this.bookSearchLoading = true
        try {
          const res = await getBookList({
            page: 1,
            size: 20,
            keyword: query
          })
          if (res.code === 200) {
            this.bookOptions = res.data.records.filter(book => book.availableQuantity > 0)
          }
        } catch (error) {
          console.error('搜索图书失败:', error)
        } finally {
          this.bookSearchLoading = false
        }
      }
    },

    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitLoading = true
          try {
            const dueDate = new Date()
            dueDate.setDate(dueDate.getDate() + this.form.borrowDays)
            
            const orderData = {
              borrowerId: this.form.borrowerId,
              bookId: this.form.bookId,
              dueDate: dueDate.toISOString().split('T')[0],
              depositAmount: this.form.depositAmount,
              remark: this.form.remark
            }
            
            const res = await createOrder(orderData)
            if (res.code === 200) {
              this.$message.success('创建成功')
              this.dialogVisible = false
              this.loadData()
            }
          } catch (error) {
            console.error('创建失败:', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
    },

    async handleReturn(row) {
      try {
        await this.$confirm(`确定要归还图书《${row.bookTitle}》吗？`, '提示', {
          type: 'warning'
        })

        const res = await returnOrder(row.id)
        if (res.code === 200) {
          this.$message.success('归还成功')
          this.loadData()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('归还失败:', error)
        }
      }
    },

    getStatusType(status) {
      const map = {
        0: 'primary',
        1: 'success',
        2: 'danger'
      }
      return map[status] || 'info'
    },

    getStatusText(status) {
      const map = {
        0: '借阅中',
        1: '已归还',
        2: '逾期'
      }
      return map[status] || '未知'
    },

    getDepositStatusType(status) {
      const map = {
        0: 'info',
        1: 'success',
        2: 'warning'
      }
      return map[status] || 'info'
    },

    getDepositStatusText(status) {
      const map = {
        0: '未支付',
        1: '已支付',
        2: '已退还'
      }
      return map[status] || '未知'
    },

    getPayStatusType(status) {
      const map = {
        0: 'info',
        1: 'success',
        2: 'warning'
      }
      return map[status] || 'info'
    },

    getPayStatusText(status) {
      const map = {
        0: '未支付',
        1: '已支付',
        2: '已退款'
      }
      return map[status] || '未知'
    }
  }
}
</script>

<style scoped>
.order-container {
  padding-bottom: 24px;
}
</style>
