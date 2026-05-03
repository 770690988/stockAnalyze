<template>
    <div class="login-wrap">
      <el-card class="login-card">
        <div class="login-title">📈 Stock Analyze</div>
        <el-form :model="form" :rules="rules" ref="formRef" @keyup.enter="handleLogin">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              placeholder="密码"
              prefix-icon="Lock"
              type="password"
              show-password
              size="large"
            />
          </el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { login } from '../api/auth.js'
  
  const router = useRouter()
  const formRef = ref()
  const loading = ref(false)
  
  const form = ref({
    username: '',
    password: ''
  })
  
  const rules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
  }
  
  const handleLogin = () => {
    formRef.value.validate(async valid => {
      if (!valid) return
      loading.value = true
      try {
        const res = await login(form.value)
        if (res.success) {
          localStorage.setItem('token', res.data.token)
          localStorage.setItem('username', res.data.username)
          ElMessage.success('登录成功')
          router.push('/')
        } else {
          ElMessage.error(res.message || '登录失败')
        }
      } catch (e) {
        ElMessage.error('网络异常，请稍后重试')
      } finally {
        loading.value = false
      }
    })
  }
  </script>
  
  <style scoped>
  .login-wrap {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f0f2f5;
  }
  
  .login-card {
    width: 400px;
    padding: 20px;
  }
  
  .login-title {
    font-size: 24px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 32px;
    color: #303133;
  }
  </style>