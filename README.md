# PicaComic API

基于 Ktorfit 的 PicACG Kotlin Multiplatform API client 库，覆盖账号、内容、互动及相关网络能力。

支持当前工程配置的 Android、JVM、JS、Wasm JS、Linux、Windows、macOS、iOS、watchOS、
tvOS 和 Android Native target。库只依赖 Ktor client core，实际 HTTP engine 由使用者提供。

## 功能

- 提供 `PicaComicClient`，同时保留公开的底层 Ktorfit 接口，便于使用者按需自行封装。
- 统一处理请求签名、请求头、响应封装、错误、凭证状态、重试和备用主机切换。
- 登录成功后自动保存 token；可在构造时传入 token，也可随时读取、替换或清除。
- 包含注册、用户、漫画、章节、评论、游戏、推荐、动漫、Smart DNS、启动配置、版本检查和
  广告信息等 API 能力及其数据模型。
- 实现 API 协议要求的 query 编码规则，包括空格使用 `%20` 而不是 Ktor 默认的 `+`。
- 提供普通、分类及 RequestMode 图片 URL 构造工具。

应用层 UI、任务调度、媒体播放与下载等功能不在本库范围内。
Smart DNS 的解析接口可以调用，但“指定 IP 且保留域名 SNI”的 engine 注入不是所有 Ktorfit
平台都支持，需由使用者在所选 Ktor engine 中配置。浏览器和 Wasm 还会受到运行环境 CORS 限制。

## 命名与模型契约

公开的 Kotlin 参数和属性使用业务语义命名；协议字段只保留在映射注解中。例如
`@Query("ac") action`、`@SerialName("_id") id`、`@SerialName("epsCount") episodeCount`。
模型的必填性、可空性和默认值与 API 协议契约一致，不会把必填字段任意改成可空；缺少必填字段时会正常抛出序列化异常。

## 创建 client

消费方需要添加适合目标平台的 Ktor engine。以下以 JVM CIO 为例：

```kotlin
val ktorClient = HttpClient(CIO)
val api = PicaComicClient(ktorClient)

val login = api.signIn("account", "password")
println(login.token)

// 登录后的所有 PicA 请求会自动携带该 token。
val profile = api.getProfile()
val exportedToken = api.credential()

api.signOut()
api.close()
ktorClient.close()
```

构造时已有 token：

```kotlin
val api = PicaComicClient(
    httpClient = ktorClient,
    initialCredential = savedToken,
    identity = PicaIdentity(
        nonce = savedNonce,
        applicationUuid = savedApplicationUuid,
    ),
)
```

`nonce` 和 `applicationUuid` 应按安装持久化。未提供时会为当前 client 实例生成新值。

## 自定义凭证持久化

实现 `PicaSession` 可把 token、nonce 和 UUID 接入任意跨平台存储：

```kotlin
class StoredSession : PicaSession {
    override val identity = PicaIdentity(loadNonce(), loadUuid())

    override suspend fun credential(): String? = loadToken()

    override suspend fun updateCredential(credential: String?) {
        saveToken(credential)
    }
}

val api = PicaComicClient(ktorClient, session = StoredSession())
```

`signIn()` 只在响应通过 PicA Envelope 校验后写入 token。HTTP 401 会调用
`updateCredential(null)`。`credential()`、`updateCredential()` 和 `signOut()` 分别用于导出、替换和
清除登录凭证。

## RequestMode 与图片

```kotlin
val api = PicaComicClient(
    ktorClient,
    configuration = PicaComicClientConfig(
        requestMode = RequestMode.PROXY1_DIRECT,
        imageQuality = ImageQuality.ORIGINAL,
    ),
)

val normalUrl = imageInfo.url()
val routedUrl = imageInfo.routedUrl(api.configuration.requestMode.imageRootDomain)
```

`OFFICIAL_ENHANCED` 与 `OFFICIAL_DIRECT` 的 URL Host 相同；两者的区别是调用方是否给 engine
安装 Smart DNS 解析策略。

## 验证

```shell
./gradlew jvmTest
```

Windows 使用 `gradlew.bat`。测试覆盖请求签名与编码、登录状态、凭证失效、重试、备用主机和
相关组合请求流程。

## License

Apache License 2.0.
