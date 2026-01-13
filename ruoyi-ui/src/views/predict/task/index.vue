<template>
  <div class="app-container">
    <el-card class="task-card">
      <div slot="header" class="card-header">
        <span class="title">手动触发预测</span>
        <span class="subtitle">基于P0规则引擎，对设备进行风险预测分析</span>
      </div>

      <el-form
        ref="taskForm"
        :model="taskForm"
        :rules="rules"
        label-width="120px"
        class="task-form"
      >
        <el-form-item label="资产类型" prop="assetType">
          <el-select
            v-model="taskForm.assetType"
            placeholder="请选择资产类型"
            style="width: 300px"
          >
            <el-option label="装置(device)" value="device"></el-option>
            <el-option label="设备(equipment)" value="equipment"></el-option>
          </el-select>
          <span class="form-tip">选择要进行预测的资产类型</span>
        </el-form-item>

        <el-form-item label="工厂范围" prop="factoryId">
          <el-select
            v-model="taskForm.factoryId"
            placeholder="全部工厂"
            filterable
            clearable
            style="width: 300px"
          >
            <el-option
              v-for="item in factoryList"
              :key="item.factoryId"
              :label="item.name"
              :value="item.factoryId"
            ></el-option>
          </el-select>
          <span class="form-tip">可选，不选则对全部工厂进行预测</span>
        </el-form-item>

        <el-form-item label="指定设备ID" prop="assetIdsStr">
          <el-input
            v-model="taskForm.assetIdsStr"
            type="textarea"
            :rows="3"
            placeholder="可选，输入设备ID，多个ID用逗号分隔，如：1,2,3"
            style="width: 400px"
          ></el-input>
          <span class="form-tip">可选，不填则对范围内全部设备进行预测</span>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            icon="el-icon-video-play"
            :loading="running"
            @click="handleRun"
            v-hasPermi="['predict:task:run']"
          >
            {{ running ? '预测中...' : '运行预测' }}
          </el-button>
          <el-button icon="el-icon-refresh" @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 执行结果 -->
    <el-card class="result-card" v-if="lastResult">
      <div slot="header" class="card-header">
        <span class="title">执行结果</span>
        <el-tag :type="lastResult.success ? 'success' : 'danger'" size="small">
          {{ lastResult.success ? '成功' : '失败' }}
        </el-tag>
      </div>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="执行状态">
          <el-tag :type="lastResult.success ? 'success' : 'danger'">
            {{ lastResult.success ? '执行成功' : '执行失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="执行耗时">
          {{ lastResult.duration }}ms
        </el-descriptions-item>
        <el-descriptions-item label="生成数量">
          <span class="stat-value success">{{ lastResult.generatedCount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="跳过数量">
          <span class="stat-value info">{{ lastResult.skippedCount }}</span>
          <span class="stat-hint">（当天已有预测记录）</span>
        </el-descriptions-item>
        <el-descriptions-item label="错误数量">
          <span class="stat-value" :class="lastResult.errorCount > 0 ? 'danger' : ''">
            {{ lastResult.errorCount }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="消息">
          {{ lastResult.message }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="result-actions" v-if="lastResult.success && lastResult.generatedCount > 0">
        <el-button type="primary" icon="el-icon-view" @click="goToResultList">
          查看预测结果
        </el-button>
      </div>

      <div class="error-list" v-if="lastResult.errors && lastResult.errors.length > 0">
        <el-alert
          title="错误详情"
          type="error"
          :closable="false"
        >
          <ul>
            <li v-for="(err, index) in lastResult.errors" :key="index">{{ err }}</li>
          </ul>
        </el-alert>
      </div>
    </el-card>

    <!-- 使用说明 -->
    <el-card class="help-card">
      <div slot="header" class="card-header">
        <span class="title">使用说明</span>
      </div>
      <div class="help-content">
        <h4>P0预测规则说明</h4>
        <p>本功能使用基于规则的预测引擎（RULE_P0_v1），通过分析设备的历史维护记录，计算风险分数并生成预测结果。</p>

        <h4>评分规则</h4>
        <el-table :data="ruleData" border size="small" style="margin: 15px 0">
          <el-table-column prop="rule" label="规则" width="80"></el-table-column>
          <el-table-column prop="name" label="名称" width="150"></el-table-column>
          <el-table-column prop="condition" label="条件"></el-table-column>
          <el-table-column prop="score" label="贡献分" width="80"></el-table-column>
        </el-table>

        <h4>风险等级</h4>
        <ul>
          <li><el-tag type="danger" size="small">高风险</el-tag> 风险分数 ≥ 80</li>
          <li><el-tag type="warning" size="small">中风险</el-tag> 50 ≤ 风险分数 &lt; 80</li>
          <li><el-tag type="success" size="small">低风险</el-tag> 风险分数 &lt; 50</li>
        </ul>

        <h4>注意事项</h4>
        <ul>
          <li>每个设备每天只会生成一条预测记录，重复运行会跳过已有记录</li>
          <li>支持device和equipment两种资产类型，根据实际数据选择</li>
          <li>预测结果需要人工确认或处理，支持确认/忽略/完成操作</li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script>
import { runPrediction } from "@/api/predict/task";
import { listFactory } from "@/api/ar/factory";

export default {
  name: "PredictTask",
  data() {
    return {
      // 任务表单
      taskForm: {
        assetType: "device",
        factoryId: null,
        assetIdsStr: "",
      },
      // 表单校验
      rules: {
        assetType: [
          { required: true, message: "请选择资产类型", trigger: "change" },
        ],
      },
      // 工厂列表
      factoryList: [],
      // 运行状态
      running: false,
      // 上次执行结果
      lastResult: null,
      // 规则说明数据
      ruleData: [
        { rule: "R1", name: "距上次维护天数", condition: ">90天: +40分; 60-90天: +25分; 30-60天: +10分", score: "0-40" },
        { rule: "R2", name: "近30天维护次数", condition: "≥3次: +35分; 2次: +20分; 1次: +8分", score: "0-35" },
        { rule: "R3", name: "近90天维护次数", condition: "≥6次: +25分; 3-5次: +12分", score: "0-25" },
        { rule: "R4", name: "关键词命中", condition: "维护记录含异常关键词: +15分", score: "0-15" },
      ],
    };
  },
  created() {
    this.getFactoryList();
  },
  methods: {
    /** 获取工厂列表 */
    getFactoryList() {
      listFactory({}).then((response) => {
        this.factoryList = response.rows;
      });
    },
    /** 运行预测 */
    handleRun() {
      this.$refs["taskForm"].validate((valid) => {
        if (valid) {
          this.running = true;

          // 解析assetIds
          let assetIds = null;
          if (this.taskForm.assetIdsStr && this.taskForm.assetIdsStr.trim()) {
            const ids = this.taskForm.assetIdsStr
              .split(",")
              .map((s) => s.trim())
              .filter((s) => s && !isNaN(Number(s)))
              .map((s) => Number(s));
            if (ids.length > 0) {
              assetIds = ids;
            }
          }

          const data = {
            assetType: this.taskForm.assetType,
            factoryId: this.taskForm.factoryId,
            assetIds: assetIds,
          };

          runPrediction(data)
            .then((response) => {
              this.lastResult = response.data || response;
              if (this.lastResult.success) {
                this.$modal.msgSuccess("预测任务执行完成");
              } else {
                this.$modal.msgError(this.lastResult.message || "预测任务执行失败");
              }
            })
            .catch((error) => {
              this.lastResult = {
                success: false,
                message: error.message || "请求失败",
                generatedCount: 0,
                skippedCount: 0,
                errorCount: 1,
                duration: 0,
              };
              this.$modal.msgError("预测任务执行失败");
            })
            .finally(() => {
              this.running = false;
            });
        }
      });
    },
    /** 重置表单 */
    resetForm() {
      this.taskForm = {
        assetType: "device",
        factoryId: null,
        assetIdsStr: "",
      };
      this.$refs["taskForm"].resetFields();
    },
    /** 跳转到结果列表 */
    goToResultList() {
      this.$router.push("/predict/result");
    },
  },
};

</script>

<style scoped>
.task-card,
.result-card,
.help-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 15px;
}
.card-header .title {
  font-size: 16px;
  font-weight: 500;
}
.card-header .subtitle {
  font-size: 14px;
  color: #909399;
}
.task-form {
  max-width: 600px;
}
.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
.stat-value {
  font-size: 18px;
  font-weight: bold;
}
.stat-value.success {
  color: #67C23A;
}
.stat-value.info {
  color: #909399;
}
.stat-value.danger {
  color: #F56C6C;
}
.stat-hint {
  margin-left: 8px;
  font-size: 12px;
  color: #909399;
}
.result-actions {
  margin-top: 20px;
  text-align: center;
}
.error-list {
  margin-top: 20px;
}
.error-list ul {
  margin: 10px 0 0 20px;
  padding: 0;
}
.help-content {
  color: #606266;
  line-height: 1.8;
}
.help-content h4 {
  margin: 20px 0 10px 0;
  color: #303133;
}
.help-content h4:first-child {
  margin-top: 0;
}
.help-content ul {
  margin: 10px 0;
  padding-left: 20px;
}
.help-content li {
  margin: 5px 0;
}
</style>
