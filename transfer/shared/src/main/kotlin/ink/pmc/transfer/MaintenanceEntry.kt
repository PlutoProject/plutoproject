package ink.pmc.transfer

data class MaintenanceEntry(
    val id: String,
    val enabledAt: Long,
    val globalMaintenance: Boolean = false
)