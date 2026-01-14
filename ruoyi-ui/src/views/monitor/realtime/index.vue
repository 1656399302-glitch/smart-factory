<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="80px">
      <el-form-item label="选择设备" prop="deviceNumber">
        <el-select v-model="queryParams.deviceNumber" placeholder="请选择设备" filterable @change="handleDeviceChange" style="width: 280px">
          <el-option v-for="item in deviceList" :key="item.deviceNumber" :label="item.name + ' (' + item.deviceNumber + ')'" :value="item.deviceNumber"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-refresh" size="mini" @click="refreshData">刷新数据</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据卡片展示 -->
    <div v-if="queryParams.deviceNumber" class="sensor-cards">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="item in latestDataList" :key="item.sensorId">
          <el-card class="sensor-card" :class="'type-' + item.sensorType" shadow="hover">
            <div class="card-header">
              <span class="sensor-name">{{ item.sensorName }}</span>
              <el-tag :type="getSensorTypeTag(item.sensorType)" size="mini">{{ getSensorTypeLabel(item.sensorType) }}</el-tag>
            </div>
            <div class="card-value">
              <span class="value">{{ item.value !== null ? item.value : '--' }}</span>
              <span class="unit">{{ item.unit || '' }}</span>
            </div>
            <div class="card-footer">
              <span class="sensor-code">{{ item.sensorCode }}</span>
              <span class="update-time">{{ item.dataTime ? parseTime(item.dataTime, '{m}-{d} {h}:{i}:{s}') : '暂无数据' }}</span>
            </div>
            <div class="card-actions">
              <el-button type="text" size="mini" @click="goToHistory(item)">
                <i class="el-icon-data-line"></i> 历史数据
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 未选择设备提示 -->
    <el-empty v-else description="请选择设备查看实时数据"></el-empty>

    <!-- 无传感器提示 -->
    <el-empty v-if="queryParams.deviceNumber && latestDataList.length === 0 && !loading" description="该设备暂无传感器数据"></el-empty>
  </div>
</template>

<script>
import { getLatestData } from "@/api/monitor/data";
import { listDevice } from "@/api/ar/device";

export default {
  name: "RealtimeData",
  data() {
    return {
      loading: false,
      deviceList: [],
      latestDataList: [],
      queryParams: {
        deviceNumber: null
      },
      refreshTimer: null
    };
  },
  created() {
    this.getDeviceList();
  },
  beforeDestroy() {
    if (this.refreshTimer) {
      clearInterval(this.refreshTimer);
    }
  },
  methods: {
    getDeviceList() {
      listDevice({}).then(response => {
        this.deviceList = response.rows;
        // 如果有设备，默认选中第一个
        if (this.deviceList.length > 0 && !this.queryParams.deviceNumber) {
          this.queryParams.deviceNumber = this.deviceList[0].deviceNumber;
          this.refreshData();
        }
      });
    },
    handleDeviceChange() {
      this.refreshData();
    },
    refreshData() {
      if (!this.queryParams.deviceNumber) return;
      this.loading = true;
      getLatestData(this.queryParams.deviceNumber).then(response => {
        this.latestDataList = response.data || [];
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    getSensorTypeLabel(type) {
      const types = { temperature: '温度', pressure: '压力', vibration: '振动', current: '电流', custom: '自定义' };
      return types[type] || type;
    },
    getSensorTypeTag(type) {
      const tags = { temperature: 'danger', pressure: 'warning', vibration: 'info', current: 'success', custom: '' };
      return tags[type] || '';
    },
    goToHistory(item) {
      this.$router.push({
        path: '/monitor/history',
        query: { deviceNumber: item.deviceNumber, sensorId: item.sensorId }
      });
    }
  }
};
</script>

<style scoped>
.sensor-cards {
  margin-top: 20px;
}
.sensor-card {
  margin-bottom: 20px;
  border-radius: 12px;
  transition: all 0.3s;
}
.sensor-card:hover {
  transform: translateY(-4px);
}
.sensor-card.type-temperature {
  border-top: 3px solid #F56C6C;
}
.sensor-card.type-pressure {
  border-top: 3px solid #E6A23C;
}
.sensor-card.type-vibration {
  border-top: 3px solid #909399;
}
.sensor-card.type-current {
  border-top: 3px solid #67C23A;
}
.sensor-card.type-custom {
  border-top: 3px solid #409EFF;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}
.sensor-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}
.card-value {
  text-align: center;
  padding: 20px 0;
}
.card-value .value {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
}
.card-value .unit {
  font-size: 16px;
  color: #909399;
  margin-left: 5px;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
  border-top: 1px solid #EBEEF5;
  padding-top: 12px;
}
.card-actions {
  text-align: center;
  margin-top: 10px;
}
</style>
