<template>

  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
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
          :columns="columns"
          :row-key="record => record.id"
          :data-source="level1"
          :loading="loading"
          :pagination="false"
      >
        <template #cover="{ text: cover}">
          <img v-if="cover" :src="cover" alt="avatar"/>
        </template>
        <template v-slot:action="{ text, record}">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>

            <a-popconfirm
                title="删除后不可恢复，确认删除？"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>

          </a-space>
        </template>


      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal
    title="文档表单"
    v-model:visible="modalVisible"
    :confirm-loading="modalLoading"
    @ok="handleModalOk">
      <a-form :model="doc" :label-col="{ span: 6}"  :wrapper-col="{ span: 18 }">
        <a-form-item label="名称">
          <a-input v-model:value="doc.name" />
        </a-form-item>
        <a-form-item label="父文档">
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

        <a-form-item label="顺序">
          <a-input v-model:value="doc.sort" />
        </a-form-item>
        <a-form-item label="内容">
          <div id="content">
          </div>
        </a-form-item>
      </a-form>
  </a-modal>
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
      },
      {
        title: '父文档',
        key: 'parent',
        dataIndex: 'parent',
      },
      {
        title: '顺序',
        key: 'sort',
        dataIndex: 'sort',
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'},
      },
    ];

    const level1 = ref();

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
    const doc = ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const editor = new E("#content");

    const handleModalOk = () => {
      modalLoading.value = true;

      axios.post("/doc/save", doc.value).then((response) => {
        const data = response.data;
        modalLoading.value = false;
        if(data.success) {
          modalVisible.value = false;

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
    * 编辑
    * */
    const edit = (record: any) => {

      modalVisible.value = true;

      doc.value = Tool.copy(record);

      treeSelectData.value = Tool.copy(level1.value);
      setDisabled(treeSelectData.value, record.id);

      //添加一个无 unshift往数组前添加元素
      treeSelectData.value.unshift({id: 0, name: '无'});
      setTimeout(function () {
        editor.create();
      },100);
    };

    /*
    * 新增
    * */
    const add = () => {

      modalVisible.value = true;

      doc.value = {
        ebookId: route.query.ebookId
      };

      treeSelectData.value = Tool.copy(level1.value);

      //添加一个无
      treeSelectData.value.unshift({id: 0, name: '无'});
      setTimeout(function () {
        editor.create();
      },100);

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
      handleModalOk,
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