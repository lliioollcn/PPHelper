package cn.lliiooll.pphelper.hook

object CustomVoiceHook : BaseHook("custom_voice", "自定义评论区语音") {
    override fun init(): Boolean {
        this.desc = "启用后将显示一个悬浮窗，在帖子界面点击悬浮窗使用语音并发送（未完成）"
        return true
    }
}