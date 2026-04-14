<template>
  <div class="borrower-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
        <span>借阅人员列表</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增借阅人员</el-button>
      </div>
      
      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column type="index" width="50" align="center" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template slot-scope="scope">
            {{ scope.row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
        <el-table-column prop="deposit" label="押金余额" width="100" align="right">
          <template slot-scope="scope">
            ¥{{ scope.row.deposit ? scope.row.deposit.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleDeposit(scope.row)">押金</el-button>
            <el-button 
              :type="scope.row.status === 1 ? 'warning' : 'success'" 
              size="small"
              @click="handleStatusChange(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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
    
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" type="textarea" :rows="2" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="押金" prop="deposit">
          <el-input-number v-model="form.deposit" :min="0" :precision="2" :step="10" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
    
    <el-dialog title="押金管理" :visible.sync="depositDialogVisible" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="借阅人">{{ currentBorrower.name }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentBorrower.phone }}</el-descriptions-item>
        <el-descriptions-item label="当前押金余额">
          ¥{{ currentBorrower.deposit ? currentBorrower.deposit.toFixed(2) : '0.00' }}
        </el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <el-form ref="depositForm" :model="depositForm" :rules="depositRules" label-width="100px">
        <el-form-item label="操作类型" prop="type">
          <el-radio-group v-model="depositForm.type">
            <el-radio :label="1">缴纳押金</el-radio>
            <el-radio :label="2">退还押金</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="depositForm.amount" :min="0" :precision="2" :step="10" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="depositForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="depositDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="depositSubmitLoading" @click="handleDepositSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getBorrowerList, addBorrower, updateBorrower, deleteBorrower, updateBorrowerStatus } from '@/api/borrower'
import { addDepositRecord } from '@/api/deposit'

export default {
  name: 'BorrowerManagement',
  data() {
    return {
      loading: false,
      tableData: [],
      page: 1,
      size: 10,
      total: 0,
      searchForm: {
        name: '',
        phone: '',
        status: null
      },
      dialogVisible: false,
      dialogTitle: '新增借阅人员',
      form: {
        id: null,
        name: '',
        phone: '',
        idCard: '',
        gender: 1,
        address: '',
        deposit: 0,
        status: 1,
        remark: ''
      },
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
      },
      submitLoading: false,
      depositDialogVisible: false,
      currentBorrower: {},
      depositForm: {
        type: 1,
        amount: 0,
        remark: ''
      },
      depositRules: {
        type: [{ required: true, message: '请选择操作类型', trigger: 'change' }],
        amount: [{ required: true, message: '请输入金额', trigger: 'blur' }]
      },
      depositSubmitLoading: false
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getBorrowerList({
          page: this.page,
          size: this.size,
          ...this.searchForm
        })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        console.error('加载借阅人员列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    handleSearch() {
      this.page = 1
      this.loadData()
    },
    
    handleReset() {
      this.searchForm = {
        name: '',
        phone: '',
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
      this.dialogTitle = '新增借阅人员'
      this.form = {
        id: null,
        name: '',
        phone: '',
        idCard: '',
        gender: 1,
        address: '',
        deposit: 0,
        status: 1,
        remark: ''
      }
      this.dialogVisible = true
    },
    
    handleEdit(row) {
      this.dialogTitle = '编辑借阅人员'
      this.form = { ...row }
      this.dialogVisible = true
    },
    
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitLoading = true
          try {
            let res
            if (this.form.id) {
              res = await updateBorrower(this.form.id, this.form)
            } else {
              res = await addBorrower(this.form)
            }
            if (res.code === 200) {
              this.$message.success(this.form.id ? '更新成功' : '添加成功')
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
    },
    
    async handleDelete(row) {
      try {
        await this.$confirm(`确定要删除借阅人员"${row.name}"吗？`, '提示', { type: 'warning' })
        const res = await deleteBorrower(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
        }
      }
    },
    
    async handleStatusChange(row) {
      const newStatus = row.status === 1 ? 0 : 1
      const statusText = newStatus === 1 ? '启用' : '禁用'
      try {
        await this.$confirm(`确定要${statusText}借阅人员"${row.name}"吗？`, '提示', { type: 'warning' })
        const res = await updateBorrowerStatus(row.id, newStatus)
        if (res.code === 200) {
          this.$message.success(`${statusText}成功`)
          this.loadData()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('操作失败:', error)
        }
      }
    },
    
    handleDeposit(row) {
      this.currentBorrower = { ...row }
      this.depositForm = {
        type: 1,
        amount: 0,
        remark: ''
      }
      this.depositDialogVisible = true
    },
    
    handleDepositSubmit() {
      this.$refs.depositForm.validate(async valid => {
        if (valid) {
          this.depositSubmitLoading = true
          try {
            const res = await addDepositRecord(
              this.currentBorrower.id,
              this.depositForm.amount,
              this.depositForm.type,
              this.depositForm.remark
            )
            if (res.code === 200) {
              this.$message.success('操作成功')
              this.depositDialogVisible = false
              this.loadData()
            }
          } catch (error) {
            console.error('操作失败:', error)
          } finally {
            this.depositSubmitLoading = false
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.borrower-container {
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
