package cn.li.reader.core

import cn.li.reader.core.parse.EpubBookProcessor

/**
 *
 *
 * @author Grimrise 2024/9/25
 */
fun main() {
    EpubBookProcessor("/Users/bytedance/Ebooks/《你怎样过一天，就怎样过一生》梁爽.epub").process()
        ?.let {
            println(it.navigationDocument)
        }

}