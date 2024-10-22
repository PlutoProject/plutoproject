package ink.pmc.hypervisor

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction

data class Double2IntCurve(
    val sample: Double2IntSample,
    val function: PolynomialFunction
) {
    fun getHighestPoint(): Double2IntPoint {
        return sample.maxByOrNull { it.first } ?: error("No point on function")
    }
}