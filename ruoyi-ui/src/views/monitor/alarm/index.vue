<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="设备" prop="deviceNumber">
        <el-select v-model="queryParams.deviceNumber" placeholder="请选择设备" filterable clearable style="width: 200px">
          <el-option v-for="item in deviceList" :key="item.deviceNumber" :label="item.name" :value="item.deviceNumber"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="严重程度" prop="severity">
        <el-select v-model="queryParams.severity" placeholder="请选择" clearable style="width: 120px">
          <el-option label="高" value="high"></el-option>
          <el-option label="中" value="medium"></el-option>
          <el-option label="低" value="low"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 120px">
          <el-option label="待处理" value="new"></el-option>
          <el-option label="已确认" value="acknowledged"></el-option>
          <el-option label="已关闭" value="resolved"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="报警时间">
        <el-date-picker v-model="dateRange" type="datetimerange" range-separator="-" start-placeholder="开始时间" end-placeholder="结束时间" value-format="yyyy-MM-dd HH:mm:ss" style="width: 340px"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['monitor:alarm:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="alarmList">
      <el-table-column label="报警ID" align="center" prop="alarmId" width="80" />
      <el-table-column label="设备" align="center" prop="deviceName" min-width="120" show-overflow-tooltip />
      <el-table-column label="传感器" align="center" min-width="140">
        <template slot-scope="scope">
          <div>{{ scope.row.sensorName }}</div>
          <div class="sub-text">{{ scope.row.sensorCode }}</div>
        </template>
      </el-table-column>
      <el-table-column label="触发规则" align="center" prop="ruleName" min-width="150" show-overflow-tooltip />
      <el-table-column label="触发值" align="center" width="120">
        <template slot-scope="scope">
          <span class="alarm-value">{{ scope.row.alarmValue }}</span>
          <span class="unit-text">{{ scope.row.unit }}</span>
        </template>
      </el-table-column>
      <el-table-column label="严重程度" align="center" prop="severity" width="100">
        <template slot-scope="scope">
          <el-tag :type="getSeverityType(scope.row.severity)" size="small">{{ getSeverityLabel(scope.row.severity) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)" size="small">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报警时间" align="center" prop="alarmTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.alarmTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="处理人" align="center" prop="handledBy" width="100" />
      <el-table-column label="处理时间" align="center" prop="handledTime" width="160">
        <template slot-scope="scope">
          <span>{{ scope.row.handledTime ? parseTime(scope.row.handledTime) : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['monitor:alarm:query']">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-check" @click="handleAck(scope.row)" v-if="scope.row.status === 'new'" v-hasPermi="['monitor:alarm:handle']">确认</el-button>
          <el-button size="mini" type="text" icon="el-icon-close" @click="handleResolve(scope.row)" v-if="scope.row.status !== 'resolved'" v-hasPermi="['monitor:alarm:handle']">关闭</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 详情对话框 -->
    <el-dialog title="报警详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border v-if="currentAlarm">
        <el-descriptions-item label="报警ID">{{ currentAlarm.alarmId }}</el-descriptions-item>
        <el-descriptions-item label="严重程度">
          <el-tag :type="getSeverityType(currentAlarm.severity)" size="small">{{ getSeverityLabel(currentAlarm.severity) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="设备">{{ currentAlarm.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="传感器">{{ currentAlarm.sensorName }} ({{ currentAlarm.sensorCode }})</el-descriptions-item>
        <el-descriptions-item label="触发规则" :span="2">{{ currentAlarm.ruleName }}</el-descriptions-item>
        <el-descriptions-item label="触发值">{{ currentAlarm.alarmValue }} {{ currentAlarm.unit }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentAlarm.status)" size="small">{{ getStatusLabel(currentAlarm.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报警时间" :span="2">{{ parseTime(currentAlarm.alarmTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentAlarm.handledBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ currentAlarm.handledTime ? parseTime(currentAlarm.handledTime) : '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentAlarm.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 关闭对话框 -->
    <el-dialog title="关闭报警" :visible.sync="resolveOpen" width="500px" append-to-body>
      <el-form ref="resolveForm" :model="resolveForm" label-width="80px">
        <el-form-item label="处理备注">
          <el-input v-model="resolveForm.remark" type="textarea" :rows="3" placeholder="请输入处理备注（可选）" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitResolve">确认关闭</el-button>
        <el-button @click="resolveOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAlarm, getAlarm, ackAlarm, resolveAlarm } from "@/api/monitor/alarm";
import { listDevice } from "@/api/ar/device";

export default {
  name: "AlarmRecord",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      alarmList: [],
      deviceList: [],
      dateRange: [],
      detailOpen: false,
      resolveOpen: false,
      currentAlarm: null,
      resolveForm: {
        alarmId: null,
        remark: ''
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceNumber: null,
        severity: null,
        status: null,
        beginAlarmTime: null,
        endAlarmTime: null,
      }
    };
  },
  created() {
    this.getList();
    this.getDeviceList();
  },
  methods: {
    getList() {
      this.loading = true;
      if (this.dateRange && this.dateRange.length === 2) {
        this.queryParams.beginAlarmTime = this.dateRange[0];
        this.queryParams.endAlarmTime = this.dateRange[1];
      } else {
        this.queryParams.beginAlarmTime = null;
        this.queryParams.endAlarmTime = null;
      }
      listAlarm(this.queryParams).then(response => {
        this.alarmList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getDeviceList() {
      listDevice({}).then(response => {
        this.deviceList = response.rows;
      });
    },
    getSeverityType(severity) {
      const types = { high: 'danger', medium: 'warning', low: 'success' };
      return types[severity] || 'info';
    },
    getSeverityLabel(severity) {
      const labels = { high: '高', medium: '中', low: '低' };
      return labels[severity] || severity;
    },
    getStatusType(status) {
      const types = { new: 'danger', acknowledged: 'warning', resolved: 'success' };
      return types[status] || 'info';
    },
    getStatusLabel(status) {
      const labels = { new: '待处理', acknowledged: '已确认', resolved: '已关闭' };
      return labels[status] || status;
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleView(row) {
      getAlarm(row.alarmId).then(response => {
        this.currentAlarm = response.data;
        this.detailOpen = true;
      });
    },
    handleAck(row) {
      this.$modal.confirm('是否确认该报警？确认后将进入"已确认"状态。').then(() => {
        return ackAlarm(row.alarmId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("确认成功");
      }).catch(() => {});
    },
    handleResolve(row) {
      this.resolveForm.alarmId = row.alarmId;
      this.resolveForm.remark = '';
      this.resolveOpen = true;
    },
    submitResolve() {
      resolveAlarm(this.resolveForm.alarmId, this.resolveForm.remark).then(() => {
        this.resolveOpen = false;
        this.getList();
        this.$modal.msgSuccess("关闭成功");
      });
    },
    handleExport() {
      this.download('monitor/alarm/export', { ...this.queryParams }, `alarm_record_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>

<style scoped>
.sub-text {
  font-size: 12px;
  color: #909399;
}
.alarm-value {
  font-weight: bold;
  color: #F56C6C;
}
.unit-text {
  color: #909399;
  margin-left: 4px;
}
</style>
