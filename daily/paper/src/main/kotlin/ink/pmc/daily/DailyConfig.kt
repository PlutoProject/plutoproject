package ink.pmc.daily

import com.electronwill.nightconfig.core.Config

@Suppress("UNUSED")
class DailyConfig(config: Config): Config by config  {

    val afterSigned: List<String> get() = get("after-signed")

}