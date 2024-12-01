package com.example.lab4kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputVoltage = findViewById<EditText>(R.id.inputVoltage)
        val inputShortCircuitCurrent = findViewById<EditText>(R.id.inputShortCircuitCurrent)
        val inputTransformerPower = findViewById<EditText>(R.id.inputTransformerPower)
        val inputLoadPower = findViewById<EditText>(R.id.inputLoadPower)
        val inputOperatingTime = findViewById<EditText>(R.id.inputOperatingTime)
        val inputFaultTime = findViewById<EditText>(R.id.inputFaultTime)
        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val outputResult = findViewById<TextView>(R.id.outputResult)
        val buttonToSecondActivity = findViewById<Button>(R.id.buttonToSecondActivity)
        buttonToSecondActivity.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
        val buttonToThirdActivity = findViewById<Button>(R.id.buttonToThirdActivity)
        buttonToThirdActivity.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

        calculateButton.setOnClickListener {
            val voltage = inputVoltage.text.toString().toDoubleOrNull() ?: 0.0
            val shortCircuitCurrent = inputShortCircuitCurrent.text.toString().toDoubleOrNull() ?: 0.0
            val transformerPower = inputTransformerPower.text.toString().toDoubleOrNull() ?: 0.0
            val loadPower = inputLoadPower.text.toString().toDoubleOrNull() ?: 0.0
            val operatingTime = inputOperatingTime.text.toString().toDoubleOrNull() ?: 0.0
            val faultTime = inputFaultTime.text.toString().toDoubleOrNull() ?: 0.0

            // Розрахунок номінального струму
            val nominalCurrent = calculateNominalCurrent(loadPower, voltage)

            // Розрахунок термічної стійкості
            val thermalStability = calculateThermalStability(shortCircuitCurrent, faultTime)

            // Розрахунок економічного перерізу
            val economicCrossSection = calculateEconomicCrossSection(nominalCurrent)

            // Розрахунок мінімального перерізу для термічної стійкості
            val minimalCrossSection = calculateMinimalCrossSection(shortCircuitCurrent, faultTime)

            // Вибір кабелю
            val cableRecommendation = selectCable(economicCrossSection, minimalCrossSection)

            // Виведення результату
            outputResult.text = """
                Номінальний струм: ${String.format("%.2f", nominalCurrent)} А
                Термічна стійкість: ${String.format("%.2f", thermalStability)} Дж
                Економічний переріз: ${String.format("%.2f", economicCrossSection)} мм²
                Мінімальний переріз для термічної стійкості: ${String.format("%.2f", minimalCrossSection)} мм²
                Обраний кабель: ${cableRecommendation}
            """.trimIndent()
        }
    }

    // Метод для розрахунку номінального струму навантаження
    private fun calculateNominalCurrent(load: Double, voltage: Double): Double {
        return if (voltage != 0.0) (2 * load) / (sqrt(3.0) * voltage) else 0.0
    }

    // Метод для розрахунку термічної стійкості (енергія КЗ)
    private fun calculateThermalStability(current: Double, time: Double): Double {
        return current * current * time
    }

    // Метод для розрахунку економічного перерізу
    private fun calculateEconomicCrossSection(current: Double): Double {
        val currentDensity = 1.4 // Економічна густина струму в А/мм²
        return current / currentDensity
    }

    // Метод для розрахунку мінімального перерізу для термічної стійкості
    private fun calculateMinimalCrossSection(current: Double, time: Double): Double {
        val thermalConstant = 92.0 // Термічний коефіцієнт для алюмінієвих жил
        return (current * sqrt(time)) / thermalConstant
    }

    // Метод для вибору кабелю
    private fun selectCable(economicCrossSection: Double, minimalCrossSection: Double): String {
        val recommendedSection = if (minimalCrossSection > economicCrossSection) minimalCrossSection else economicCrossSection
        return if (recommendedSection <= 25) {
            "ААБ 10 3×25, але рекомендовано переріз 50 мм²"
        } else {
            "ААБ 10 3×50"
        }
    }
}
