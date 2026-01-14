<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="设备" prop="deviceNumber">
        <el-select v-model="queryParams.deviceNumber" placeholder="请选择设备" filterable clearable @change="handleDeviceChange" style="width: 200px">
          <el-option v-for="item in deviceList" :key="item.deviceNumber" :label="item.name" :value="item.deviceNumber"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="传感器" prop="sensorId">
        <el-select v-model="queryParams.sensorId" placeholder="请选择传感器" filterable clearable style="width: 200px">
          <el-option v-for="item in sensorList" :key="item.sensorId" :label="item.sensorName" :value="item.sensorId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker v-model="dateRange" type="datetimerange" range-separator="-" start-placeholder="开始时间" end-placeholder="结束时间" value-format="yyyy-MM-dd HH:mm:ss" style="width: 340px"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['monitor:data:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['monitor:data:view']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="设备" align="center" prop="deviceName" min-width="120" show-overflow-tooltip />
      <el-table-column label="传感器" align="center" prop="sensorName" min-width="140" show-overflow-tooltip />
      <el-table-column label="传感器编码" align="center" prop="sensorCode" width="120" />
      <el-table-column label="采集值" align="center" width="120">
        <template slot-scope="scope">
          <span class="value-cell">{{ scope.row.value }}</span>
          <span class="unit-cell">{{ scope.row.unit }}</span>
        </template>
      </el-table-column>
      <el-table-column label="采集时间" align="center" prop="dataTime" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.dataTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="入库时间" align="center" prop="createTime" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['monitor:data:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>
  </div>
</template>

<script>
import { listSensorData, delSensorData } from "@/api/monitor/data";
import { listDevice } from "@/api/ar/device";
import { listSensorByDevice } from "@/api/monitor/sensor";

export default {
  name: "HistoryData",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      ids: [],
      multiple: true,
      dataList: [],
      deviceList: [],
      sensorList: [],
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceNumber: null,
        sensorId: null,
        beginDataTime: null,
        endDataTime: null,
      }
    };
  },
  created() {
    // 从路由获取参数
    if (this.$route.query.deviceNumber) {
      this.queryParams.deviceNumber = parseInt(this.$route.query.deviceNumber);
    }
    if (this.$route.query.sensorId) {
      this.queryParams.sensorId = parseInt(this.$route.query.sensorId);
    }
    this.getDeviceList();
  },
  methods: {
    getList() {
      this.loading = true;
      if (this.dateRange && this.dateRange.length === 2) {
        this.queryParams.beginDataTime = this.dateRange[0];
        this.queryParams.endDataTime = this.dateRange[1];
      } else {
        this.queryParams.beginDataTime = null;
        this.queryParams.endDataTime = null;
      }
      listSensorData(this.queryParams).then(response => {
        this.dataList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getDeviceList() {
      listDevice({}).then(response => {
        this.deviceList = response.rows;
        if (this.queryParams.deviceNumber) {
          this.getSensorList(this.queryParams.deviceNumber);
        }
        this.getList();
      });
    },
    handleDeviceChange(val) {
      this.queryParams.sensorId = null;
      this.sensorList = [];
      if (val) {
        this.getSensorList(val);
      }
    },
    getSensorList(deviceNumber) {
      listSensorByDevice(deviceNumber).then(response => {
        this.sensorList = response.data || [];
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.dateRange = [];
      this.queryParams.sensorId = null;
      this.sensorList = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.multiple = !selection.length;
    },
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除数据编号为"' + ids + '"的数据项？').then(() => {
        return delSensorData(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('monitor/data/export', { ...this.queryParams }, `sensor_data_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>

<style scoped>
.value-cell {
  font-weight: bold;
  color: #409EFF;
}
.unit-cell {
  color: #909399;
  margin-left: 4px;
}
</style>
