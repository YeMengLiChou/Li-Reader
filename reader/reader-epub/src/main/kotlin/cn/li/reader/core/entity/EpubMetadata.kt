package cn.li.reader.core.entity

import cn.li.reader.core.entity.base.Meta

class EpubMetadata(
    val identifiers: List<Identifier>,
    val titles: List<Title>,
    val languages: List<String>,
) {
    private val mContributors = arrayListOf<Contributor>()
    private val mAuthors = arrayListOf<Author>()
    private var mMetas: HashMap<String, Meta>? = null
    // 虽然元素可重复，但是只能有一个
    private var mPublishDate: String? = null
    private var mSource: String? = null
    private var mType: String? = null
    // 文档未说明，因此跟随元素可重复
    private var mCoverages: ArrayList<String>? = null
    private var mDescription: ArrayList<String>? = null
    private var mFormats: ArrayList<String>? = null
    private var mPublishers: ArrayList<String>? = null
    private var mRelations: ArrayList<String>? = null
    private var mRights: ArrayList<String>? = null
    private var mSubjects: ArrayList<String>? = null

    fun addContributor(contributor: Contributor) {
        mContributors.add(contributor)
    }

    fun getContributors() = mContributors.toList()

    fun addAuthor(creator: Author) {
        mAuthors.add(creator)
    }

    fun getAuthors() = mAuthors.toList()

    fun getProperty(property: String) = mMetas?.get(property)?.value

    fun setMeta(meta: HashMap<String, Meta>) {
        mMetas = meta
    }

    fun setPublishDate(date: String) {
        mPublishDate = date
    }

    fun getPublishDate() = mPublishDate

    fun getPublishDateLong() {
        //
    }

    fun setSource(source: String) {
        mSource = source
    }

    fun getSource() = mSource

    fun setType(type: String) {
        mType = type
    }

    fun getType() = mType

    fun addCoverage(coverage: String) {
        if (mCoverages == null) mCoverages = arrayListOf()
        mCoverages!!.add(coverage)
    }

    fun getCoverages() = mCoverages?.toList() ?: emptyList()

    fun addDescription(desc: String) {
        if (mDescription == null) mDescription = arrayListOf()
        mDescription!!.add(desc)
    }

    fun addFormats(format: String) {
        if (mFormats == null) mFormats = arrayListOf()
        mFormats!!.add(format)
    }

    fun getFormats() = mFormats?.toList() ?: emptyList()

    fun addPublisher(publisher: String) {
        if (mPublishers == null) mPublishers = arrayListOf()
        mPublishers!!.add(publisher)
    }

    fun getPublishers() = mPublishers?.toList() ?: emptyList()

    fun addRelation(relation: String) {
        if (mRelations == null) mRelations = arrayListOf()
        mRelations!!.add(relation)
    }

    fun getRelations() = mRelations?.toList() ?: emptyList()

    fun addRights(rights: String) {
        if (mRights == null) mRights = arrayListOf()
        mRights!!.add(rights)
    }

    fun getRights() = mRights?.toList() ?: emptyList()

    fun addSubject(subject: String) {
        if (mSubjects == null) mSubjects = arrayListOf()
        mSubjects!!.add(subject)
    }

    fun getSubjects() = mSubjects?.toList() ?: emptyList()


}
