# Pica Comic API
Call `picacomic` API in Kotlin.

# Usage
Gradle
```groovy
//TODO
```

Recommend HttpEngine is CIO, see all available engines below

https://ktor.io/clients/http-client/engines.html

Example:
```kotlin
val picaComicClient = PicaComicClient(CIO)
picaComicClient.signIn("email", "password")

//get title of categories(嗶咔漢化, 全彩, 長篇 etc)
val categories = picaComicClient.category.get().data.map { it.title }

//first comic in category "全彩"
val firstComic = picaComicClient.comic
                    .search(category = PredefinedCategory.全彩.name)
                    .data.docs.first().comicId
//episodes of a comic
val episodes = picaComicClient.comic.getAllEpisodes(firstComic)
//all pages in first episode
val pages = picaComicClient.comic.getAllPages(firstComic, 1)
//url of first page in episode
val url = pages.first().media.urlString

picaComicClient.close()
```

How to use other HttpEngine and use proxy?
```kotlin
val picaComicClient = PicaComicClient(Apache) {
    engine {
        customizeClient {
            setProxy("proxyHost", 8080)
        }
    }   
}
```

How to download image(s)?
```kotlin
val imageFile = File("001.jpg")
HttpClient(CIO).use {
    get<ByteReadChannel>(url).copyAndClose(imageFile.writeChannel())
}
```

# License
Apache 2.0
