<template>
  <div class="borrow-order-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="借阅人">
          <el-select
            v-model="searchForm.borrowerId"
            filterable
            remote
            reserve-keyword
            placeholder="请选择借阅人"
            :remote-method="searchBorrowers"
            :loading="borrowerSearchLoading"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in borrowerOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="借阅中" :value="0" />
            <el-option label="已归还" :value="1" />
            <el-option label="逾期" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="押金状态">
          <el-select v-model="searchForm.depositStatus" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="未退还" :value="0" />
            <el-option label="已退还" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select v-model="searchForm.paymentStatus" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="未支付" :value="0" />
            <el-option label="已支付" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="table-card">
      <div slot="header" class="card-header">
        <span>借阅订单列表</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增借阅订单</el-button>
      </div>
      
      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column type="index" width="50" align="center" />
        <el-table-column prop="orderNo" label="订单号" width="160" />
        <el-table-column prop="borrowerName" label="借阅人" width="100" />
        <el-table-column prop="borrowerPhone" label="手机号" width="130" />
        <el-table-column prop="bookTitle" label="图书名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="borrowDate" label="借阅日期" width="110" />
        <el-table-column prop="dueDate" label="应还日期" width="110" />
        <el-table-column prop="returnDate" label="归还日期" width="110">
          <template slot-scope="scope">
            {{ scope.row.returnDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="depositAmount" label="押金" width="100" align="right">
          <template slot-scope="scope">
            ¥{{ scope.row.depositAmount ? scope.row.depositAmount.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="depositStatus" label="押金状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.depositStatus === 1 ? 'success' : 'warning'" size="small">
              {{ scope.row.depositStatus === 1 ? '已退还' : '未退还' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatus" label="支付状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.paymentStatus === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.paymentStatus === 1 ? '已支付' : '未支付' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getOrderStatusType(scope.row.status)" size="small">
              {{ getOrderStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button 
              v-if="scope.row.status === 0"
              type="success" 
              size="small"
              @click="handleReturn(scope.row)"
            >
              归还
            </el-button>
            <el-button 
              v-if="scope.row.depositStatus === 0 && scope.row.depositAmount > 0"
              type="warning" 
              size="small"
              @click="handleRefundDeposit(scope.row)"
            >
              退押金
            </el-button>
            <el-button 
              v-if="scope.row.paymentStatus === 0"
              type="primary" 
              size="small"
              @click="handlePay(scope.row)"
            >
              确认支付
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
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
    
    <el-dialog title="新增借阅订单" :visible.sync="dialogVisible" width="600px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="借阅人" prop="borrowerId">
          <el-select
            v-model="form.borrowerId"
            filterable
            remote
            reserve-keyword
            placeholder="请选择借阅人"
            :remote-method="searchBorrowers"
            :loading="borrowerSearchLoading"
            style="width: 100%"
          >
            <el-option
              v-for="item in borrowerOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span>{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                押金: ¥{{ item.deposit ? item.deposit.toFixed(2) : '0.00' }}
              </span>
            </el-option>
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
          <el-input-number v-model="form.depositAmount" :min="0" :precision="2" :step="10" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定借阅</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getBorrowOrderList, createBorrowOrder, returnBook, refundDeposit, updatePaymentStatus } from '@/api/borrowOrder'
import { getBorrowerList } from '@/api/borrower'
import { getBookList } from '@/api/book'

export default {
  name: 'BorrowOrderManagement',
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
        depositStatus: null,
        paymentStatus: null
      },
      dialogVisible: false,
      form: {
        borrowerId: null,
        bookId: null,
        borrowDays: 30,
        depositAmount: 0
      },
      rules: {
        borrowerId: [{ required: true, message: '请选择借阅人', trigger: 'change' }],
        bookId: [{ required: true, message: '请选择图书', trigger: 'change' }]
      },
      submitLoading: false,
      borrowerSearchLoading: false,
      borrowerOptions: [],
      bookSearchLoading: false,
      bookOptions: []
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
        const res = await getBorrowOrderList({
          page: this.page,
          size: this.size,
          ...this.searchForm
        })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        console.error('加载借阅订单列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    async loadBorrowers() {
      try {
        const res = await getBorrowerList({ page: 1, size: 100 })
        if (res.code === 200) {
          this.borrowerOptions = res.data.records
        }
      } catch (error) {
        console.error('加载借阅人列表失败:', error)
      }
    },
    
    async searchBorrowers(query) {
      if (query !== '') {
        this.borrowerSearchLoading = true
        try {
          const res = await getBorrowerList({ page: 1, size: 20, name: query })
          if (res.code === 200) {
            this.borrowerOptions = res.data.records
          }
        } catch (error) {
          console.error('搜索借阅人失败:', error)
        } finally {
          this.borrowerSearchLoading = false
        }
      }
    },
    
    async searchBooks(query) {
      if (query !== '') {
        this.bookSearchLoading = true
        try {
          const res = await getBookList({ page: 1, size: 20, keyword: query })
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
    
    handleSearch() {
      this.page = 1
      this.loadData()
    },
    
    handleReset() {
      this.searchForm = {
        orderNo: '',
        borrowerId: null,
        status: null,
        depositStatus: null,
        paymentStatus: null
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
        depositAmount: 0
      }
      this.bookOptions = []
      this.dialogVisible = true
    },
    
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitLoading = true
          try {
            const res = await createBorrowOrder(
              this.form.borrowerId,
              this.form.bookId,
              this.form.borrowDays,
              this.form.depositAmount
            )
            if (res.code === 200) {
              this.$message.success('借阅成功')
              this.dialogVisible = false
              this.loadData()
            }
          } catch (error) {
            console.error('借阅失败:', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    
    async handleReturn(row) {
      try {
        await this.$confirm(`确定要归还图书《${row.bookTitle}》吗？`, '提示', { type: 'warning' })
        const res = await returnBook(row.id)
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
    
    async handleRefundDeposit(row) {
      try {
        await this.$confirm(`确定要退还押金¥${row.depositAmount.toFixed(2)}吗？`, '提示', { type: 'warning' })
        const res = await refundDeposit(row.id)
        if (res.code === 200) {
          this.$message.success('押金退还成功')
          this.loadData()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('退还押金失败:', error)
        }
      }
    },
    
    async handlePay(row) {
      try {
        await this.$confirm('确定已收到支付款项吗？', '确认支付', { type: 'info' })
        const res = await updatePaymentStatus(row.id, 1)
        if (res.code === 200) {
          this.$message.success('支付确认成功')
          this.loadData()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('确认支付失败:', error)
        }
      }
    },
    
    getOrderStatusType(status) {
      const map = {
        0: 'primary',
        1: 'success',
        2: 'danger',
        3: 'info'
      }
      return map[status] || 'info'
    },
    
    getOrderStatusText(status) {
      const map = {
        0: '借阅中',
        1: '已归还',
        2: '逾期',
        3: '已取消'
      }
      return map[status] || '未知'
    }
  }
}
</script>

<style scoped>
.borrow-order-container {
  padding-bottom: 24px;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.table-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 16px;
  font-weight: 500;
}
</style>
