package ink.pmc.member.api

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text

val STATUS_WHITELISTED = component { 
    text("已发放")
}

val STATUS_NON_WHITELISTED = component { 
    text("未发放")
}

val STATUS_WHITELISTED_BEFORE = component { 
    text("曾发放过，但被移除了")
}

val OFFICIAL_AUTH = component { 
    text("正版账号")
}

val LITTLESKIN_AUTH = component { 
    text("LittleSkin 皮肤站")
}

val BEDROCK_ONLY_AUTH = component { 
    text("仅基岩版")
}

val NONE_AUTH = component { 
    text("无")
}