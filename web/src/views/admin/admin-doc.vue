<template>

  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >

      <a-row :gutter="24">
        <a-col :span="8">
          <p>
          <a-form layout="inline" :model="param">
            <a-form-item>
              <a-button type="primary" @click="handleQuery()">
                查询
              </a-button>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="add()">
                新增
              </a-button>
            </a-form-item>
          </a-form>
        </p>
          <a-table
              v-if="level1.length > 0"
              :columns="columns"
              :row-key="record => record.id"
              :data-source="level1"
              :loading="loading"
              :pagination="false"
              size="small"
              :defaultExpandAllRows="true"
          >
            <template #name="{ text, record}">
              {{record.sort}} {{text}}
            </template>
            <template v-slot:action="{ text, record}">
              <a-space size="small">
                <a-button type="primary" @click="edit(record)" size="small">
                  编辑
                </a-button>

                <a-popconfirm
                    title="删除后不可恢复，确认删除？"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="handleDelete(record.id)"
                >
                  <a-button type="danger" size="small">
                    删除
                  </a-button>
                </a-popconfirm>

              </a-space>
            </template>


          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave()">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="doc" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name"  placeholder="名称"/>
            </a-form-item>
            <a-form-item>
              <a-tree-select
                  v-model:value="doc.parent"
                  style="width: 100%"
                  :dropdown-style="{maxHight: '400px', overflow: 'auto'}"
                  :tree-data="treeSelectData"
                  placeholder = "请选择父文档"
                  tree-default-expand-all
                  :replaceFields="{title: 'name', key: 'id', value: 'id'}"
              >
              </a-tree-select>
            </a-form-item>

            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="顺序"/>
            </a-form-item>
            <a-form-item>
              <div id="content">
              </div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

    </a-layout-content>
  </a-layout>

<!--  <a-modal-->
<!--    title="文档表单"-->
<!--    v-model:visible="modalVisible"-->
<!--    :confirm-loading="modalLoading"-->
<!--    @ok="handleSave">-->
<!--      -->
<!--  </a-modal>-->
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import { Tool } from "@/util/tool";
import {useRoute} from "vue-router";
import E from "wangeditor"


const listData: Record<string, string>[] = [];

for (let i = 0; i < 23; i++) {
  listData.push({
    href: 'https://www.antdv.com/',
    title: `ant design vue part ${i}`,
    avatar: 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png',
    description:
        'Ant Design, a design language for background applications, is refined by Ant UED Team.',
    content:
        'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
  });
}

export default defineComponent({
  name: 'AdminDoc',
  setup() {
    const route = useRoute();
    const param = ref();
    param.value = {};
    const docs = ref();

    const loading = ref(false);

    const columns = [
      {
        title: '名称',
        dataIndex: 'name',
        slots: {customRender: 'name'}
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'},
      },
    ];

    const level1 = ref();
    level1.value = [];
    /*
    * 数据查询
    * */
    const handleQuery = () => {
      loading.value = true;
      level1.value = [];
      axios.get("/doc/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if(data.success) {
          docs.value = data.content;
          console.log("原始数据", docs.value);

          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
          console.log("树型结构", level1);

        }else {
          message.error(data.message);
        }

      });
    };



    /*
    * 表单
    * */

    //因为树选择组件的属性状态会变化，所以用响应式变量
    const treeSelectData = ref();
    treeSelectData.value = [];
    const doc = ref();
    doc.value = [];
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const editor = new E("#content");
    editor.config.zIndex = 0;

    const handleSave = () => {
      modalLoading.value = true;
      doc.value.content = editor.txt.html();
      axios.post("/doc/save", doc.value).then((response) => {
        const data = response.data;
        modalLoading.value = false;
        if(data.success) {

          message.success("保存成功！")

          //重新加载列表
          handleQuery();
        }else {
          message.error(data.message);
        }
      })
    }

    /*
    * 将某节点及其子孙节点全部部署为disabled
    * */
    const setDisabled = (treeSelectData: any, id: any) => {
      for(let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if(node.id === id) {
          console.log("disable", node);
          node.disabled = true;

          const children = node.children;
          if(Tool.isNotEmpty(children)) {
            for(let j = 0; j < children.length; j++) {
              setDisabled(children, children[j].id)
            }
          }
        }
      }
    }

    const ids: Array<string> = [];

    const getDeleteIds = (treeSelectData: any, id: any) => {
      for(let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if(node.id === id) {
          console.log("disable", node);
          ids.push(id);
          const children = node.children;
          if(Tool.isNotEmpty(children)) {
            for(let j = 0; j < children.length; j++) {
              getDeleteIds(children, children[j].id)
            }
          }
        }
      }
    }


    /*
   * 内容查询
   * */
    const handleQueryContent = () => {
      axios.get("/doc/find-content/" + doc.value.id).then((response) => {
        loading.value = false;
        const data = response.data;
        if(data.success) {
          editor.txt.html(data.content);

        }else {
          message.error(data.message);
        }

      });
    };
    /*
    * 编辑
    * */
    const edit = (record: any) => {
      editor.txt.html("");
      modalVisible.value = true;

      doc.value = Tool.copy(record);
      handleQueryContent();
      treeSelectData.value = Tool.copy(level1.value);
      setDisabled(treeSelectData.value, record.id);

      //添加一个无 unshift往数组前添加元素
      treeSelectData.value.unshift({id: 0, name: '无'});

    };

    /*
    * 新增
    * */
    const add = () => {
      editor.txt.html("");

      modalVisible.value = true;

      doc.value = {
        ebookId: route.query.ebookId
      };

      treeSelectData.value = Tool.copy(level1.value);

      //添加一个无
      treeSelectData.value.unshift({id: 0, name: '无'});

    }

    /*
    * 删除
    * */
    const handleDelete = (id: number) => {
      ids.length = 0;
      getDeleteIds(level1.value, id);
      modalLoading.value = true;

      axios.delete("/doc/delete/" + ids.join(",")).then((response) => {
        const data = response.data;
        if(data.success) {
          modalVisible.value = false;
          modalLoading.value = false;

          //重新加载列表
          handleQuery();
        }
      })
    };

    onMounted(() => {
      handleQuery();
      editor.create();
    })

    return {
      // docs,
      treeSelectData,
      level1,
      columns,
      loading,
      param,
      handleQuery,

      edit,
      add,

      modalVisible,
      modalLoading,
      handleSave,
      handleDelete,

      doc
    }
  }
});
</script>


<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>