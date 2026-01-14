<template>
  <div class="app-container">
    <el-card class="collect-card">
      <div slot="header" class="card-header">
        <span>采集测试工具</span>
        <el-tag type="info" size="small">用于模拟设备/网关上报数据</el-tag>
      </div>

      <el-form ref="form" :model="form" :rules="rules" label-width="120px" style="max-width: 600px;">
        <el-form-item label="设备编号" prop="deviceNumber">
          <el-select v-model="form.deviceNumber" placeholder="请选择设备" filterable @change="handleDeviceChange" style="width: 100%">
            <el-option v-for="item in deviceList" :key="item.deviceNumber" :label="item.name + ' (' + item.deviceNumber + ')'" :value="item.deviceNumber"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="传感器编码" prop="sensorCode">
          <el-select v-model="form.sensorCode" placeholder="请选择传感器" filterable allow-create style="width: 100%">
            <el-option v-for="item in sensorList" :key="item.sensorCode" :label="item.sensorName + ' (' + item.sensorCode + ')'" :value="item.sensorCode"></el-option>
          </el-select>
          <div class="form-tip">可输入自定义编码或从列表选择</div>
        </el-form-item>

        <el-form-item label="采集值" prop="value">
          <el-input-number v-model="form.value" :precision="2" :controls="false" placeholder="请输入采集值" style="width: 100%" />
        </el-form-item>

        <el-form-item label="采集时间">
          <el-date-picker v-model="form.dataTime" type="datetime" placeholder="选择时间（留空使用服务器时间）" value-format="yyyy-MM-dd HH:mm:ss" style="width: 100%"></el-date-picker>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitCollect" :loading="submitting">
            <i class="el-icon-upload2"></i> 上报数据
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 结果展示 -->
    <el-card class="result-card" style="margin-top: 20px;" v-if="resultList.length > 0">
      <div slot="header">
        <span>上报结果记录</span>
        <el-button type="text" style="float: right; padding: 0" @click="clearResults">清空记录</el-button>
      </div>
      <el-timeline>
        <el-timeline-item v-for="(item, index) in resultList" :key="index" :type="item.success ? 'success' : 'danger'" :timestamp="item.time" placement="top">
          <el-card :class="item.success ? 'result-success' : 'result-error'">
            <div class="result-header">
              <el-tag :type="item.success ? 'success' : 'danger'" size="small">{{ item.success ? '成功' : '失败' }}</el-tag>
              <span class="result-message">{{ item.message }}</span>
            </div>
            <div class="result-data" v-if="item.success">
              <span>设备: {{ item.deviceNumber }}</span>
              <span>传感器: {{ item.sensorCode }}</span>
              <span>值: {{ item.value }}</span>
              <span v-if="item.alarmCount > 0" class="alarm-count">
                <el-tag type="warning" size="mini">触发 {{ item.alarmCount }} 条报警</el-tag>
              </span>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <!-- API文档 -->
    <el-card class="api-card" style="margin-top: 20px;">
      <div slot="header">
        <span>API 接口说明</span>
      </div>
      <div class="api-doc">
        <h4>采集上报接口</h4>
        <p><code>POST /monitor/data/collect</code></p>
        <h4>请求参数 (JSON Body)</h4>
        <pre>
{
  "deviceNumber": 15,        // 设备编号（必填）
  "sensorCode": "TEMP_001",  // 传感器编码（必填）
  "value": 68.5,             // 采集值（必填）
  "dataTime": null           // 采集时间（可选，不传使用服务器时间）
}</pre>
        <h4>返回示例</h4>
        <pre>
{
  "code": 200,
  "msg": "数据上报成功",
  "data": {
    "success": true,
    "sensorId": 1,
    "dataId": 100,
    "alarmCount": 0
  }
}</pre>
      </div>
    </el-card>
  </div>
</template>

<script>
import { collectData } from "@/api/monitor/data";
import { listDevice } from "@/api/ar/device";
import { listSensorByDevice } from "@/api/monitor/sensor";

export default {
  name: "CollectTest",
  data() {
    return {
      submitting: false,
      deviceList: [],
      sensorList: [],
      resultList: [],
      form: {
        deviceNumber: null,
        sensorCode: '',
        value: null,
        dataTime: null
      },
      rules: {
        deviceNumber: [{ required: true, message: "请选择设备", trigger: "change" }],
        sensorCode: [{ required: true, message: "请输入传感器编码", trigger: "blur" }],
        value: [{ required: true, message: "请输入采集值", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getDeviceList();
  },
  methods: {
    getDeviceList() {
      listDevice({}).then(response => {
        this.deviceList = response.rows;
      });
    },
    handleDeviceChange(val) {
      this.form.sensorCode = '';
      this.sensorList = [];
      if (val) {
        listSensorByDevice(val).then(response => {
          this.sensorList = response.data || [];
        });
      }
    },
    submitCollect() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.submitting = true;
          collectData(this.form).then(response => {
            const result = {
              success: response.code === 200,
              message: response.msg,
              time: this.parseTime(new Date()),
              deviceNumber: this.form.deviceNumber,
              sensorCode: this.form.sensorCode,
              value: this.form.value,
              alarmCount: response.data ? response.data.alarmCount : 0
            };
            this.resultList.unshift(result);
            if (result.success) {
              this.$modal.msgSuccess("数据上报成功");
            }
            this.submitting = false;
          }).catch(() => {
            this.submitting = false;
          });
        }
      });
    },
    resetForm() {
      this.form = {
        deviceNumber: null,
        sensorCode: '',
        value: null,
        dataTime: null
      };
      this.sensorList = [];
      this.resetForm("form");
    },
    clearResults() {
      this.resultList = [];
    }
  }
};
</script>

<style scoped>
.collect-card {
  border-radius: 12px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.result-card {
  border-radius: 12px;
}
.result-success {
  background: #f0f9eb;
  border-color: #c2e7b0;
}
.result-error {
  background: #fef0f0;
  border-color: #fbc4c4;
}
.result-header {
  display: flex;
  align-items: center;
  gap: 10px;
}
.result-message {
  font-weight: 500;
}
.result-data {
  margin-top: 10px;
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #606266;
}
.alarm-count {
  margin-left: auto;
}
.api-card {
  border-radius: 12px;
}
.api-doc h4 {
  margin: 15px 0 10px 0;
  color: #303133;
}
.api-doc h4:first-child {
  margin-top: 0;
}
.api-doc code {
  background: #f4f4f5;
  padding: 2px 8px;
  border-radius: 4px;
  color: #409EFF;
}
.api-doc pre {
  background: #f4f4f5;
  padding: 15px;
  border-radius: 8px;
  overflow-x: auto;
  font-size: 13px;
  line-height: 1.6;
}
</style>
