<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="queryParams.ruleName" placeholder="请输入规则名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
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
      <el-form-item label="状态" prop="enabled">
        <el-select v-model="queryParams.enabled" placeholder="请选择" clearable style="width: 100px">
          <el-option label="启用" value="1"></el-option>
          <el-option label="停用" value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['monitor:rule:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['monitor:rule:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['monitor:rule:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ruleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="规则ID" align="center" prop="ruleId" width="80" />
      <el-table-column label="规则名称" align="center" prop="ruleName" min-width="150" show-overflow-tooltip />
      <el-table-column label="设备/传感器" align="center" min-width="180">
        <template slot-scope="scope">
          <div>{{ scope.row.deviceName }}</div>
          <div class="sub-text">{{ scope.row.sensorName }} ({{ scope.row.sensorCode }})</div>
        </template>
      </el-table-column>
      <el-table-column label="判定条件" align="center" width="200">
        <template slot-scope="scope">
          <span>{{ formatCondition(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="严重程度" align="center" prop="severity" width="100">
        <template slot-scope="scope">
          <el-tag :type="getSeverityType(scope.row.severity)" size="small">{{ getSeverityLabel(scope.row.severity) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="enabled" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enabled === '1' ? 'success' : 'info'" size="small">{{ scope.row.enabled === '1' ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['monitor:rule:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['monitor:rule:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="650px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="选择设备" prop="deviceNumber">
          <el-select v-model="form.deviceNumber" placeholder="请选择设备" filterable @change="handleFormDeviceChange" style="width: 100%">
            <el-option v-for="item in deviceList" :key="item.deviceNumber" :label="item.name" :value="item.deviceNumber"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择传感器" prop="sensorId">
          <el-select v-model="form.sensorId" placeholder="请先选择设备" filterable style="width: 100%">
            <el-option v-for="item in formSensorList" :key="item.sensorId" :label="item.sensorName + ' (' + item.sensorCode + ')'" :value="item.sensorId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="比较类型" prop="compareType">
          <el-select v-model="form.compareType" placeholder="请选择比较类型" @change="handleCompareTypeChange" style="width: 100%">
            <el-option label="大于 (>)" value="gt"></el-option>
            <el-option label="大于等于 (>=)" value="ge"></el-option>
            <el-option label="小于 (<)" value="lt"></el-option>
            <el-option label="小于等于 (<=)" value="le"></el-option>
            <el-option label="超出区间 (between)" value="between"></el-option>
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12" v-if="showThresholdMin">
            <el-form-item label="阈值下限" prop="thresholdMin">
              <el-input-number v-model="form.thresholdMin" :precision="2" :controls="false" placeholder="阈值下限" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="showThresholdMax">
            <el-form-item label="阈值上限" prop="thresholdMax">
              <el-input-number v-model="form.thresholdMax" :precision="2" :controls="false" placeholder="阈值上限" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="严重程度" prop="severity">
          <el-radio-group v-model="form.severity">
            <el-radio label="low">低</el-radio>
            <el-radio label="medium">中</el-radio>
            <el-radio label="high">高</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-switch v-model="form.enabled" active-value="1" inactive-value="0"></el-switch>
        </el-form-item>
        <el-form-item label="规则描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入规则描述" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRule, getRule, addRule, updateRule, delRule } from "@/api/monitor/rule";
import { listDevice } from "@/api/ar/device";
import { listSensorByDevice } from "@/api/monitor/sensor";

export default {
  name: "AlarmRule",
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      ruleList: [],
      deviceList: [],
      formSensorList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ruleName: null,
        deviceNumber: null,
        severity: null,
        enabled: null,
      },
      form: {},
      rules: {
        ruleName: [{ required: true, message: "请输入规则名称", trigger: "blur" }],
        deviceNumber: [{ required: true, message: "请选择设备", trigger: "change" }],
        sensorId: [{ required: true, message: "请选择传感器", trigger: "change" }],
        compareType: [{ required: true, message: "请选择比较类型", trigger: "change" }],
        severity: [{ required: true, message: "请选择严重程度", trigger: "change" }],
      }
    };
  },
  computed: {
    showThresholdMin() {
      return ['lt', 'le', 'between'].includes(this.form.compareType);
    },
    showThresholdMax() {
      return ['gt', 'ge', 'between'].includes(this.form.compareType);
    }
  },
  created() {
    this.getList();
    this.getDeviceList();
  },
  methods: {
    getList() {
      this.loading = true;
      listRule(this.queryParams).then(response => {
        this.ruleList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getDeviceList() {
      listDevice({}).then(response => {
        this.deviceList = response.rows;
      });
    },
    formatCondition(row) {
      const type = row.compareType;
      const min = row.thresholdMin;
      const max = row.thresholdMax;
      switch(type) {
        case 'gt': return `值 > ${max}`;
        case 'ge': return `值 >= ${max}`;
        case 'lt': return `值 < ${min}`;
        case 'le': return `值 <= ${min}`;
        case 'between': return `值 < ${min} 或 > ${max}`;
        default: return type;
      }
    },
    getSeverityType(severity) {
      const types = { high: 'danger', medium: 'warning', low: 'success' };
      return types[severity] || 'info';
    },
    getSeverityLabel(severity) {
      const labels = { high: '高', medium: '中', low: '低' };
      return labels[severity] || severity;
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        ruleId: null,
        ruleName: null,
        deviceNumber: null,
        sensorId: null,
        compareType: 'gt',
        thresholdMin: null,
        thresholdMax: null,
        severity: 'medium',
        enabled: '1',
        description: null
      };
      this.formSensorList = [];
      this.resetForm("form");
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.ruleId);
      this.multiple = !selection.length;
    },
    handleFormDeviceChange(val) {
      this.form.sensorId = null;
      this.formSensorList = [];
      if (val) {
        listSensorByDevice(val).then(response => {
          this.formSensorList = response.data || [];
        });
      }
    },
    handleCompareTypeChange() {
      // 清除不需要的阈值
      if (!this.showThresholdMin) this.form.thresholdMin = null;
      if (!this.showThresholdMax) this.form.thresholdMax = null;
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加报警规则";
    },
    handleUpdate(row) {
      this.reset();
      const ruleId = row.ruleId || this.ids;
      getRule(ruleId).then(response => {
        this.form = response.data;
        // 加载传感器列表
        if (this.form.deviceNumber) {
          listSensorByDevice(this.form.deviceNumber).then(res => {
            this.formSensorList = res.data || [];
          });
        }
        this.open = true;
        this.title = "修改报警规则";
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.ruleId != null) {
            updateRule(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addRule(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      const ruleIds = row.ruleId || this.ids;
      this.$modal.confirm('是否确认删除规则编号为"' + ruleIds + '"的数据项？').then(() => {
        return delRule(ruleIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('monitor/rule/export', { ...this.queryParams }, `alarm_rule_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>

<style scoped>
.sub-text {
  font-size: 12px;
  color: #909399;
}
</style>
