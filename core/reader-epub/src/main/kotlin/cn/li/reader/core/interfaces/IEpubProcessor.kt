package cn.li.reader.core.interfaces

import cn.li.reader.core.entity.EpubBook

/**
 *
 *
 * @author Grimrise 2024/9/23
 */
interface IEpubProcessor {


    fun process(book: EpubBook)
}