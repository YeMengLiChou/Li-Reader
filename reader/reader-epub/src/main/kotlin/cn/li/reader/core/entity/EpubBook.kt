package cn.li.reader.core.entity

import cn.li.reader.core.consts.MetaProperty
import cn.li.reader.core.entity.base.EpubResource

class EpubBook(
    var path: String,
) {

    lateinit var version: String
        internal set
    lateinit var uniqueIdentifierScheme: String
        internal set
    var prefix: String? = null
        internal set
    lateinit var metadata: EpubMetadata
        internal set
    lateinit var resources: EpubResources
        internal set
    lateinit var spine: Spine
        internal set
    lateinit var containerDocument: EpubResource
    lateinit var packageDocument: EpubResource
    lateinit var navigationDocument: EpubResource
    var uniqueIdentifier: String? = null
        private set
    var lastModifiedDate: String? = null
        private set

    fun setMetadata(metadata: EpubMetadata) {
        this.metadata = metadata
        uniqueIdentifier = metadata.identifiers
            .find { it.id == uniqueIdentifierScheme }
            ?.apply { unique = true }
            ?.value
        lastModifiedDate = metadata.getProperty(MetaProperty.MODIFIED)
    }

    fun isEpubVersion3x() = version.startsWith("3.")


    override fun toString(): String {
        return """
            EpubBook(
                path=$path,
                version=$version,
                uniqueIdentifierScheme=$uniqueIdentifierScheme,
                prefix=$prefix,
                metadata=$metadata,
                resources=$resources,
                spine=$spine,
                containerDocument=$containerDocument,
                packageDocument=$packageDocument,
                navigationDocument=navigationDocument,
                uniqueIdentifier=$uniqueIdentifier,
                mLastModifiedDate=$lastModifiedDate,
            )    
            """.trimIndent()
    }

}



