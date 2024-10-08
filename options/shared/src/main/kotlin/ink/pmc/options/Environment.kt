package ink.pmc.options

import kotlinx.coroutines.Job
import java.util.logging.Logger

lateinit var logger: Logger
lateinit var cleanerJob: Job

fun stopCleanerJob() {
    cleanerJob.cancel()
}