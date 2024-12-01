package com.example.lab4kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.exp
import kotlin.math.sqrt

class ThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        val inputVoltage = findViewById<EditText>(R.id.inputVoltage)
        val inputPowerSC = findViewById<EditText>(R.id.inputPowerSC)
        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val backToFirstActivityButton = findViewById<Button>(R.id.backToFirstActivityButton)
        val outputResult = findViewById<TextView>(R.id.outputResult)

        calculateButton.setOnClickListener {
            val voltage = inputVoltage.text.toString().toDoubleOrNull() ?: 0.0
            val powerSC = inputPowerSC.text.toString().toDoubleOrNull() ?: 0.0

            val impedanceXC = calculateImpedance(35.0, 200.0)
            val impedanceXT = calculateImpedance(7.5, 100.0)
            val impedanceXM = calculateImpedance(0.08, 1000.0)
            val impedanceXD = calculateImpedance(2.38, 1000.0)
            val totalImpedance = impedanceXC + impedanceXT + impedanceXM + impedanceXD

            val baseCurrent = calculateBaseCurrent(powerSC, voltage)
            val shortCircuitCurrent = calculateShortCircuitCurrent(baseCurrent, totalImpedance)
            val impactCurrent = calculateImpactCurrent(shortCircuitCurrent)

            outputResult.text = """
                Початкове діюче значення струму КЗ: ${String.format("%.2f", shortCircuitCurrent)} кА
                Амплітудне значення ударного струму: ${String.format("%.2f", impactCurrent)} кА
            """.trimIndent()
        }

        backToFirstActivityButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun calculateImpedance(voltage: Double, power: Double): Double {
        return (voltage * voltage) / power
    }

    private fun calculateBaseCurrent(powerSC: Double, voltage: Double): Double {
        return if (voltage != 0.0) (powerSC * 1_000) / (sqrt(3.0) * voltage) else 0.0
    }

    private fun calculateShortCircuitCurrent(baseCurrent: Double, totalImpedance: Double): Double {
        return if (totalImpedance != 0.0) baseCurrent / totalImpedance else 0.0
    }

    private fun calculateImpactCurrent(shortCircuitCurrent: Double): Double {
        val exponentFactor = exp(-0.05)
        return shortCircuitCurrent * sqrt(2.0) * (1 + exponentFactor)
    }
}
