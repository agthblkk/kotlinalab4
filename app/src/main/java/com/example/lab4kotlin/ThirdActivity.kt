package com.example.lab4kotlin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class ThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        val inputVoltage = findViewById<EditText>(R.id.inputVoltage)
        val inputImpedanceR = findViewById<EditText>(R.id.inputImpedanceR)
        val inputImpedanceX = findViewById<EditText>(R.id.inputImpedanceX)
        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val backToFirstActivityButton = findViewById<Button>(R.id.backToFirstActivityButton)
        val outputResult = findViewById<TextView>(R.id.outputResult)

        calculateButton.setOnClickListener {
            val voltage = inputVoltage.text.toString().toDoubleOrNull() ?: 0.0
            val impedanceR = inputImpedanceR.text.toString().toDoubleOrNull() ?: 0.0
            val impedanceX = inputImpedanceX.text.toString().toDoubleOrNull() ?: 0.0

            // Розрахунок повного опору для трьох режимів
            val impedanceNormal = calculateTotalImpedance(impedanceR, impedanceX)
            val impedanceMinimal = calculateTotalImpedance(impedanceR * 1.5, impedanceX * 1.5) // Збільшення для мінімального режиму
            val impedanceEmergency = calculateTotalImpedance(impedanceR * 2.0, impedanceX * 2.0) // Збільшення для аварійного режиму

            // Розрахунок струмів КЗ для трьох режимів
            val currentNormal = calculateShortCircuitCurrent(voltage, impedanceNormal)
            val currentMinimal = calculateShortCircuitCurrent(voltage, impedanceMinimal)
            val currentEmergency = calculateShortCircuitCurrent(voltage, impedanceEmergency)

            outputResult.text = """
                Нормальний режим: ${String.format("%.2f", currentNormal)} кА
                Мінімальний режим: ${String.format("%.2f", currentMinimal)} кА
                Аварійний режим: ${String.format("%.2f", currentEmergency)} кА
            """.trimIndent()
        }

        backToFirstActivityButton.setOnClickListener {
            finish()
        }
    }

    private fun calculateTotalImpedance(impedanceR: Double, impedanceX: Double): Double {
        return sqrt(impedanceR * impedanceR + impedanceX * impedanceX)
    }

    private fun calculateShortCircuitCurrent(voltage: Double, impedance: Double): Double {
        return if (impedance != 0.0) (voltage) / (sqrt(3.0) * impedance) else 0.0
    }
}
