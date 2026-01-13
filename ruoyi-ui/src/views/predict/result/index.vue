<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      @submit.native.prevent
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="80px"
    >
      <el-form-item label="工厂" prop="factoryId">
        <el-select
          v-model="queryParams.factoryId"
          placeholder="请选择工厂"
          filterable
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="item in factoryList"
            :key="item.factoryId"
            :label="item.name"
            :value="item.factoryId"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="资产类型" prop="assetType">
        <el-select
          v-model="queryParams.assetType"
          placeholder="请选择资产类型"
          clearable
          style="width: 150px"
        >
          <el-option label="设备(equipment)" value="equipment"></el-option>
          <el-option label="装置(device)" value="device"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="风险等级" prop="riskLevel">
        <el-select
          v-model="queryParams.riskLevel"
          placeholder="请选择风险等级"
          clearable
          style="width: 120px"
        >
          <el-option label="高风险" value="high"></el-option>
          <el-option label="中风险" value="medium"></el-option>
          <el-option label="低风险" value="low"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          style="width: 120px"
        >
          <el-option label="待处理" value="new"></el-option>
          <el-option label="已确认" value="acknowledged"></el-option>
          <el-option label="已忽略" value="ignored"></el-option>
          <el-option label="已完成" value="resolved"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="预测时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['predict:result:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['predict:result:export']"
          >导出</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="resultList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="60" />
      <el-table-column label="资产类型" align="center" prop="assetType" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.assetType === 'equipment' ? 'primary' : 'info'" size="small">
            {{ scope.row.assetType === 'equipment' ? '设备' : '装置' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="资源名称"
        align="center"
        prop="assetName"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        label="工厂"
        align="center"
        prop="factoryName"
        width="120"
        show-overflow-tooltip
      />
      <el-table-column label="风险等级" align="center" prop="riskLevel" width="100">
        <template slot-scope="scope">
          <el-tag :type="getRiskLevelType(scope.row.riskLevel)" size="small">
            {{ getRiskLevelLabel(scope.row.riskLevel) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="风险分数" align="center" prop="riskScore" width="90">
        <template slot-scope="scope">
          <el-progress
            :percentage="scope.row.riskScore"
            :color="getRiskScoreColor(scope.row.riskScore)"
            :stroke-width="10"
            :show-text="true"
          ></el-progress>
        </template>
      </el-table-column>
      <el-table-column label="预测时间" align="center" prop="predictionTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.predictionTime, "{y}-{m}-{d} {h}:{i}") }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="原因摘要"
        align="center"
        prop="reasonSummary"
        min-width="200"
        show-overflow-tooltip
      />
      <el-table-column
        label="建议"
        align="center"
        prop="recommendation"
        min-width="180"
        show-overflow-tooltip
      />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)" size="small">
            {{ getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
        width="220"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['predict:result:view']"
            >详情</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleAck(scope.row)"
            v-if="scope.row.status === 'new'"
            v-hasPermi="['predict:result:handle']"
            >确认</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleIgnore(scope.row)"
            v-if="scope.row.status === 'new' || scope.row.status === 'acknowledged'"
            v-hasPermi="['predict:result:handle']"
            >忽略</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-success"
            @click="handleResolve(scope.row)"
            v-if="scope.row.status === 'acknowledged'"
            v-hasPermi="['predict:result:handle']"
            >完成</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 详情对话框 -->
    <el-dialog title="预测结果详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <div v-if="currentResult">
        <!-- 风险卡片 -->
        <el-card class="risk-card" :class="'risk-' + currentResult.riskLevel">
          <div class="risk-header">
            <div class="risk-score">
              <span class="score-value">{{ currentResult.riskScore }}</span>
              <span class="score-label">风险分数</span>
            </div>
            <div class="risk-info">
              <el-tag :type="getRiskLevelType(currentResult.riskLevel)" size="large">
                {{ getRiskLevelLabel(currentResult.riskLevel) }}
              </el-tag>
              <div class="asset-info">
                <span class="asset-type">{{ currentResult.assetType === 'equipment' ? '设备' : '装置' }}</span>
                <span class="asset-name">{{ currentResult.assetName }}</span>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 基本信息 -->
        <el-descriptions title="基本信息" :column="2" border style="margin-top: 20px">
          <el-descriptions-item label="预测时间">
            {{ parseTime(currentResult.predictionTime, "{y}-{m}-{d} {h}:{i}:{s}") }}
          </el-descriptions-item>
          <el-descriptions-item label="模型版本">
            {{ currentResult.modelVersion }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentResult.status)" size="small">
              {{ getStatusLabel(currentResult.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="处理人" v-if="currentResult.handledBy">
            {{ currentResult.handledBy }}
          </el-descriptions-item>
          <el-descriptions-item label="处理时间" v-if="currentResult.handledTime">
            {{ parseTime(currentResult.handledTime, "{y}-{m}-{d} {h}:{i}:{s}") }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 原因摘要 -->
        <el-card style="margin-top: 20px">
          <div slot="header">原因摘要</div>
          <div class="reason-summary">{{ currentResult.reasonSummary }}</div>
        </el-card>

        <!-- 原因详情 -->
        <el-card style="margin-top: 20px" v-if="reasonDetail">
          <div slot="header">原因详情（可解释）</div>
          <el-table :data="ruleList" border size="small">
            <el-table-column prop="name" label="规则" width="150"></el-table-column>
            <el-table-column prop="threshold" label="条件" width="120"></el-table-column>
            <el-table-column prop="value" label="实际值" width="100">
              <template slot-scope="scope">
                {{ scope.row.value !== null ? scope.row.value : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="score" label="贡献分" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.score > 0 ? 'warning' : 'info'" size="small">
                  +{{ scope.row.score }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div class="total-score">
            总计风险分数: <strong>{{ reasonDetail.total_score }}</strong>
          </div>
        </el-card>

        <!-- 建议 -->
        <el-card style="margin-top: 20px">
          <div slot="header">建议维护动作</div>
          <div class="recommendation">{{ currentResult.recommendation }}</div>
        </el-card>

        <!-- 历史维护记录 -->
        <el-card style="margin-top: 20px">
          <div slot="header">
            历史维护记录
          </div>
          <el-table :data="maintenanceList" border size="small" v-loading="maintenanceLoading">
            <el-table-column prop="recordId" label="记录ID" width="80"></el-table-column>
            <el-table-column prop="date" label="维护日期" width="120">
              <template slot-scope="scope">
                {{ parseTime(scope.row.date, "{y}-{m}-{d}") }}
              </template>
            </el-table-column>
            <el-table-column prop="maintainerName" label="维护人" width="100"></el-table-column>
            <el-table-column prop="maintenanceLog" label="维护日志" show-overflow-tooltip></el-table-column>
          </el-table>
          <div v-if="maintenanceList.length === 0 && !maintenanceLoading" class="no-data">
            暂无维护记录
          </div>
        </el-card>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listResult,
  getResult,
  getMaintenanceRecords,
  ackResult,
  ignoreResult,
  resolveResult,
  delResult,
} from "@/api/predict/result";
import { listFactory } from "@/api/ar/factory";

export default {
  name: "PredictResult",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 预测结果表格数据
      resultList: [],
      // 工厂列表
      factoryList: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        factoryId: null,
        assetType: null,
        riskLevel: null,
        status: null,
        beginPredictionTime: null,
        endPredictionTime: null,
      },
      // 详情对话框
      detailOpen: false,
      // 当前详情
      currentResult: null,
      // 原因详情JSON解析
      reasonDetail: null,
      // 规则列表
      ruleList: [],
      // 维护记录列表
      maintenanceList: [],
      // 维护记录加载状态
      maintenanceLoading: false,
    };
  },
  created() {
    this.getList();
    this.getFactoryList();
  },
  methods: {
    /** 查询预测结果列表 */
    getList() {
      this.loading = true;
      // 处理日期范围
      if (this.dateRange && this.dateRange.length === 2) {
        this.queryParams.beginPredictionTime = this.dateRange[0] + " 00:00:00";
        this.queryParams.endPredictionTime = this.dateRange[1] + " 23:59:59";
      } else {
        this.queryParams.beginPredictionTime = null;
        this.queryParams.endPredictionTime = null;
      }
      listResult(this.queryParams).then((response) => {
        this.resultList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 获取工厂列表 */
    getFactoryList() {
      listFactory({}).then((response) => {
        this.factoryList = response.rows;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.multiple = !selection.length;
    },
    /** 查看详情 */
    handleView(row) {
      this.currentResult = row;
      this.detailOpen = true;
      // 解析原因详情JSON
      if (row.reasonDetailJson) {
        try {
          this.reasonDetail = JSON.parse(row.reasonDetailJson);
          // 构建规则列表
          if (this.reasonDetail.rules) {
            this.ruleList = Object.values(this.reasonDetail.rules);
          } else {
            this.ruleList = [];
          }
        } catch (e) {
          this.reasonDetail = null;
          this.ruleList = [];
        }
      } else {
        this.reasonDetail = null;
        this.ruleList = [];
      }
      // 获取维护记录（equipment和device都支持）
      this.maintenanceList = [];
      if (row.assetType === "equipment" || row.assetType === "device") {
        this.maintenanceLoading = true;
        getMaintenanceRecords(row.assetType, row.assetId)
          .then((response) => {
            this.maintenanceList = response.data || [];
            this.maintenanceLoading = false;
          })
          .catch(() => {
            this.maintenanceLoading = false;
          });
      }
    },
    /** 确认操作 */
    handleAck(row) {
      this.$modal
        .confirm('是否确认该预测结果？确认后将进入"已确认"状态。')
        .then(() => {
          return ackResult(row.id);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("确认成功");
        })
        .catch(() => {});
    },
    /** 忽略操作 */
    handleIgnore(row) {
      this.$modal
        .confirm('是否忽略该预测结果？')
        .then(() => {
          return ignoreResult(row.id);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("已忽略");
        })
        .catch(() => {});
    },
    /** 完成操作 */
    handleResolve(row) {
      this.$modal
        .confirm('是否标记该预测结果为已完成？')
        .then(() => {
          return resolveResult(row.id);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("已完成");
        })
        .catch(() => {});
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除预测结果编号为"' + ids + '"的数据项？')
        .then(() => {
          return delResult(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "predict/result/export",
        {
          ...this.queryParams,
        },
        `predict_result_${new Date().getTime()}.xlsx`
      );
    },
    // 获取风险等级标签类型
    getRiskLevelType(level) {
      const types = {
        high: "danger",
        medium: "warning",
        low: "success",
      };
      return types[level] || "info";
    },
    // 获取风险等级标签
    getRiskLevelLabel(level) {
      const labels = {
        high: "高风险",
        medium: "中风险",
        low: "低风险",
      };
      return labels[level] || level;
    },
    // 获取风险分数颜色
    getRiskScoreColor(score) {
      if (score >= 80) return "#F56C6C";
      if (score >= 50) return "#E6A23C";
      return "#67C23A";
    },
    // 获取状态标签类型
    getStatusType(status) {
      const types = {
        new: "warning",
        acknowledged: "primary",
        ignored: "info",
        resolved: "success",
      };
      return types[status] || "info";
    },
    // 获取状态标签
    getStatusLabel(status) {
      const labels = {
        new: "待处理",
        acknowledged: "已确认",
        ignored: "已忽略",
        resolved: "已完成",
      };
      return labels[status] || status;
    },
  },
};
</script>

<style scoped>
.risk-card {
  border-radius: 8px;
}
.risk-card.risk-high {
  border-left: 4px solid #F56C6C;
  background: linear-gradient(135deg, #fff5f5 0%, #fff 100%);
}
.risk-card.risk-medium {
  border-left: 4px solid #E6A23C;
  background: linear-gradient(135deg, #fdf6ec 0%, #fff 100%);
}
.risk-card.risk-low {
  border-left: 4px solid #67C23A;
  background: linear-gradient(135deg, #f0f9eb 0%, #fff 100%);
}
.risk-header {
  display: flex;
  align-items: center;
  gap: 30px;
}
.risk-score {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 20px;
}
.score-value {
  font-size: 42px;
  font-weight: bold;
  color: #303133;
}
.score-label {
  font-size: 14px;
  color: #909399;
}
.risk-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.asset-info {
  font-size: 16px;
  color: #606266;
}
.asset-type {
  margin-right: 10px;
  color: #909399;
}
.asset-name {
  font-weight: 500;
}
.reason-summary {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
}
.recommendation {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
}
.total-score {
  margin-top: 15px;
  text-align: right;
  font-size: 14px;
  color: #606266;
}
.total-score strong {
  font-size: 20px;
  color: #E6A23C;
}
.no-data {
  text-align: center;
  padding: 20px;
  color: #909399;
}
.no-data-hint {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
}
</style>
