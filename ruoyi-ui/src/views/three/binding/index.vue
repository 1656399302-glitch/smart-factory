<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="工厂模型ID" prop="factoryModelId">
        <el-input v-model="queryParams.factoryModelId" placeholder="请输入工厂模型ID" clearable style="width: 200px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备编号" prop="deviceNumber">
        <el-input v-model="queryParams.deviceNumber" placeholder="请输入设备编号" clearable style="width: 200px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['three:binding:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['three:binding:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['three:binding:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="tableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="id" width="80" align="center" />
      <el-table-column label="工厂模型ID" prop="factoryModelId" width="120" align="center" />
      <el-table-column label="设备编号" prop="deviceNumber" width="120" align="center" />
      <el-table-column label="设备名称" min-width="160">
        <template slot-scope="scope">
          <span>{{ getDeviceName(scope.row.deviceNumber) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="设备模型ID" prop="deviceModelId" width="120" align="center" />
      <el-table-column label="设备模型名称" min-width="160">
        <template slot-scope="scope">
          <span>{{ getDeviceModelName(scope.row.deviceModelId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="位置(x,y,z)" min-width="180">
        <template slot-scope="scope">
          <span>{{ scope.row.positionX }}, {{ scope.row.positionY }}, {{ scope.row.positionZ }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['three:binding:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['three:binding:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="820px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="工厂" prop="factoryId">
              <el-select v-model="form.factoryId" placeholder="请选择工厂" filterable clearable style="width: 100%" @change="handleFactoryChange">
                <el-option v-for="f in factoryList" :key="f.factoryId" :label="f.name" :value="f.factoryId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工厂模型" prop="factoryModelId">
              <el-select v-model="form.factoryModelId" placeholder="请选择工厂模型" filterable clearable style="width: 100%">
                <el-option v-for="m in factoryModelList" :key="m.id" :label="m.name + '（' + m.status + '）'" :value="m.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="设备" prop="deviceNumber">
              <el-select v-model="form.deviceNumber" placeholder="请选择设备" filterable clearable style="width: 100%">
                <el-option v-for="d in deviceList" :key="d.deviceNumber" :label="d.name + '（' + d.deviceNumber + '）'" :value="d.deviceNumber" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备模型" prop="deviceModelId">
              <el-select v-model="form.deviceModelId" placeholder="请选择设备模型" filterable clearable style="width: 100%">
                <el-option v-for="m in deviceModelList" :key="m.id" :label="m.name + '（' + m.id + '）'" :value="m.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">Transform</el-divider>

        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="位置 X" prop="positionX"><el-input-number v-model="form.positionX" :precision="6" :step="0.1" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="位置 Y" prop="positionY"><el-input-number v-model="form.positionY" :precision="6" :step="0.1" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="位置 Z" prop="positionZ"><el-input-number v-model="form.positionZ" :precision="6" :step="0.1" style="width: 100%" /></el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="旋转 X" prop="rotationX"><el-input-number v-model="form.rotationX" :precision="6" :step="0.1" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="旋转 Y" prop="rotationY"><el-input-number v-model="form.rotationY" :precision="6" :step="0.1" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="旋转 Z" prop="rotationZ"><el-input-number v-model="form.rotationZ" :precision="6" :step="0.1" style="width: 100%" /></el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="缩放 X" prop="scaleX"><el-input-number v-model="form.scaleX" :precision="6" :step="0.1" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="缩放 Y" prop="scaleY"><el-input-number v-model="form.scaleY" :precision="6" :step="0.1" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="缩放 Z" prop="scaleZ"><el-input-number v-model="form.scaleZ" :precision="6" :step="0.1" style="width: 100%" /></el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFactory } from "@/api/ar/factory";
import { listDevice } from "@/api/ar/device";
import { listFactoryModelByFactory, getFactoryModel } from "@/api/three/factoryModel";
import { listDeviceModel } from "@/api/three/deviceModel";
import { listBinding, getBinding, addBinding, updateBinding, delBinding } from "@/api/three/binding";

export default {
  name: "ThreeBinding",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      tableList: [],
      title: "",
      open: false,
      factoryList: [],
      factoryModelList: [],
      deviceList: [],
      deviceModelList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        factoryModelId: undefined,
        deviceNumber: undefined,
      },
      form: {},
      rules: {
        factoryId: [{ required: true, message: "请选择工厂", trigger: "change" }],
        factoryModelId: [{ required: true, message: "请选择工厂模型", trigger: "change" }],
        deviceNumber: [{ required: true, message: "请选择设备", trigger: "change" }],
      },
    };
  },
  created() {
    this.getBaseLists();
    this.getList();
  },
  methods: {
    getBaseLists() {
      listFactory({ pageNum: 1, pageSize: 1000 }).then((res) => (this.factoryList = res.rows || []));
      listDevice({ pageNum: 1, pageSize: 1000 }).then((res) => (this.deviceList = res.rows || []));
      listDeviceModel({ pageNum: 1, pageSize: 1000 }).then((res) => (this.deviceModelList = res.rows || []));
    },
    handleFactoryChange(factoryId) {
      this.form.factoryModelId = undefined;
      if (!factoryId) {
        this.factoryModelList = [];
        return;
      }
      listFactoryModelByFactory(factoryId).then((res) => {
        this.factoryModelList = res.data || [];
      });
    },
    getDeviceName(deviceNumber) {
      const d = (this.deviceList || []).find((x) => String(x.deviceNumber) === String(deviceNumber));
      return d ? d.name : deviceNumber;
    },
    getDeviceModelName(deviceModelId) {
      const m = (this.deviceModelList || []).find((x) => String(x.id) === String(deviceModelId));
      return m ? m.name : deviceModelId;
    },
    getList() {
      this.loading = true;
      listBinding(this.queryParams).then((res) => {
        this.tableList = res.rows || [];
        this.total = res.total || 0;
        this.loading = false;
      });
    },
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    reset() {
      this.form = {
        id: undefined,
        factoryId: undefined, // 仅前端使用，用于联动选择 factoryModelId
        factoryModelId: undefined,
        deviceNumber: undefined,
        deviceModelId: undefined,
        positionX: 0,
        positionY: 0,
        positionZ: 0,
        rotationX: 0,
        rotationY: 0,
        rotationZ: 0,
        scaleX: 1,
        scaleY: 1,
        scaleZ: 1,
      };
      this.resetForm("form");
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    handleAdd() {
      this.reset();
      // 每次打开弹窗刷新基础下拉（避免新建设备模型后此页不刷新导致“选不出来”）
      this.getBaseLists();
      this.open = true;
      this.title = "新增点位绑定";
    },
    handleUpdate(row) {
      const id = row.id || this.ids[0];
      this.reset();
      // 每次打开弹窗刷新基础下拉
      this.getBaseLists();
      getBinding(id).then((res) => {
        const data = res.data || {};
        this.form = Object.assign(this.form, data);
        // 编辑时通过 factoryModelId 反查 factoryId，联动加载工厂模型下拉，保证回显正常
        if (this.form.factoryModelId) {
          getFactoryModel(this.form.factoryModelId).then((r) => {
            const fm = r.data || {};
            if (fm.factoryId) {
              this.form.factoryId = fm.factoryId;
              this.handleFactoryChange(fm.factoryId);
            }
          });
        }
        this.open = true;
        this.title = "修改点位绑定";
      });
    },
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (!valid) return;
        const payload = Object.assign({}, this.form);
        delete payload.factoryId; // 后端无此字段
        const req = payload.id ? updateBinding(payload) : addBinding(payload);
        req.then(() => {
          this.$modal.msgSuccess("操作成功");
          this.open = false;
          this.getList();
        });
      });
    },
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除点位绑定编号为"' + ids + '"的数据项？')
        .then(function () {
          return delBinding(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
  },
};
</script>


