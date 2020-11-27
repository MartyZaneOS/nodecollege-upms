<template>
  <div>
    <a-tree
        :checkable="checkable"
        @check="onCheck"
        @expand="onExpand"
        :checkStrictly="checkStrictly"
        :autoExpandParent="autoExpandParent"
        :expandedKeys="defaultExpandedKeys"
        v-model="treeSelectData2"
        @select="onSelect"
        :selectedKeys="selectedKeys"
        :treeData="treeAllData2"
    />
  </div>
</template>

<script>
  export default {
    name: 'NCTree',
    props: {
      treeAllData: {
        type: Array,
        default: () => []
      },
      treeSelectData: {
        type: Array,
        default: () => []
      },
      checkable: {
        type: Boolean,
        default: true
      },
      // 校验父子数据
      checkParent: {
        type: Boolean,
        default: true
      }
    },
    data () {
      return {
        visible: false,
        loading: false,
        treeAllData2: [],
        checkParent2: [],
        treeSelectData2: [],
        allResourceList: [],
        autoExpandParent: true,
        checkStrictly: true,
        defaultExpandedKeys: [],
        checkedKeys: [],
        selectedKeys: [],
        checkedKeysResult: [],
        defaultProps: {
          children: 'children',
          label: 'title'
        }
      }
    },
    mounted () {
      this.treeSelectData2 = []
      this.treeSelectData.forEach(item => this.treeSelectData2.push(item))
      this.treeAllData2 = []
      this.handleTree(this.treeAllData)
      this.treeAllData2 = this.treeAllData
      this.checkParent2 = this.checkParent
    },
    watch: {
      treeAllData () {
        this.treeAllData2 = []
        this.handleTree(this.treeAllData)
        this.treeAllData2 = this.treeAllData
      },
      treeSelectData () {
        this.treeSelectData2 = []
        this.treeSelectData.forEach(item => this.treeSelectData2.push(item))
      },
      checkParent () {
        this.checkParent2 = this.checkParent
      }
    },
    methods: {
      handleTree (treeList) {
        treeList.forEach(item => {
          this.defaultExpandedKeys.push(item.key)
          this.allResourceList.push(item)
          if (item.children) {
            this.handleTree(item.children)
          }
        })
      },
      onCheck (checkedKeys) {
        if (!this.checkParent2) {
          this.checkedKeysResult = checkedKeys.checked
          this.$emit('onCheck', checkedKeys.checked)
          return
        }
        if (checkedKeys.checked.length > this.checkedKeysResult.length) {
          this.checkedKeys = []
          // 获取所有上级节点
          checkedKeys.checked.forEach(item => this.getParentNode(this.allResourceList, item))
          // 获取选中的下级几点及所有的上级节点
          this.checkedKeys.push(...checkedKeys.checked)
          // 去重
          const checkedKeysResult = Array.from(new Set(this.checkedKeys))
          this.treeSelectData2 = checkedKeysResult
          this.checkedKeysResult = checkedKeysResult
        } else {
          let delItem = null
          this.checkedKeysResult.forEach(item => {
            if (checkedKeys.checked.indexOf(item) === -1) {
              delItem = item
            }
          })
          if (delItem != null) {
            // 查找改节点的所有下级节点
            let childrenList = this.getChildrenList(this.allResourceList, delItem)
            let endList = []
            checkedKeys.checked.forEach(item => {
              if (childrenList.indexOf(item) === -1) {
                endList.push(item)
              }
            })
            this.treeSelectData2 = endList
            this.checkedKeysResult = endList
          }
        }
        this.$emit('onCheck', this.checkedKeysResult)
      },
      // 获取父级节点
      getParentNode (dataList, parentId) {
        dataList.forEach(item => {
          let id = item.key
          if (parentId === id) {
            this.checkedKeys.push(item.key)
            if (item.parentId !== 0) {
              this.getParentNode(dataList, item.parentId)
            }
          }
        })
      },
      // 获取下级节点
      getChildrenList (dataList, parentId) {
        let childrens = []
        dataList.forEach(item => {
          let id = item.key
          if (parentId === item.parentId) {
            childrens.push(item.key)
            childrens.push(...this.getChildrenList(dataList, id))
          }
        })
        return childrens
      },
      onSelect (selectedKeys, info) {
        this.selectedKeys = selectedKeys
      },
      onExpand (expandedKeys) {
        const checkedKeysResult = Array.from(new Set(expandedKeys))
        this.defaultExpandedKeys = checkedKeysResult
        this.autoExpandParent = false
      }
    }
  }
</script>

<style scoped>

</style>
