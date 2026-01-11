<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="90px">
      <el-form-item label="工厂" prop="factoryId">
        <el-select v-model="queryParams.factoryId" placeholder="请选择工厂" filterable clearable style="width: 240px">
          <el-option v-for="f in factoryList" :key="f.factoryId" :label="f.name" :value="f.factoryId" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">加载</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="12">
      <el-col :span="16">
        <div class="three-viewer-wrap">
          <div ref="threeContainer" class="three-viewer-canvas"></div>
          <div v-if="threeLoading" class="three-viewer-overlay">
            <i class="el-icon-loading" style="margin-right: 8px"></i> 正在加载模型...
          </div>
          <div v-if="threeError" class="three-viewer-overlay three-viewer-error">
            {{ threeError }}
          </div>
          <div v-if="!viewer || !viewer.factoryModel" class="three-viewer-overlay">
            当前工厂暂无发布态工厂模型（请先在【工厂模型管理】发布）
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>发布态工厂模型</span>
          </div>
          <div v-if="viewer && viewer.factoryModel">
            <div><b>名称：</b>{{ viewer.factoryModel.name }}</div>
            <div><b>格式：</b>{{ viewer.factoryModel.modelFormat }}</div>
            <div style="margin-top: 6px">
              <b>地址：</b>
              <el-link :href="resolveUrl(viewer.factoryModel.modelUrl)" target="_blank" :underline="false">
                {{ viewer.factoryModel.modelUrl }}
              </el-link>
            </div>
            <div style="margin-top: 6px"><b>默认相机(JSON)：</b></div>
            <pre class="three-viewer-pre">{{ viewer.factoryModel.defaultCamera }}</pre>
          </div>
          <el-empty v-else description="暂无发布态模型" />
        </el-card>

        <el-card class="box-card" style="margin-top: 12px">
          <div slot="header" class="clearfix">
            <span>渲染设置</span>
          </div>
          <el-form size="small" label-width="110px">
            <el-form-item label="渲染设备模型">
              <el-switch v-model="useDeviceModels" />
            </el-form-item>
            <el-form-item label="编辑模式">
              <el-switch v-model="editMode" @change="onEditModeChanged" />
            </el-form-item>
            <el-form-item v-if="editMode" label="Gizmo 模式">
              <el-radio-group v-model="transformMode" size="mini" @change="onTransformModeChanged">
                <el-radio-button label="translate">平移(W)</el-radio-button>
                <el-radio-button label="rotate">旋转(E)</el-radio-button>
                <el-radio-button label="scale">缩放(R)</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="editMode" label="选中设备">
              <div>
                <span v-if="selectedDevice">
                  {{ selectedDevice.name }}（{{ selectedDevice.deviceNumber }}）
                </span>
                <span v-else>-</span>
              </div>
            </el-form-item>
            <el-form-item v-if="editMode" label="保存">
              <el-button type="primary" size="mini" :disabled="!selectedDevice || !selectedDevice.bindingId || saving" @click="saveSelected">
                保存选中
              </el-button>
              <el-button size="mini" :disabled="!viewer || !viewer.factoryModel" @click="exportSceneJson">
                导出场景JSON
              </el-button>
            </el-form-item>
            <el-form-item label="状态标签">
              <el-switch v-model="showStatusLabels" @change="renderViewer" />
            </el-form-item>
            <el-form-item label="显示未绑定设备">
              <el-switch v-model="showUnboundMarkers" />
            </el-form-item>
            <el-form-item label="设备统计">
              <div>
                总数：<b>{{ viewerDevices.length }}</b>，
                已绑定：<b>{{ boundDevicesCount }}</b>
              </div>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="viewerDevices" style="margin-top: 12px">
      <el-table-column label="设备编号" prop="deviceNumber" width="120" align="center" />
      <el-table-column label="设备名称" prop="name" min-width="160" />
      <el-table-column label="已绑定" prop="bindingId" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.bindingId ? 'success' : 'info'">{{ scope.row.bindingId ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="120" align="center">
        <template slot-scope="scope">
          <el-tag :style="{ backgroundColor: scope.row.statusColor, borderColor: scope.row.statusColor, color: '#fff' }">
            {{ scope.row.status || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="设备模型" min-width="220">
        <template slot-scope="scope">
          <el-link v-if="scope.row.deviceModelUrl" :href="fileUrl(scope.row.deviceModelUrl)" target="_blank" :underline="false">
            {{ scope.row.deviceModelUrl }}
          </el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="位置(x,y,z)" min-width="180">
        <template slot-scope="scope">
          <span>{{ scope.row.positionX }}, {{ scope.row.positionY }}, {{ scope.row.positionZ }}</span>
        </template>
      </el-table-column>
      <el-table-column label="旋转(x,y,z)" min-width="180">
        <template slot-scope="scope">
          <span>{{ scope.row.rotationX }}, {{ scope.row.rotationY }}, {{ scope.row.rotationZ }}</span>
        </template>
      </el-table-column>
      <el-table-column label="缩放(x,y,z)" min-width="180">
        <template slot-scope="scope">
          <span>{{ scope.row.scaleX }}, {{ scope.row.scaleY }}, {{ scope.row.scaleZ }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { listFactory } from "@/api/ar/factory";
import { getViewer } from "@/api/three/viewer";
import { updateBinding } from "@/api/three/binding";
import * as THREE from "three";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls.js";
import { TransformControls } from "three/examples/jsm/controls/TransformControls.js";
import { GLTFLoader } from "three/examples/jsm/loaders/GLTFLoader.js";
import { FBXLoader } from "three/examples/jsm/loaders/FBXLoader.js";
import { OBJLoader } from "three/examples/jsm/loaders/OBJLoader.js";
import { clone as skeletonClone } from "three/examples/jsm/utils/SkeletonUtils.js";

export default {
  name: "ThreeViewer",
  data() {
    return {
      loading: false,
      factoryList: [],
      queryParams: {
        factoryId: undefined,
      },
      viewer: null,
      showUnboundMarkers: false,
      useDeviceModels: true,
      editMode: false,
      transformMode: "translate",
      showStatusLabels: true,
      selectedDevice: null,
      saving: false,
      // three.js
      threeLoading: false,
      threeError: "",
      _three: {
        renderer: null,
        scene: null,
        camera: null,
        controls: null,
        rafId: null,
        modelGroup: null,
        markerGroup: null,
        deviceModelGroup: null,
        labelGroup: null,
        transformControls: null,
        raycaster: null,
        mouse: null,
        modelCache: null,
      },
    };
  },
  computed: {
    viewerDevices() {
      return (this.viewer && this.viewer.devices) ? this.viewer.devices : [];
    },
    boundDevicesCount() {
      return this.viewerDevices.filter((d) => d && d.bindingId).length;
    },
  },
  created() {
    this.getFactoryList();
  },
  mounted() {
    this.initThree();
    window.addEventListener("resize", this.onResize);
    if (this.$refs.threeContainer) {
      this.$refs.threeContainer.addEventListener("click", this.onClick);
    }
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.onResize);
    if (this.$refs.threeContainer) {
      this.$refs.threeContainer.removeEventListener("click", this.onClick);
    }
    this.disposeThree();
  },
  methods: {
    resolveUrl(path) {
      if (!path) return "";
      if (/^https?:\/\//i.test(path)) return path;
      return process.env.VUE_APP_BASE_API + path;
    },
    fileUrl(path) {
      return this.resolveUrl(path);
    },
    getFactoryList() {
      listFactory({ pageNum: 1, pageSize: 1000 }).then((res) => {
        this.factoryList = (res.rows || []);
      });
    },
    handleQuery() {
      if (!this.queryParams.factoryId) {
        this.$modal.msgWarning("请先选择工厂");
        return;
      }
      this.loading = true;
      getViewer(this.queryParams.factoryId)
        .then((res) => {
          this.viewer = res.data;
          this.$nextTick(() => {
            this.renderViewer();
          });
        })
        .finally(() => {
          this.loading = false;
        });
    },
    resetQuery() {
      this.queryParams.factoryId = undefined;
      this.viewer = null;
      this.clearScene();
      this.threeError = "";
    },

    initThree() {
      const container = this.$refs.threeContainer;
      if (!container) return;

      const width = container.clientWidth || 800;
      const height = container.clientHeight || 500;

      const scene = new THREE.Scene();
      scene.background = new THREE.Color(0xf5f7fa);

      const camera = new THREE.PerspectiveCamera(60, width / height, 0.1, 100000);
      camera.position.set(5, 5, 10);

      const renderer = new THREE.WebGLRenderer({ antialias: true });
      renderer.setSize(width, height);
      renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));

      container.innerHTML = "";
      container.appendChild(renderer.domElement);

      const controls = new OrbitControls(camera, renderer.domElement);
      controls.enableDamping = true;

      const hemi = new THREE.HemisphereLight(0xffffff, 0x444444, 1.0);
      hemi.position.set(0, 200, 0);
      scene.add(hemi);

      const dir = new THREE.DirectionalLight(0xffffff, 0.8);
      dir.position.set(50, 50, 25);
      scene.add(dir);

      const grid = new THREE.GridHelper(50, 50, 0xcccccc, 0xe0e0e0);
      grid.position.y = 0;
      scene.add(grid);

      const modelGroup = new THREE.Group();
      const markerGroup = new THREE.Group();
      const deviceModelGroup = new THREE.Group();
      const labelGroup = new THREE.Group();
      scene.add(modelGroup);
      scene.add(markerGroup);
      scene.add(deviceModelGroup);
      scene.add(labelGroup);

      const transformControls = new TransformControls(camera, renderer.domElement);
      transformControls.setMode(this.transformMode);
      transformControls.addEventListener("dragging-changed", (event) => {
        controls.enabled = !event.value;
      });
      transformControls.addEventListener("objectChange", () => {
        this.syncSelectedFromObject();
      });
      scene.add(transformControls);

      this._three = {
        renderer,
        scene,
        camera,
        controls,
        rafId: null,
        modelGroup,
        markerGroup,
        deviceModelGroup,
        labelGroup,
        transformControls,
        raycaster: new THREE.Raycaster(),
        mouse: new THREE.Vector2(),
        modelCache: new Map(),
      };

      this.animate();
    },

    animate() {
      const t = this._three;
      if (!t || !t.renderer) return;
      t.rafId = requestAnimationFrame(this.animate);
      if (t.controls) t.controls.update();
      t.renderer.render(t.scene, t.camera);
    },

    disposeThree() {
      const t = this._three;
      try {
        if (t.rafId) cancelAnimationFrame(t.rafId);
      } catch (e) {}
      if (t.renderer) {
        try {
          t.renderer.dispose();
        } catch (e) {}
      }
      this._three = {
        renderer: null,
        scene: null,
        camera: null,
        controls: null,
        rafId: null,
        modelGroup: null,
        markerGroup: null,
        deviceModelGroup: null,
        raycaster: null,
        mouse: null,
        modelCache: null,
      };
    },

    clearScene() {
      const t = this._three;
      if (!t || !t.modelGroup || !t.markerGroup || !t.deviceModelGroup || !t.labelGroup) return;
      t.modelGroup.clear();
      t.markerGroup.clear();
      t.deviceModelGroup.clear();
      t.labelGroup.clear();
      if (t.transformControls) t.transformControls.detach();
      this.selectedDevice = null;
    },

    async renderViewer() {
      this.threeError = "";
      this.clearScene();

      if (!this.viewer || !this.viewer.factoryModel || !this.viewer.factoryModel.modelUrl) {
        return;
      }

      this.threeLoading = true;
      try {
        await this.loadFactoryModel(this.viewer.factoryModel);
        this.addDeviceMarkers(this.viewerDevices);
        if (this.useDeviceModels) {
          await this.addDeviceModels(this.viewerDevices);
        }
        if (this.showStatusLabels) {
          this.addStatusLabels(this.viewerDevices);
        }
      } catch (e) {
        this.threeError = (e && e.message) ? e.message : String(e);
      } finally {
        this.threeLoading = false;
      }
    },

    mapStatusLabel(status) {
      const s = status == null ? "" : String(status);
      if (s === "0" || s === "running" || s.toLowerCase() === "normal") return "正常使用";
      if (s === "1" || s === "maintenance" || s === "repair" || s === "stopped") return "检修";
      return s || "-";
    },

    createLabelSprite(text, colorHex) {
      const canvas = document.createElement("canvas");
      canvas.width = 256;
      canvas.height = 64;
      const ctx = canvas.getContext("2d");
      const bg = colorHex || "#4caf50";

      // 背景
      ctx.fillStyle = "rgba(0,0,0,0.55)";
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      // 左侧色条
      ctx.fillStyle = bg;
      ctx.fillRect(0, 0, 10, canvas.height);
      // 文本
      ctx.fillStyle = "#fff";
      ctx.font = "bold 28px sans-serif";
      ctx.textBaseline = "middle";
      ctx.fillText(String(text), 18, canvas.height / 2);

      const texture = new THREE.CanvasTexture(canvas);
      texture.needsUpdate = true;
      const material = new THREE.SpriteMaterial({ map: texture, transparent: true, depthTest: false });
      const sprite = new THREE.Sprite(material);
      sprite.scale.set(3.6, 0.9, 1);
      return sprite;
    },

    addStatusLabels(devices) {
      const t = this._three;
      if (!t || !t.labelGroup) return;
      // 只给已绑定的设备加标签（避免堆满视野）
      const bound = devices.filter((d) => d && d.bindingId);
      bound.forEach((d) => {
        const label = this.createLabelSprite(this.mapStatusLabel(d.status), d.statusColor);
        label.position.set(
          Number(d.positionX || 0),
          Number(d.positionY || 0) + 1.2,
          Number(d.positionZ || 0)
        );
        label.userData = d;
        t.labelGroup.add(label);
      });
    },

    getModelExt(factoryModel) {
      const format = (factoryModel && factoryModel.modelFormat) ? String(factoryModel.modelFormat).toLowerCase() : "";
      if (format) return format;
      const url = (factoryModel && factoryModel.modelUrl) ? factoryModel.modelUrl : "";
      const m = String(url).split("?")[0].match(/\.([a-zA-Z0-9]+)$/);
      return m ? m[1].toLowerCase() : "";
    },

    loadFactoryModel(factoryModel) {
      const t = this._three;
      if (!t || !t.scene) return Promise.reject(new Error("three.js 未初始化"));

      const ext = this.getModelExt(factoryModel);
      const url = this.resolveUrl(factoryModel.modelUrl);

      return new Promise((resolve, reject) => {
        const onLoaded = (obj) => {
          t.modelGroup.add(obj);
          this.fitCameraToObject(obj);
          resolve();
        };

        const onError = (err) => {
          reject(new Error("模型加载失败：" + (err && err.message ? err.message : "请检查文件地址/格式")));
        };

        if (ext === "glb" || ext === "gltf") {
          const loader = new GLTFLoader();
          loader.load(
            url,
            (gltf) => {
              onLoaded(gltf.scene || gltf.scenes[0]);
            },
            undefined,
            onError
          );
          return;
        }

        if (ext === "fbx") {
          const loader = new FBXLoader();
          loader.load(
            url,
            (obj) => {
              onLoaded(obj);
            },
            undefined,
            onError
          );
          return;
        }

        if (ext === "obj") {
          const loader = new OBJLoader();
          loader.load(
            url,
            (obj) => {
              // 给 obj 一个默认材质，避免黑屏
              obj.traverse((child) => {
                if (child.isMesh) {
                  child.material = new THREE.MeshStandardMaterial({ color: 0xb0bec5 });
                }
              });
              onLoaded(obj);
            },
            undefined,
            onError
          );
          return;
        }

        reject(new Error("不支持的模型格式：" + (ext || "未知") + "（支持 glb/gltf/fbx/obj）"));
      });
    },

    addDeviceMarkers(devices) {
      const t = this._three;
      if (!t || !t.markerGroup) return;
      if (!devices || !devices.length) return;

      // 根据模型尺寸动态决定点位球半径（更稳）
      const bbox = new THREE.Box3().setFromObject(t.modelGroup);
      const size = new THREE.Vector3();
      const center = new THREE.Vector3();
      bbox.getSize(size);
      bbox.getCenter(center);
      const radius = Math.max(size.length() / 200, 0.1);

      const bound = devices.filter((d) => d && d.bindingId);
      const unbound = devices.filter((d) => d && !d.bindingId);

      // 1) 已绑定：按绑定坐标渲染
      bound.forEach((d) => {
        const color = (d && d.statusColor) ? d.statusColor : "#9e9e9e";
        const geometry = new THREE.SphereGeometry(radius, 16, 16);
        const material = new THREE.MeshStandardMaterial({ color: new THREE.Color(color) });
        const mesh = new THREE.Mesh(geometry, material);
        mesh.position.set(
          Number(d.positionX || 0),
          Number(d.positionY || 0),
          Number(d.positionZ || 0)
        );
        // 轻微抬高，避免与地面/模型面片重合导致不易看到
        mesh.position.y += radius * 1.2;
        mesh.userData = d;
        t.markerGroup.add(mesh);
      });

      // 2) 未绑定：可选渲染（默认关闭），使用环形排布避免堆叠在 0,0,0
      if (this.showUnboundMarkers && unbound.length) {
        const ringR = Math.max(size.length() / 6, radius * 6, 1);
        unbound.forEach((d, idx) => {
          const color = (d && d.statusColor) ? d.statusColor : "#9e9e9e";
          const geometry = new THREE.SphereGeometry(radius * 0.8, 16, 16);
          const material = new THREE.MeshStandardMaterial({ color: new THREE.Color(color) });
          const mesh = new THREE.Mesh(geometry, material);
          const a = (idx / unbound.length) * Math.PI * 2;
          mesh.position.set(
            center.x + Math.cos(a) * ringR,
            center.y + radius * 1.2,
            center.z + Math.sin(a) * ringR
          );
          mesh.userData = d;
          t.markerGroup.add(mesh);
        });
      }
    },

    normalizeRotation(v) {
      const n = Number(v || 0);
      // 经验规则：若绝对值明显大于 2π，认为录入的是“度”
      if (Math.abs(n) > Math.PI * 2 + 0.0001) {
        return (n * Math.PI) / 180;
      }
      return n;
    },

    tintObject(obj, colorHex) {
      const c = new THREE.Color(colorHex || "#9e9e9e");
      obj.traverse((child) => {
        if (child && child.isMesh && child.material) {
          if (Array.isArray(child.material)) {
            child.material.forEach((m) => {
              if (m && m.color) m.color = c.clone();
            });
          } else {
            if (child.material.color) child.material.color = c.clone();
          }
        }
      });
    },

    async getOrLoadDeviceModel(url, ext) {
      const t = this._three;
      if (!t || !t.modelCache) throw new Error("three.js 未初始化");
      const key = `${ext}::${url}`;
      if (t.modelCache.has(key)) {
        return await t.modelCache.get(key);
      }

      const p = new Promise((resolve, reject) => {
        const onError = (err) => {
          reject(new Error("设备模型加载失败：" + (err && err.message ? err.message : url)));
        };

        if (ext === "glb" || ext === "gltf") {
          const loader = new GLTFLoader();
          loader.load(
            url,
            (gltf) => resolve(gltf.scene || gltf.scenes[0]),
            undefined,
            onError
          );
          return;
        }

        if (ext === "fbx") {
          const loader = new FBXLoader();
          loader.load(url, (obj) => resolve(obj), undefined, onError);
          return;
        }

        if (ext === "obj") {
          const loader = new OBJLoader();
          loader.load(
            url,
            (obj) => {
              obj.traverse((child) => {
                if (child.isMesh && !child.material) {
                  child.material = new THREE.MeshStandardMaterial({ color: 0xb0bec5 });
                }
              });
              resolve(obj);
            },
            undefined,
            onError
          );
          return;
        }

        reject(new Error("不支持的设备模型格式：" + (ext || "未知")));
      });

      t.modelCache.set(key, p);
      return await p;
    },

    async addDeviceModels(devices) {
      const t = this._three;
      if (!t || !t.deviceModelGroup) return;
      if (!devices || !devices.length) return;

      const bound = devices.filter((d) => d && d.bindingId && d.deviceModelUrl);
      if (!bound.length) return;

      const tasks = bound.map(async (d) => {
        const url = this.resolveUrl(d.deviceModelUrl);
        const ext = (() => {
          const fmt = d.deviceModelFormat ? String(d.deviceModelFormat).toLowerCase() : "";
          if (fmt) return fmt;
          const m = String(d.deviceModelUrl || "").split("?")[0].match(/\.([a-zA-Z0-9]+)$/);
          return m ? m[1].toLowerCase() : "";
        })();

        const baseObj = await this.getOrLoadDeviceModel(url, ext);
        // 对于带骨骼的模型（常见于 fbx），用 SkeletonUtils.clone 更稳；否则退回 Object3D.clone
        const instance = typeof skeletonClone === "function" ? skeletonClone(baseObj) : baseObj.clone(true);

        instance.position.set(
          Number(d.positionX || 0),
          Number(d.positionY || 0),
          Number(d.positionZ || 0)
        );
        instance.rotation.set(
          this.normalizeRotation(d.rotationX),
          this.normalizeRotation(d.rotationY),
          this.normalizeRotation(d.rotationZ)
        );
        instance.scale.set(
          Number(d.scaleX || 1),
          Number(d.scaleY || 1),
          Number(d.scaleZ || 1)
        );

        this.tintObject(instance, d.statusColor);
        instance.userData = d;
        t.deviceModelGroup.add(instance);

        // 编辑模式下：如果当前选中的是该设备，重新 attach
        if (this.editMode && this.selectedDevice && this.selectedDevice.deviceNumber === d.deviceNumber && t.transformControls) {
          t.transformControls.attach(instance);
        }
      });

      await Promise.all(tasks);
    },

    fitCameraToObject(obj) {
      const t = this._three;
      if (!t || !t.camera || !t.controls) return;

      const box = new THREE.Box3().setFromObject(obj);
      const size = new THREE.Vector3();
      const center = new THREE.Vector3();
      box.getSize(size);
      box.getCenter(center);

      const maxDim = Math.max(size.x, size.y, size.z);
      const fov = t.camera.fov * (Math.PI / 180);
      let cameraZ = Math.abs(maxDim / 2 / Math.tan(fov / 2));
      cameraZ *= 1.5;

      t.camera.position.set(center.x + cameraZ, center.y + cameraZ, center.z + cameraZ);
      t.camera.near = maxDim / 100;
      t.camera.far = maxDim * 100;
      t.camera.updateProjectionMatrix();

      t.controls.target.copy(center);
      t.controls.update();
    },

    onResize() {
      const t = this._three;
      const container = this.$refs.threeContainer;
      if (!t || !t.renderer || !t.camera || !container) return;
      const width = container.clientWidth || 800;
      const height = container.clientHeight || 500;
      t.camera.aspect = width / height;
      t.camera.updateProjectionMatrix();
      t.renderer.setSize(width, height);
    },

    onClick(evt) {
      const t = this._three;
      const container = this.$refs.threeContainer;
      if (!t || !t.raycaster || !t.mouse || !t.camera || !t.markerGroup || !container) return;

      const rect = container.getBoundingClientRect();
      const x = ((evt.clientX - rect.left) / rect.width) * 2 - 1;
      const y = -((evt.clientY - rect.top) / rect.height) * 2 + 1;
      t.mouse.set(x, y);
      t.raycaster.setFromCamera(t.mouse, t.camera);
      const pickTargets = []
        .concat(t.markerGroup ? t.markerGroup.children || [] : [])
        .concat(t.deviceModelGroup ? t.deviceModelGroup.children || [] : []);
      const intersects = t.raycaster.intersectObjects(pickTargets, true);
      if (intersects && intersects.length) {
        const hit = intersects[0].object;
        let cur = hit;
        while (cur && cur.parent && (!cur.userData || !cur.userData.deviceNumber)) {
          cur = cur.parent;
        }
        const d = (cur && cur.userData) ? cur.userData : (hit.userData || {});
        if (this.editMode && d && d.bindingId) {
          this.selectDeviceInScene(d, cur);
          return;
        }
        this.$modal.msgInfo(`设备：${d.name || "-"}（${d.deviceNumber || "-"}），状态：${d.status || "-"}`);
      }
    },

    onEditModeChanged() {
      const t = this._three;
      if (!t || !t.transformControls) return;
      if (!this.editMode) {
        t.transformControls.detach();
        this.selectedDevice = null;
      } else {
        this.onTransformModeChanged();
      }
    },

    onTransformModeChanged() {
      const t = this._three;
      if (t && t.transformControls) {
        t.transformControls.setMode(this.transformMode);
      }
    },

    selectDeviceInScene(deviceVO, pickedObj) {
      const t = this._three;
      this.selectedDevice = deviceVO;
      if (!t || !t.transformControls) return;
      // 优先把 gizmo 挂在设备模型实例上；否则挂在点位球体上
      let target = null;
      if (pickedObj && pickedObj.userData && pickedObj.userData.deviceNumber) {
        target = pickedObj;
      }
      if (!target && t.deviceModelGroup) {
        target = (t.deviceModelGroup.children || []).find((x) => x.userData && x.userData.deviceNumber === deviceVO.deviceNumber);
      }
      if (!target && t.markerGroup) {
        target = (t.markerGroup.children || []).find((x) => x.userData && x.userData.deviceNumber === deviceVO.deviceNumber);
      }
      if (target) {
        t.transformControls.attach(target);
      }
    },

    syncSelectedFromObject() {
      const t = this._three;
      if (!this.selectedDevice || !t || !t.transformControls) return;
      const obj = t.transformControls.object;
      if (!obj) return;
      // 同步到内存（保存时使用）
      this.selectedDevice.positionX = obj.position.x;
      this.selectedDevice.positionY = obj.position.y;
      this.selectedDevice.positionZ = obj.position.z;
      this.selectedDevice.rotationX = obj.rotation.x;
      this.selectedDevice.rotationY = obj.rotation.y;
      this.selectedDevice.rotationZ = obj.rotation.z;
      this.selectedDevice.scaleX = obj.scale.x;
      this.selectedDevice.scaleY = obj.scale.y;
      this.selectedDevice.scaleZ = obj.scale.z;
    },

    async saveSelected() {
      if (!this.viewer || !this.viewer.factoryModel || !this.selectedDevice || !this.selectedDevice.bindingId) return;
      const t = this._three;
      if (!t || !t.transformControls || !t.transformControls.object) {
        this.$modal.msgWarning("请先在场景中选中一个已绑定设备");
        return;
      }
      this.saving = true;
      try {
        const obj = t.transformControls.object;
        const radToDeg = (r) => (Number(r || 0) * 180) / Math.PI;
        const payload = {
          id: this.selectedDevice.bindingId,
          factoryModelId: this.viewer.factoryModel.id,
          deviceNumber: this.selectedDevice.deviceNumber,
          deviceModelId: this.selectedDevice.deviceModelId,
          positionX: obj.position.x,
          positionY: obj.position.y,
          positionZ: obj.position.z,
          rotationX: radToDeg(obj.rotation.x),
          rotationY: radToDeg(obj.rotation.y),
          rotationZ: radToDeg(obj.rotation.z),
          scaleX: obj.scale.x,
          scaleY: obj.scale.y,
          scaleZ: obj.scale.z,
        };
        await updateBinding(payload);
        this.$modal.msgSuccess("保存成功");
        // 重新拉取 viewer，刷新列表/状态/渲染
        await this.handleQuery();
      } finally {
        this.saving = false;
      }
    },

    exportSceneJson() {
      if (!this.viewer || !this.viewer.factoryModel) return;
      const data = {
        factoryId: this.queryParams.factoryId,
        factoryModel: this.viewer.factoryModel,
        devices: this.viewerDevices,
        exportedAt: new Date().toISOString(),
      };
      const blob = new Blob([JSON.stringify(data, null, 2)], { type: "application/json;charset=utf-8" });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `scene_${this.queryParams.factoryId || "factory"}_${Date.now()}.json`;
      a.click();
      URL.revokeObjectURL(url);
    },
  },
};
</script>

<style scoped>
.three-viewer-wrap {
  position: relative;
  width: 100%;
  height: 520px;
  background: #f5f7fa;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}
.three-viewer-canvas {
  width: 100%;
  height: 100%;
}
.three-viewer-overlay {
  position: absolute;
  left: 12px;
  top: 12px;
  padding: 8px 10px;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
}
.three-viewer-error {
  background: rgba(245, 108, 108, 0.9);
}
.three-viewer-pre {
  margin-top: 6px;
  background: #f6f8fa;
  padding: 8px;
  border-radius: 4px;
  max-height: 160px;
  overflow: auto;
}
</style>


