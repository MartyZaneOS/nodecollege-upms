<template>
  <a-row style="padding: 10px;">
    <a-col span="16" style="text-align: center;">
      <a-avatar :size="120" :src="avatar"></a-avatar>
      <a @click="uploadA">更换</a>
    </a-col>
    <a-col span="16" style="margin: 10px 0">
      <a-form :form="modal.form" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
        <a-form-item label="昵称">
          <a-input placeholder="请输入昵称！" v-model="userInfo.nickname"/>
        </a-form-item>
        <a-form-item label="账号">
          <a-input placeholder="请输入账号！" v-model="userInfo.account"/>
        </a-form-item>
        <a-form-item label="电话">
          <a-input placeholder="请输入电话！" v-model="userInfo.telephone"/>
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input placeholder="请输入邮箱！" v-model="userInfo.email"/>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="16" style="margin: 10px 0; text-align:center;">
      <a-button type="primary" @click="updateUserInfo">确认修改</a-button>
    </a-col>
    <a-modal v-model="uploadModal.visible" title="修改头像">
      <vue-cropper
          ref="cropper"
          :aspect-ratio="16 / 16"
          :src="uploadModal.imgSrc"
          preview=".preview"
      />
      <a-upload
          name="file"
          :fileList="uploadModal.fileList"
          :remove="handleRemove"
          :beforeUpload="beforeUpload"
          class="upload-list-inline"
      >
        <a-button
            type="primary"
            :disabled="uploadModal.fileList.length !== 0"
            :loading="uploadModal.uploading"
            style="margin-top: 16px;width: 100px"
        >选择图片
        </a-button>
      </a-upload>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="modal.loading" @click="uploadAvatar">
          确定
        </a-button>
        <a-button key="back" @click="uploadModal.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import * as operateApi from '../api/operate'
  import VueCropper from 'vue-cropperjs'
  import 'cropperjs/dist/cropper.css'
  import {USER_INFO} from 'common'

  export default {
    name: 'MyInfo',
    components: {VueCropper},
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1, pageSizeOptions: ['5', '10', '20', '50', '100']},
        rolePageObj: {total: 10, pageSize: 10, pageNum: 1, pageSizeOptions: ['5', '10', '20', '50', '100']},
        loading: false,
        pagination: false,
        userInfo: {},
        avatar: '',
        modal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          form: this.$form.createForm(this)
        },
        uploadModal: {
          visible: false,
          imgSrc: '',
          uploading: false,
          fileList: []
        }
      }
    },
    mounted () {
      this.searchUserInfo()
    },
    methods: {
      searchUserInfo () {
        operateApi.getUserInfo({}).then((res) => {
          if (res.success) {
            this.userInfo = res.rows[0]
            this.$store.state.userInfo = this.userInfo
            if (this.userInfo.avatar) {
              this.uploadModal.imgSrc = this.userInfo.avatar
              this.avatar = this.userInfo.avatarThumb
            }
          }
        })
      },
      // 更新用户信息
      updateUserInfo () {
        operateApi.updateUserInfo({
          ...this.userInfo
        }).then((res) => {
          if (res.success) {
            sessionStorage.setItem(USER_INFO, JSON.stringify(res.rows[0]))
            this.searchUserInfo()
          }
        })
      },
      uploadA () {
        this.uploadModal.visible = true
        this.uploadModal.fileList = []
        if (this.$refs.cropper) {
          this.$refs.cropper.replace(this.userInfo.avatar)
        }
      },
      handleRemove (file) {
        const index = this.uploadModal.fileList.indexOf(file)
        const newFileList = this.uploadModal.fileList.slice()
        newFileList.splice(index, 1)
        this.uploadModal.fileList = newFileList
      },
      beforeUpload (file) {
        this.uploadModal.fileList = [file]
        var reader = new FileReader()
        reader.readAsDataURL(file)
        let that = this
        reader.onload = function () {
          // 获取到base64格式图片
          that.$refs.cropper.replace(reader.result)
        }
        return false
      },
      // 更换头像
      uploadAvatar () {
        let avatar = this.$refs.cropper.getCroppedCanvas().toDataURL()
        if (!avatar) {
          this.$message.error('请选择文件')
          return
        }
        // 获取到base64格式图片
        operateApi.uploadAvatar({
          avatar: avatar
        }).then((res) => {
          if (res.success) {
            this.userInfo = JSON.parse(sessionStorage.getItem(USER_INFO))
            this.userInfo.avatar = res.rows[0].avatar
            this.userInfo.avatarThumb = res.rows[0].avatarThumb
            if (this.userInfo.avatar) {
              this.uploadModal.imgSrc = this.userInfo.avatar
              this.avatar = this.userInfo.avatarThumb
            }
            sessionStorage.setItem(USER_INFO, JSON.stringify(this.userInfo))
            this.$store.state.userInfo = this.userInfo
            this.uploadModal.visible = false
          }
        })
      }
    }
  }
</script>

<style scoped>
  .upload-list-inline >>> .ant-upload-list-item {
    width: 200px;
    height: 30px;
    margin: 0 auto;
  }

  .upload-list-inline >>> .ant-upload-animate-enter {
    animation-name: uploadAnimateInlineIn;
  }

  .upload-list-inline >>> .ant-upload-animate-leave {
    animation-name: uploadAnimateInlineOut;
  }
</style>
