package cn.li.reader.core.exception

/**
 * Epub 解析异常
 *
 * @author Grimrise 2024/9/23
 */
class EpubParseException(
    message: String,
    cause: Throwable? = null
): RuntimeException(message, cause) {

    companion object {
        /**
         * META-INF/container.xml 文件中没有 `full-path` 属性
         * */
        const val ERROR_NOT_FULL_PATH = 1

        /**
         * EPUB 文件格式不正确 payload 为何处不正确
         * */
        const val ERROR_INCORRECT_EPUB_FORMAT = 2

        /**
         * PackageDocument 缺失 `package` 元素, payload 是路径
         * */
        const val ERROR_NO_PACKAGE_ELEMENT = 3

        /**
         * 解析异常，payload 为解析失败的元素 line
         * */
        const val ERROR_PARSE = 4

        const val ERROR_UNKNOWN = -1


        fun from(
            type: Int,
            payload: String = "",
            throwable: Throwable? = null
        ): EpubParseException {
            return when(type) {
                ERROR_NOT_FULL_PATH -> EpubParseException( "There is no 'full-path' attribute in the `META-INF/container.xml`!", throwable)
                ERROR_INCORRECT_EPUB_FORMAT -> EpubParseException("The file format is incorrect! Caused by $payload", throwable)
                ERROR_NO_PACKAGE_ELEMENT -> EpubParseException("The `package` element is missing in the `$payload`!", throwable)
                ERROR_PARSE -> EpubParseException("Parse failed when resolving `$payload`", throwable)
                else -> EpubParseException("Unknown error type: $type $payload", throwable)
            }
        }
    }


}