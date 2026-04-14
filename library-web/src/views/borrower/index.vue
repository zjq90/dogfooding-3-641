<template>
  <div class="borrower-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="姓名">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入姓名"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入手机号"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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

    <!-- 借阅人员列表 -->
    <el-card class="table-card">
      <div slot="header" class="card-header">
        <span>借阅人员列表</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">
          新增借阅人员
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" width="50" align="center" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
        <el-table-column prop="idCard" label="身份证号" min-width="150" />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="depositAmount" label="押金余额" min-width="100" align="right">
          <template slot-scope="scope">
            <span style="color: #F56C6C; font-weight: bold;">¥{{ scope.row.depositAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="text" size="small" @click="handleDeposit(scope.row)">
              缴纳押金
            </el-button>
            <el-button type="text" size="small" @click="handleRefund(scope.row)">
              退还押金
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">
              删除
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
    >
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" type="textarea" :rows="2" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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

    <!-- 缴纳押金对话框 -->
    <el-dialog
      title="缴纳押金"
      :visible.sync="depositDialogVisible"
      width="500px"
    >
      <el-form
        ref="depositForm"
        :model="depositForm"
        :rules="depositRules"
        label-width="100px"
      >
        <el-form-item label="借阅人员">
          <span>{{ currentBorrower.name }}</span>
        </el-form-item>
        <el-form-item label="当前余额">
          <span style="color: #F56C6C; font-weight: bold;">¥{{ currentBorrower.depositAmount }}</span>
        </el-form-item>
        <el-form-item label="缴纳金额" prop="amount">
          <el-input-number v-model="depositForm.amount" :min="0.01" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="depositForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="depositDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleDepositSubmit">
          确定
        </el-button>
      </div>
    </el-dialog>

    <!-- 退还押金对话框 -->
    <el-dialog
      title="退还押金"
      :visible.sync="refundDialogVisible"
      width="500px"
    >
      <el-form
        ref="refundForm"
        :model="refundForm"
        :rules="refundRules"
        label-width="100px"
      >
        <el-form-item label="借阅人员">
          <span>{{ currentBorrower.name }}</span>
        </el-form-item>
        <el-form-item label="当前余额">
          <span style="color: #F56C6C; font-weight: bold;">¥{{ currentBorrower.depositAmount }}</span>
        </el-form-item>
        <el-form-item label="退还金额" prop="amount">
          <el-input-number 
            v-model="refundForm.amount" 
            :min="0.01" 
            :max="currentBorrower.depositAmount"
            :precision="2" 
            style="width: 200px" 
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="refundForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleRefundSubmit">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getBorrowerList, addBorrower, updateBorrower, deleteBorrower, addDeposit, refundDeposit } from '@/api/borrower'

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
      dialogTitle: '',
      form: {
        id: null,
        name: '',
        phone: '',
        email: '',
        idCard: '',
        address: '',
        status: 1,
        remark: ''
      },
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
      },
      submitLoading: false,
      depositDialogVisible: false,
      depositForm: {
        amount: 100,
        remark: ''
      },
      depositRules: {
        amount: [{ required: true, message: '请输入缴纳金额', trigger: 'blur' }]
      },
      refundDialogVisible: false,
      refundForm: {
        amount: 0,
        remark: ''
      },
      refundRules: {
        amount: [{ required: true, message: '请输入退还金额', trigger: 'blur' }]
      },
      currentBorrower: {}
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
          name: this.searchForm.name,
          phone: this.searchForm.phone,
          status: this.searchForm.status
        })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        console.error('加载借阅人员失败:', error)
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
        email: '',
        idCard: '',
        address: '',
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
              this.$message.success(this.form.id ? '更新成功' : '新增成功')
              this.dialogVisible = false
              this.loadData()
            }
          } catch (error) {
            console.error('提交失败:', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
    },

    async handleDelete(row) {
      try {
        await this.$confirm(`确定要删除借阅人员"${row.name}"吗？`, '提示', {
          type: 'warning'
        })

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

    handleDeposit(row) {
      this.currentBorrower = row
      this.depositForm = {
        amount: 100,
        remark: ''
      }
      this.depositDialogVisible = true
    },

    handleDepositSubmit() {
      this.$refs.depositForm.validate(async valid => {
        if (valid) {
          this.submitLoading = true
          try {
            const res = await addDeposit(
              this.currentBorrower.id,
              this.depositForm.amount,
              this.depositForm.remark
            )
            if (res.code === 200) {
              this.$message.success('缴纳成功')
              this.depositDialogVisible = false
              this.loadData()
            }
          } catch (error) {
            console.error('缴纳失败:', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
    },

    handleRefund(row) {
      if (row.depositAmount <= 0) {
        this.$message.warning('该用户没有押金余额')
        return
      }
      this.currentBorrower = row
      this.refundForm = {
        amount: row.depositAmount,
        remark: ''
      }
      this.refundDialogVisible = true
    },

    handleRefundSubmit() {
      this.$refs.refundForm.validate(async valid => {
        if (valid) {
          this.submitLoading = true
          try {
            const res = await refundDeposit(
              this.currentBorrower.id,
              this.refundForm.amount,
              this.refundForm.remark
            )
            if (res.code === 200) {
              this.$message.success('退还成功')
              this.refundDialogVisible = false
              this.loadData()
            }
          } catch (error) {
            console.error('退还失败:', error)
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
.borrower-container {
  padding-bottom: 24px;
}
</style>
