# 粮油交易系统后台管理系统文档

## 1. 用户管理模块
页面：用户管理
功能：
1. 用户列表 `POST /api/admin/user/list`
```json
请求参数：
{
    "page": 1,
    "size": 10,
    "username": "可选",
    "role": "可选"
}
返回参数：
{
    "code": 200,
    "msg": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "username": "admin",
                "role": "admin",
                "phone": "13800138000"
            }
        ],
        "total": 100
    }
}
```

2. 新增用户 `POST /api/admin/user/add`
```json
请求参数：
{
    "username": "必填",
    "password": "必填,至少6位",
    "phone": "必填",
    "role": "必填,user/admin"
}
返回参数：
{
    "code": 200,
    "msg": "添加成功"
}
```

3. 编辑用户 `POST /api/admin/user/update`
4. 删除用户 `POST /api/admin/user/delete`
5. 重置密码 `POST /api/admin/user/reset-password`
6. 用户详情 `POST /api/admin/user/detail`

## 2. 商品管理模块
页面：商品管理
功能：
1. 商品列表 `POST /api/admin/product/list`
```json
请求参数：
{
    "page": 1,
    "size": 10,
    "type": "可选"
}
返回参数：
{
    "code": 200,
    "msg": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "name": "大米",
                "type": "粮食"
            }
        ],
        "total": 50
    }
}
```

2. 新增商品 `POST /api/admin/product/add`
3. 编辑商品 `POST /api/admin/product/update`
4. 商品详情 `POST /api/admin/product/detail`

## 3. 商家管理模块
页面：商家管理
功能：
1. 商家列表 `POST /api/admin/merchant/list`
```json
请求参数：
{
    "page": 1,
    "size": 10,
    "name": "可选",
    "phone": "可选"
}
返回参数：
{
    "code": 200,
    "msg": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "name": "XX粮油",
                "phone": "13800138000"
            }
        ],
        "total": 30
    }
}
```

2. 新增商家 `POST /api/admin/merchant/add`
3. 编辑商家 `POST /api/admin/merchant/update`
4. 删除商家 `POST /api/admin/merchant/delete`
5. 商家详情 `POST /api/admin/merchant/detail`

## 4. 商品上架管理模块
页面：商品上架管理
功能：
1. 添加商品到商家 `POST /api/admin/product-merchant/add`
```json
请求参数：
{
    "productId": 1,
    "merchantId": 1,
    "currentPrice": 5.5
}
返回参数：
{
    "code": 200,
    "msg": "添加成功"
}
```

2. 移除商品 `POST /api/admin/product-merchant/remove`
3. 更新状态 `POST /api/admin/product-merchant/status`
4. 商家商品列表 `POST /api/admin/product-merchant/product-list`
5. 商品商家列表 `POST /api/admin/product-merchant/merchant-list`

## 5. 价格管理模块
页面：价格管理
功能：
1. 添加价格记录 `POST /api/admin/price/add`
```json
请求参数：
{
    "productId": 1,
    "price": 5.5,
    "recordTime": 1648888888000
}
返回参数：
{
    "code": 200,
    "msg": "添加成功"
}
```

2. 价格历史列表 `POST /api/admin/price/list`
3. 价格趋势 `POST /api/admin/price/trend`
4. 价格预测 `POST /api/admin/price/predict`

## 6. 订单管理模块
页面：订单管理
功能：
1. 订单列表 `POST /api/admin/order/list`
```json
请求参数：
{
    "page": 1,
    "size": 10
}
返回参数：
{
    "code": 200,
    "msg": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "userId": 1,
                "productId": 1,
                "merchantId": 1,
                "quantity": 100,
                "price": 5.5,
                "totalAmount": 550,
                "createTime": "2024-03-20 10:00:00"
            }
        ],
        "total": 200
    }
}
```

2. 订单详情 `POST /api/admin/order/detail`
3. 删除订单 `POST /api/admin/order/delete`
4. 新增订单 `POST /api/admin/order/add`
5. 批量添加订单 `POST /api/admin/order/batch-add`

## 7. 公告管理模块
页面：公告管理
功能：
1. 新增公告 `POST /api/admin/announcement/add`
```json
请求参数：
{
    "title": "必填",
    "content": "必填"
}
返回参数：
{
    "code": 200,
    "msg": "发布成功"
}
```

2. 删除公告 `POST /api/admin/announcement/delete`
3. 修改公告 `POST /api/admin/announcement/update`
4. 置顶公告 `POST /api/admin/announcement/top`

## 8. 统计分析模块
页面：数据统计
功能：
1. 平台概览 `GET /api/statistics/overview`
```json
返回参数：
{
    "code": 200,
    "msg": "success",
    "data": {
        "totalVolume": 10000,
        "totalAmount": 550000.00,
        "todayVolume": 100,
        "todayAmount": 5500.00
    }
}
```

2. 成交趋势 `POST /api/statistics/trend`
3. 品类排行 `POST /api/statistics/type/rank`
4. 商户排行 `POST /api/statistics/type/merchant-rank`

## 权限控制
1. 登录认证：所有接口需要在header中携带token
```json
headers: {
    "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxx"
}
```

2. 角色权限：
- admin：可访问所有接口
- user：仅可访问部分查询接口

## 错误码说明
```json
{
    "200": "成功",
    "401": "未登录或token已过期",
    "403": "无权限",
    "500": "服务器内部错误"
}
```

## 注意事项
1. 所有列表接口均支持分页
2. 时间戳统一使用毫秒级
3. 金额统一使用元为单位，保留两位小数
4. 删除操作建议使用软删除
5. 重要操作需要添加操作日志 