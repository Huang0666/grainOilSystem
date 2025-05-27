<template>
  <div class="login-bg">
    <div class="login-container">
      <div class="login-card-custom">
        <div class="login-logo">Logo</div>
        <h2 class="login-title">粮油系统</h2>
        <div class="login-subtitle">{{ loginType === 'user' ? 'User Login' : 'Admin Login' }}</div>
        <a-radio-group v-model:value="loginType" button-style="solid" class="login-type-switch">
          <a-radio-button value="user">用户登录</a-radio-button>
          <a-radio-button value="admin">管理员登录</a-radio-button>
        </a-radio-group>
        <a-form :model="form" ref="formRef" class="login-form" @submit.prevent="onLogin">
          <a-form-item>
            <a-input v-model:value="form.username" size="large" placeholder="Email / 用户名" />
          </a-form-item>
          <a-form-item>
            <a-input-password v-model:value="form.password" size="large" placeholder="Password" />
          </a-form-item>
          <div class="login-remember">
            <a-checkbox v-model:checked="form.remember">记住密码</a-checkbox>
          </div>
          <a-form-item class="login-btn-row">
            <a-button type="primary" html-type="submit" class="login-btn" @click="onLogin">登录</a-button>
          </a-form-item>
        </a-form>
        <div class="login-footer">
          <a @click="showRegister = true" class="login-link">注册</a>
        </div>
      </div>
      <a-modal v-model:open="showRegister" title="注册" :footer="null" width="400px">
        <a-form :model="registerForm" ref="registerFormRef">
          <a-form-item label="用户名">
            <a-input v-model:value="registerForm.username" />
          </a-form-item>
          <a-form-item label="密码">
            <a-input-password v-model:value="registerForm.password" />
          </a-form-item>
          <a-form-item label="手机号">
            <a-input v-model:value="registerForm.phone" />
          </a-form-item>
          <a-form-item style="text-align:right;">
            <a-button @click="showRegister = false" style="margin-right: 8px">取消</a-button>
            <a-button type="primary" @click="onRegister">注册</a-button>
          </a-form-item>
        </a-form>
      </a-modal>
      <div class="login-bg-shape shape1"></div>
      <div class="login-bg-shape shape2"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userLogin, adminLogin, userRegister } from '../api/auth'
import { message } from 'ant-design-vue'

const router = useRouter()
const loginType = ref('user')
const form = ref({ username: '', password: '', remember: false })
const formRef = ref()
const showRegister = ref(false)
const registerForm = ref({ username: '', password: '', phone: '' })
const registerFormRef = ref()

// 记住密码自动填充
onMounted(() => {
  const saved = localStorage.getItem('grain_oil_login')
  if (saved) {
    const { username, password, remember } = JSON.parse(saved)
    form.value.username = username
    form.value.password = password
    form.value.remember = remember
  }
})

async function onLogin() {
  if (!form.value.username || !form.value.password) {
    message.warning('请输入用户名和密码')
    return
  }
  try {
    const loginFn = loginType.value === 'admin' ? adminLogin : userLogin
    const response = await loginFn({
      username: form.value.username,
      password: form.value.password
    })
    const { token, user } = response.data.data
      // 登录成功
    localStorage.setItem('grain_oil_token', token)
    localStorage.setItem('grain_oil_user', JSON.stringify(user))
      if (form.value.remember) {
        localStorage.setItem('grain_oil_login', JSON.stringify({
          username: form.value.username,
          password: form.value.password,
          remember: true
        }))
      } else {
        localStorage.removeItem('grain_oil_login')
      }
      message.success('登录成功')
      if (loginType.value === 'admin') {
        router.push('/admin')
      } else {
        router.push('/user')
    }
  } catch (e) {
    // 错误已在axios拦截器中处理
    console.error(e)
  }
}

async function onRegister() {
  if (!registerForm.value.username || !registerForm.value.password) {
    message.warning('请输入用户名和密码')
    return
  }
  try {
    await userRegister(registerForm.value)
      message.success('注册成功，请登录')
      form.value.username = registerForm.value.username
      form.value.password = ''
      showRegister.value = false
  } catch (e) {
    // 错误已在axios拦截器中处理
    console.error(e)
  }
}
</script>

<style scoped>
.login-bg {
  min-height: 100vh;
  min-width: 100vw;
  height: 100vh;
  width: 100vw;
  background: linear-gradient(120deg, #c7c5f4 0%, #776bcc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: fixed;
  top: 0;
  left: 0;
  overflow: hidden;
}
.login-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  width: 100vw;
}
.login-card-custom {
  position: relative;
  z-index: 2;
  width: 380px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 32px rgba(60, 60, 120, 0.15);
  padding: 48px 36px 32px 36px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.login-logo {
  width: 56px;
  height: 56px;
  background: #6c63ac;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 20px;
  margin-bottom: 16px;
}
.login-title {
  font-size: 2rem;
  font-weight: 700;
  color: #5d54a4;
  margin-bottom: 4px;
}
.login-subtitle {
  color: #7c78b8;
  margin-bottom: 18px;
  font-size: 1rem;
}
.login-type-switch {
  margin-bottom: 18px;
}
.login-form {
  width: 100%;
}
.login-btn-row {
  width: 100%;
  display: flex;
  justify-content: center;
}
.login-btn {
  width: 100%;
  background: linear-gradient(90deg, #5d54a4 0%, #6c63ac 100%);
  border: none;
  color: #fff;
  font-weight: 700;
  border-radius: 26px;
  box-shadow: 0px 2px 2px #5c5696;
  transition: 0.2s;
}
.login-btn:hover {
  filter: brightness(1.1);
}
.login-remember {
  margin-bottom: 8px;
}
.login-footer {
  width: 100%;
  text-align: right;
  margin-top: 8px;
}
.login-link {
  color: #5d54a4;
  cursor: pointer;
  font-size: 14px;
}
.login-link:hover {
  text-decoration: underline;
}
.login-bg-shape {
  position: absolute;
  z-index: 1;
  border-radius: 50%;
  opacity: 0.7;
}
.shape1 {
  width: 340px;
  height: 340px;
  background: linear-gradient(120deg, #5d54a4 0%, #6c63ac 100%);
  top: -80px;
  left: -120px;
}
.shape2 {
  width: 420px;
  height: 220px;
  background: linear-gradient(120deg, #7e7bb9 0%, #c7c5f4 100%);
  bottom: -60px;
  right: -140px;
}
@media (max-width: 600px) {
  .login-card-custom {
    width: 98vw;
    padding: 32px 8px 24px 8px;
  }
  .login-bg-shape.shape1 {
    width: 180px;
    height: 180px;
    top: -40px;
    left: -60px;
  }
  .login-bg-shape.shape2 {
    width: 200px;
    height: 100px;
    bottom: -30px;
    right: -70px;
  }
}
</style> 