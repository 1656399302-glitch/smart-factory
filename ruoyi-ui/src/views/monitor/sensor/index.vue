<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="设备" prop="deviceNumber">
        <el-select v-model="queryParams.deviceNumber" placeholder="请选择设备" filterable clearable style="width: 200px">
          <el-option v-for="item in deviceList" :key="item.deviceNumber" :label="item.name" :value="item.deviceNumber"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="传感器编码" prop="sensorCode">
        <el-input v-model="queryParams.sensorCode" placeholder="请输入传感器编码" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="传感器类型" prop="sensorType">
        <el-select v-model="queryParams.sensorType" placeholder="请选择类型" clearable style="width: 140px">
          <el-option label="温度" value="temperature"></el-option>
          <el-option label="压力" value="pressure"></el-option>
          <el-option label="振动" value="vibration"></el-option>
          <el-option label="电流" value="current"></el-option>
          <el-option label="自定义" value="custom"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 100px">
          <el-option label="启用" value="0"></el-option>
          <el-option label="停用" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['monitor:sensor:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['monitor:sensor:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['monitor:sensor:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="sensorList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="传感器ID" align="center" prop="sensorId" width="90" />
      <el-table-column label="所属设备" align="center" prop="deviceName" min-width="120" show-overflow-tooltip />
      <el-table-column label="传感器编码" align="center" prop="sensorCode" width="120" />
      <el-table-column label="传感器名称" align="center" prop="sensorName" min-width="150" show-overflow-tooltip />
      <el-table-column label="类型" align="center" prop="sensorType" width="90">
        <template slot-scope="scope">
          <el-tag :type="getSensorTypeTag(scope.row.sensorType)" size="small">
            {{ getSensorTypeLabel(scope.row.sensorType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="单位" align="center" prop="unit" width="70" />
      <el-table-column label="采集周期(秒)" align="center" prop="collectInterval" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'" size="small">
            {{ scope.row.status === '0' ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['monitor:sensor:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['monitor:sensor:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属设备" prop="deviceNumber">
          <el-select v-model="form.deviceNumber" placeholder="请选择设备" filterable style="width: 100%">
            <el-option v-for="item in deviceList" :key="item.deviceNumber" :label="item.name" :value="item.deviceNumber"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="传感器编码" prop="sensorCode">
          <el-input v-model="form.sensorCode" placeholder="请输入传感器编码（设备侧唯一）" />
        </el-form-item>
        <el-form-item label="传感器名称" prop="sensorName">
          <el-input v-model="form.sensorName" placeholder="请输入传感器名称" />
        </el-form-item>
        <el-form-item label="传感器类型" prop="sensorType">
          <el-select v-model="form.sensorType" placeholder="请选择类型" style="width: 100%">
            <el-option label="温度" value="temperature"></el-option>
            <el-option label="压力" value="pressure"></el-option>
            <el-option label="振动" value="vibration"></el-option>
            <el-option label="电流" value="current"></el-option>
            <el-option label="自定义" value="custom"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位（℃、MPa、mm/s、A 等）" />
        </el-form-item>
        <el-form-item label="采集周期(秒)" prop="collectInterval">
          <el-input-number v-model="form.collectInterval" :min="1" :max="86400" placeholder="采集周期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
import { listSensor, getSensor, addSensor, updateSensor, delSensor } from "@/api/monitor/sensor";
import { listDevice } from "@/api/ar/device";

export default {
  name: "Sensor",
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      sensorList: [],
      deviceList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceNumber: null,
        sensorCode: null,
        sensorType: null,
        status: null,
      },
      form: {},
      rules: {
        deviceNumber: [{ required: true, message: "请选择所属设备", trigger: "change" }],
        sensorCode: [{ required: true, message: "请输入传感器编码", trigger: "blur" }],
        sensorName: [{ required: true, message: "请输入传感器名称", trigger: "blur" }],
        sensorType: [{ required: true, message: "请选择传感器类型", trigger: "change" }],
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
      listSensor(this.queryParams).then(response => {
        this.sensorList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getDeviceList() {
      listDevice({}).then(response => {
        this.deviceList = response.rows;
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
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        sensorId: null,
        deviceNumber: null,
        sensorCode: null,
        sensorName: null,
        sensorType: 'custom',
        unit: null,
        collectInterval: 60,
        status: "0",
        remark: null
      };
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
      this.ids = selection.map(item => item.sensorId);
      this.multiple = !selection.length;
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加传感器";
    },
    handleUpdate(row) {
      this.reset();
      const sensorId = row.sensorId || this.ids;
      getSensor(sensorId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改传感器";
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.sensorId != null) {
            updateSensor(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSensor(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      const sensorIds = row.sensorId || this.ids;
      this.$modal.confirm('是否确认删除传感器编号为"' + sensorIds + '"的数据项？').then(() => {
        return delSensor(sensorIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('monitor/sensor/export', { ...this.queryParams }, `sensor_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>
