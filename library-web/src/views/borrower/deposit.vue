<template>
  <div class="deposit-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
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
        <el-form-item label="类型">
          <el-select
            v-model="searchForm.type"
            placeholder="请选择类型"
            clearable
            style="width: 150px"
          >
            <el-option label="缴纳押金" :value="1" />
            <el-option label="退还押金" :value="2" />
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

    <!-- 押金明细列表 -->
    <el-card class="table-card">
      <div slot="header" class="card-header">
        <span>押金明细</span>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" width="50" align="center" />
        <el-table-column prop="borrowerName" label="借阅人员" min-width="100" />
        <el-table-column prop="borrowerPhone" label="手机号" min-width="120" />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 1 ? 'success' : 'warning'" size="small">
              {{ scope.row.type === 1 ? '缴纳' : '退还' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" min-width="100" align="right">
          <template slot-scope="scope">
            <span :style="{ color: scope.row.amount > 0 ? '#67C23A' : '#F56C6C', fontWeight: 'bold' }">
              {{ scope.row.amount > 0 ? '+' : '' }}¥{{ Math.abs(scope.row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="操作后余额" min-width="100" align="right">
          <template slot-scope="scope">
            <span style="color: #409EFF; font-weight: bold;">¥{{ scope.row.balance }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="订单号" min-width="150" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="操作时间" min-width="160" />
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
  </div>
</template>

<script>
import { getDepositList } from '@/api/deposit'
import { getAllBorrowers } from '@/api/borrower'

export default {
  name: 'DepositRecord',
  data() {
    return {
      loading: false,
      tableData: [],
      page: 1,
      size: 10,
      total: 0,
      searchForm: {
        borrowerId: null,
        type: null
      },
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
          borrowerId: this.searchForm.borrowerId,
          type: this.searchForm.type
        })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        console.error('加载押金明细失败:', error)
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
        borrowerId: null,
        type: null
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
    }
  }
}
</script>

<style scoped>
.deposit-container {
  padding-bottom: 24px;
}
</style>
