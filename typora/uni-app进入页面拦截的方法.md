在 UniApp 小程序中，因为没有像 VueRouter 这样的路由守卫来拦截页面访问，因此需要通过编程的方式来实现页面访问控制，比如检查登录认证的 token 是否存在。如果没有 token，则跳转到登录页面。

以下是实现方法：

------

### 方法一：统一封装跳转方法

通过封装跳转方法，在跳转前检查 token。

#### 1. **封装路由跳转方法**

在项目的 `utils` 文件夹中创建一个 `router.js` 文件，用于统一管理页面跳转逻辑。

```javascript
// utils/router.js
export function navigateTo(url) {
  const token = uni.getStorageSync('token'); // 从本地缓存中获取 token
  if (!token) {
    // 如果没有 token，跳转到登录页面
    uni.showToast({
      title: '请先登录',
      icon: 'none',
      duration: 1500
    });
    setTimeout(() => {
      uni.navigateTo({
        url: '/pages/login/login' // 跳转到登录页面
      });
    }, 1500);
    return;
  }

  // 有 token 正常跳转
  uni.navigateTo({
    url
  });
}
```

#### 2. **在需要跳转的地方使用封装的方法**

在页面中调用 `navigateTo` 方法，而不是直接使用 `uni.navigateTo`，这样就可以在跳转前校验 token。

```javascript
import { navigateTo } from '@/utils/router.js';

export default {
  methods: {
    goToProtectedPage() {
      navigateTo('/pages/protected/protected'); // 跳转受保护页面
    }
  }
};
```

------

### 方法二：在每个页面的 `onLoad` 中校验 Token

在需要受保护的页面中添加 token 校验逻辑，如果没有 token，则跳转到登录页面。

#### 1. **在页面的 `onLoad` 中检查 token**

```javascript
export default {
  onLoad() {
    const token = uni.getStorageSync('token'); // 获取 token
    if (!token) {
      // 没有 token，跳转到登录页面
      uni.showToast({
        title: '请先登录',
        icon: 'none',
        duration: 1500
      });
      setTimeout(() => {
        uni.redirectTo({
          url: '/pages/login/login' // 跳转到登录页面
        });
      }, 1500);
    }
  }
};
```

#### 2. **在需要校验的页面中加入上述逻辑**

对于每个需要校验 token 的页面，都在 `onLoad` 或 `onShow` 中添加上述逻辑。

------

### 方法三：全局拦截页面跳转请求

利用 `uni-app` 提供的生命周期方法 `onLaunch` 和 `onShow`，在全局拦截页面访问。

#### 1. **修改 `App.vue` 中的生命周期方法**

在 `App.vue` 中，监听小程序的 `onLaunch` 和 `onShow` 生命周期，检查用户是否登录。

```javascript
// App.vue
export default {
  onLaunch() {
    console.log('App Launch');
  },
  onShow() {
    const pages = getCurrentPages(); // 获取页面栈
    const currentPage = pages[pages.length - 1]; // 获取当前页面
    const token = uni.getStorageSync('token'); // 检查 token

    const protectedPages = ['/pages/protected/protected']; // 需要登录的页面
    if (protectedPages.includes(`/${currentPage.route}`) && !token) {
      // 如果当前页面是受保护页面且没有 token
      uni.showToast({
        title: '请先登录',
        icon: 'none',
        duration: 1500
      });
      setTimeout(() => {
        uni.redirectTo({
          url: '/pages/login/login'
        });
      }, 1500);
    }
  }
};
```

#### 2. **受保护页面列表**

在 `protectedPages` 中列出所有需要 token 的页面路径，这样可以集中管理受保护页面。

------

### 方法四：利用页面拦截 H5 跳转请求

如果项目是 H5 端，可以利用拦截器拦截跳转操作。比如重写 `uni.navigateTo` 方法。

#### 1. **重写 `uni.navigateTo`**

在项目的入口文件中（比如 `main.js`）重写 `uni.navigateTo` 方法。

```javascript
// main.js
const originalNavigateTo = uni.navigateTo;

uni.navigateTo = function(options) {
  const token = uni.getStorageSync('token');
  if (!token && options.url.startsWith('/pages/protected')) {
    uni.showToast({
      title: '请先登录',
      icon: 'none',
      duration: 1500
    });
    setTimeout(() => {
      uni.navigateTo({
        url: '/pages/login/login'
      });
    }, 1500);
    return;
  }
  // 如果有 token，正常跳转
  originalNavigateTo.call(this, options);
};
```

------

### 方法五：后端校验 Token

除了在前端校验 token，还需要后端对接口请求做权限验证。

1. **前端携带 token** 每次请求接口时，在请求头中携带 token。

   ```javascript
   // 使用封装的请求方法
   uni.request({
     url: 'https://api.example.com/protected-data',
     header: {
       Authorization: `Bearer ${uni.getStorageSync('token')}` // 携带 token
     },
     success(res) {
       if (res.statusCode === 401) {
         uni.showToast({
           title: '登录已过期，请重新登录',
           icon: 'none'
         });
         setTimeout(() => {
           uni.navigateTo({
             url: '/pages/login/login'
           });
         }, 1500);
       }
     }
   });
   ```

2. **后端验证 token**

   - 如果 token 无效或过期，返回 `401 Unauthorized` 状态码。
   - 前端根据状态码处理逻辑。

------

### 总结

- 如果只需要简单的前端跳转拦截，可以封装一个 `navigateTo` 方法。
- 如果需要对某些页面统一校验登录状态，可以在 `onLoad` 或 `onShow` 生命周期中添加校验逻辑。
- 如果希望全局拦截访问，可以在 `App.vue` 或重写 `uni.navigateTo` 实现。
- 后端 token 校验是必要的，确保接口调用安全。

以上方法可以灵活组合，根据项目需求选择合适的实现方式！