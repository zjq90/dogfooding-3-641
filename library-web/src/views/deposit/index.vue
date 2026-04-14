<template>
  <div class="deposit-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
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
            style="width: 200px"
          >
            <el-option
              v-for="item in borrowerOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable style="width: 120px">
            <el-option label="缴纳" :value="1" />
            <el-option label="退还" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="已完成" :value="1" />
            <el-option label="已取消" :value="0" />
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
        <span>押金明细列表</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增押金记录</el-button>
      </div>
      
      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column type="index" width="50" align="center" />
        <el-table-column prop="borrowerName" label="借阅人" width="100" />
        <el-table-column prop="amount" label="金额" width="120" align="right">
          <template slot-scope="scope">
            <span :style="{ color: scope.row.type === 1 ? '#67C23A' : '#F56C6C' }">
              {{ scope.row.type === 1 ? '+' : '-' }}¥{{ scope.row.amount ? scope.row.amount.toFixed(2) : '0.00' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 1 ? 'success' : 'warning'" size="small">
              {{ scope.row.type === 1 ? '缴纳' : '退还' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.status === 1 ? '已完成' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
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
    
    <el-dialog title="新增押金记录" :visible.sync="dialogVisible" width="500px">
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
        <el-form-item label="操作类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">缴纳押金</el-radio>
            <el-radio :label="2">退还押金</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getDepositList, addDepositRecord } from '@/api/deposit'
import { getBorrowerList } from '@/api/borrower'

export default {
  name: 'DepositManagement',
  data() {
    return {
      loading: false,
      tableData: [],
      page: 1,
      size: 10,
      total: 0,
      searchForm: {
        borrowerId: null,
        type: null,
        status: null
      },
      dialogVisible: false,
      form: {
        borrowerId: null,
        type: 1,
        amount: 0,
        remark: ''
      },
      rules: {
        borrowerId: [{ required: true, message: '请选择借阅人', trigger: 'change' }],
        type: [{ required: true, message: '请选择操作类型', trigger: 'change' }],
        amount: [{ required: true, message: '请输入金额', trigger: 'blur' }]
      },
      submitLoading: false,
      borrowerSearchLoading: false,
      borrowerOptions: []
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
        const res = await getDepositList({
          page: this.page,
          size: this.size,
          ...this.searchForm
        })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        console.error('加载押金明细列表失败:', error)
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
    
    handleSearch() {
      this.page = 1
      this.loadData()
    },
    
    handleReset() {
      this.searchForm = {
        borrowerId: null,
        type: null,
        status: null
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
        type: 1,
        amount: 0,
        remark: ''
      }
      this.dialogVisible = true
    },
    
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitLoading = true
          try {
            const res = await addDepositRecord(
              this.form.borrowerId,
              this.form.amount,
              this.form.type,
              this.form.remark
            )
            if (res.code === 200) {
              this.$message.success('添加成功')
              this.dialogVisible = false
              this.loadData()
            }
          } catch (error) {
            console.error('操作失败:', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.deposit-container {
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
