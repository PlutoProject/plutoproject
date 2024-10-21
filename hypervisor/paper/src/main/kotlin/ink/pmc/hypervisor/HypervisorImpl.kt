package ink.pmc.hypervisor

import ink.pmc.utils.inject.inlinedGet

class HypervisorImpl : Hypervisor, StatisticProvider by inlinedGet<StatisticProvider>()