const renderVnode = {
  name: "RenderVnode",
  props: {
    vNode: {
      type: [String, Object], // 支持 String 或 VNode 对象
      required: true,
    },
  },
  render(h) {
    // 如果是字符串，渲染为文本节点；否则直接渲染 VNode
    return typeof this.vNode === "string" ? h("span", this.vNode) : this.vNode;
  },
};

export default renderVnode;
