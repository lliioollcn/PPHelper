package cn.lliiooll.pphelper.hook

object RemoveLiveHook : BaseHook("remove_live", "去除直播") {
    override fun init(): Boolean {
        this.desc = "启用后将关闭皮皮搞笑的直播"
        return true
    }
}