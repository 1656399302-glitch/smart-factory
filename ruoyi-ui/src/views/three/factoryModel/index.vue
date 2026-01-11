<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="工厂" prop="factoryId">
        <el-select v-model="queryParams.factoryId" placeholder="请选择工厂" filterable clearable style="width: 220px">
          <el-option v-for="f in factoryList" :key="f.factoryId" :label="f.name" :value="f.factoryId" />
        </el-select>
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入模型名称" clearable style="width: 220px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 160px">
          <el-option label="draft" value="draft" />
          <el-option label="published" value="published" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['three:factoryModel:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['three:factoryModel:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['three:factoryModel:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="tableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="id" width="80" align="center" />
      <el-table-column label="工厂" prop="factoryId" width="120" align="center">
        <template slot-scope="scope">
          <span>{{ getFactoryName(scope.row.factoryId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="名称" prop="name" min-width="180" />
      <el-table-column label="模型地址" min-width="260">
        <template slot-scope="scope">
          <el-link :href="fileUrl(scope.row.modelUrl)" target="_blank" :underline="false">{{ scope.row.modelUrl }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="格式" prop="modelFormat" width="100" align="center" />
      <el-table-column label="状态" prop="status" width="120" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 'published' ? 'success' : 'info'">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['three:factoryModel:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-upload" @click="handlePublish(scope.row)" v-hasPermi="['three:factoryModel:publish']">发布</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['three:factoryModel:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="640px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="工厂" prop="factoryId">
          <el-select v-model="form.factoryId" placeholder="请选择工厂" filterable clearable style="width: 100%">
            <el-option v-for="f in factoryList" :key="f.factoryId" :label="f.name" :value="f.factoryId" />
          </el-select>
        </el-form-item>
        <el-form-item label="模型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入模型名称" />
        </el-form-item>
        <el-form-item label="模型文件" prop="modelUrl">
          <file-upload v-model="form.modelUrl" :limit="1" :fileType="['glb','gltf','fbx','obj']" :fileSize="50" />
        </el-form-item>
        <el-form-item label="模型格式" prop="modelFormat">
          <el-select v-model="form.modelFormat" placeholder="请选择格式" clearable style="width: 100%">
            <el-option label="glb" value="glb" />
            <el-option label="gltf" value="gltf" />
            <el-option label="fbx" value="fbx" />
            <el-option label="obj" value="obj" />
          </el-select>
        </el-form-item>
        <el-form-item label="默认相机(JSON)" prop="defaultCamera">
          <el-input type="textarea" :rows="4" v-model="form.defaultCamera" placeholder='例如：{"position":[0,0,10],"target":[0,0,0]}' />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" :rows="2" v-model="form.remark" placeholder="请输入备注" />
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
import { listFactory } from "@/api/ar/factory";
import {
  listFactoryModel,
  getFactoryModel,
  addFactoryModel,
  updateFactoryModel,
  delFactoryModel,
  publishFactoryModel,
} from "@/api/three/factoryModel";

export default {
  name: "ThreeFactoryModel",
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
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        factoryId: undefined,
        name: undefined,
        status: undefined,
      },
      form: {},
      rules: {
        factoryId: [{ required: true, message: "请选择工厂", trigger: "change" }],
        name: [{ required: true, message: "请输入模型名称", trigger: "blur" }],
        modelUrl: [{ required: true, message: "请上传模型文件", trigger: "blur" }],
      },
    };
  },
  created() {
    this.getFactoryList();
    this.getList();
  },
  methods: {
    fileUrl(path) {
      if (!path) return "";
      return process.env.VUE_APP_BASE_API + path;
    },
    getFactoryList() {
      listFactory({ pageNum: 1, pageSize: 1000 }).then((res) => {
        this.factoryList = res.rows || [];
      });
    },
    getFactoryName(factoryId) {
      const f = (this.factoryList || []).find((x) => String(x.factoryId) === String(factoryId));
      return f ? f.name : factoryId;
    },
    getList() {
      this.loading = true;
      listFactoryModel(this.queryParams).then((res) => {
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
        factoryId: undefined,
        name: undefined,
        modelUrl: undefined,
        modelFormat: undefined,
        status: "draft",
        defaultCamera: undefined,
        remark: undefined,
      };
      this.resetForm("form");
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "新增工厂模型";
    },
    handleUpdate(row) {
      const id = row.id || this.ids[0];
      this.reset();
      getFactoryModel(id).then((res) => {
        this.form = res.data || {};
        this.open = true;
        this.title = "修改工厂模型";
      });
    },
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (!valid) return;
        const req = this.form.id ? updateFactoryModel(this.form) : addFactoryModel(this.form);
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
        .confirm('是否确认删除工厂模型编号为"' + ids + '"的数据项？')
        .then(function () {
          return delFactoryModel(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    handlePublish(row) {
      const id = row.id;
      this.$modal
        .confirm('是否确认发布工厂模型编号为"' + id + '"？（同工厂将仅保留一个 published）')
        .then(function () {
          return publishFactoryModel(id);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("发布成功");
        })
        .catch(() => {});
    },
  },
};
</script>


