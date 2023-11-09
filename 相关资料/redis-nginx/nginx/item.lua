-- 导入common函数库
local common = require('common')
local http_get = common.http_get
local read_redis = common.read_redis

-- 导入cJson库
local cJson = require('cjson')

-- 获取本地缓存
local item_cache = ngx.shared.item_cache

-- 先查redis，未命中则查询tomcat
local function read_data(key, expire, path, params)
    local resp = item_cache:get(key)
    if not resp then
        ngx.log(ngx.ERR, "查询本地缓存失败，key = " , key)
        resp = read_redis("127.0.0.1", 6379, key)
        if not resp then
            resp = http_get(path, params)
        end
    end
    -- 写入本地缓存
    item_cache:set(key, resp, expire)
    return resp
end

-- 获取路径参数
local id = ngx.var[1]

-- 查询商品信息
local itemJson = read_data("item:id:" .. id, 1000, "/item/" .. id, nil)

-- 查询库存信息
local stockJson = read_data("item:stock:id:" .. id, 60, "/item/stock/" .. id, nil)

-- JSON转化为lua的table结构
local item = cJson.decode(itemJson)
local stock = cJson.decode(stockJson)

-- 组合数据
item.stock = stock.stock
item.sold = stock.sold


-- 返回结果
ngx.say(cJson.encode(itemJson))